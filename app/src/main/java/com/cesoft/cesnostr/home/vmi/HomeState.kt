package com.cesoft.cesnostr.home.vmi

import com.adidas.mvi.LoggableState
import rust.nostr.sdk.Event
import rust.nostr.sdk.Metadata

sealed class HomeState: LoggableState {
    data object Loading: HomeState()
    data class Init(
        val events: List<Event> = listOf(),
        val metadata: Map<String, Metadata> = mapOf(),
        val wait: Boolean = false,
        val error: Throwable? = null
    ): HomeState()
}