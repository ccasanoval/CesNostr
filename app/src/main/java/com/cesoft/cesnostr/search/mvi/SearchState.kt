package com.cesoft.cesnostr.search.mvi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrMetadata

sealed class SearchState: LoggableState {
    data object Loading: SearchState()
    data object Init: SearchState()
    data class Result(
        val authors: List<NostrMetadata> = listOf(),
        val events: List<NostrEvent> = listOf(),
        val wait: Boolean = false,
        val error: Throwable? = null
    ): SearchState()
}