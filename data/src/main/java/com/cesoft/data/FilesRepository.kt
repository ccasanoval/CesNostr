package com.cesoft.data

import android.content.Context
import com.cesoft.domain.repo.FilesRepositoryContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import qrcode.QRCode
import qrcode.color.Colors
import qrcode.render.QRCodeGraphics
import java.io.File
import javax.inject.Inject

class FilesRepository @Inject constructor(
    private val context: Context,
): FilesRepositoryContract {
    override suspend fun createQrImage(
        value: String,
        size: Int,
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val qr = QRCode.ofRoundedSquares()
                .withColor(Colors.DEEP_PINK) // Default is Colors.BLACK
                .withSize(size) // Default is 25
                .build(value)
            val pngBytes: QRCodeGraphics = qr.render()
            val filename = "$value.png"
            val file = File(context.filesDir, filename)
            file.delete()
            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(pngBytes.getBytes())
                it.close()
            }
            return@withContext Result.success(file.absolutePath)
        }
        catch(e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}