package com.cesoft.cesnostr.author.vmi

import com.adidas.mvi.Intent

sealed class AuthorIntent: Intent {
    data object Close: AuthorIntent()
    data object Load: AuthorIntent()
    data object Reload: AuthorIntent()
}