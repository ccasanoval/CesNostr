package com.cesoft.cesnostr

import android.content.Context
import com.cesoft.domain.AppError

class Util(private var context: Context) {
    fun errorString(error: AppError): String {
        return error.message(context)
    }
}

fun Throwable.message(context: Context) = when(this) {
    is AppError.NotFound -> context.getString(R.string.error_not_found)
    else -> context.getString(R.string.error_unknown)
}