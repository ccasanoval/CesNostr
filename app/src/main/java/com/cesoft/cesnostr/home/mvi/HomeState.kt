package com.cesoft.cesnostr.home.mvi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.entity.NostrEvent

sealed class HomeState: LoggableState {
    data object Loading: HomeState()
    data class Init(
        val events: List<NostrEvent> = listOf(),
        val wait: Boolean = false,
        val error: Throwable? = null
    ): HomeState()
}