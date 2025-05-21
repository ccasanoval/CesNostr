package com.cesoft.cesnostr.author.vmi

sealed class AuthorSideEffect {
    data object Start: AuthorSideEffect()
    data object Close: AuthorSideEffect()
}
