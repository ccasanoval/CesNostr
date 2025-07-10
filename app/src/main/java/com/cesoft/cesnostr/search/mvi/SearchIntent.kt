package com.cesoft.cesnostr.search.mvi

import com.adidas.mvi.Intent

sealed class SearchIntent: Intent {
    data object Close: SearchIntent()
    data object Load: SearchIntent()
    data class Search(val searchText: String): SearchIntent()
}