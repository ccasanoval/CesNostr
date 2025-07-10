package com.cesoft.cesnostr.search.mvi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.entity.NostrEvent

sealed class SearchState: LoggableState {
    data object Loading: SearchState()
    data object Init: SearchState()
    data class Result(
        val searchText: String = "",
        val events: List<NostrEvent> = listOf(),
        //val wait: Boolean = false, ???? TODO: Sustitute loading state by this
        val error: Throwable? = null
    ): SearchState()
}