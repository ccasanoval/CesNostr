package com.cesoft.cesnostr.home.vmi

import com.adidas.mvi.LoggableState
import rust.nostr.sdk.Event

sealed class HomeState: LoggableState {
    data object Loading: HomeState()
    data class Init(
        val events: List<Event> = listOf(),
        val wait: Boolean = false,
        val error: Throwable? = null
    ): HomeState()
}