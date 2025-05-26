package com.cesoft.cesnostr.view

import android.content.Context
import com.cesoft.cesnostr.R
import com.cesoft.domain.AppError

class Util(private var context: Context) {
    fun errorString(error: AppError): String {
        return error.message(context)
    }
}

fun Throwable.message(context: Context) = when(this) {
    is AppError.NotFound -> context.getString(R.string.error_not_found)
    is AppError.UnknownError -> context.getString(R.string.error_unknown)
    else -> "Error: $message"
}