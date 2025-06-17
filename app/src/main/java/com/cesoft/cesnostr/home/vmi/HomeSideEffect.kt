package com.cesoft.cesnostr.home.vmi

sealed class HomeSideEffect {
    data object Start: HomeSideEffect()
    data object Close: HomeSideEffect()
    data class Author(val npub: String): HomeSideEffect()
}
