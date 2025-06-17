package com.cesoft.cesnostr.common

import android.content.Context
import com.cesoft.domain.AppError
import com.cesoft.cesnostr.R

fun Throwable.toHumanMessage(context: Context): String {
    return when(this) {
        AppError.NotFound -> context.getString(R.string.error_not_found)
        AppError.InvalidNostrKey -> context.getString(R.string.error_nostr_key)
        AppError.InvalidMetadata -> context.getString(R.string.error_metadata)
        AppError.NotKnownError -> context.getString(R.string.error_unknown)
        else -> context.getString(R.string.error_unknown) + " : " + message
    }
}