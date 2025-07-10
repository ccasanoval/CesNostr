package com.cesoft.cesnostr.search.mvi

sealed class SearchSideEffect {
    data object Start: SearchSideEffect()
    data object Close: SearchSideEffect()
    data class Author(val npub: String): SearchSideEffect()
}
