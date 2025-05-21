package com.cesoft.cesnostr.author.vmi

import com.adidas.mvi.LoggableState
import rust.nostr.sdk.Event
import rust.nostr.sdk.Metadata

sealed class AuthorState: LoggableState {
    data object Loading: AuthorState()
    data class Init(
        val metadata: Map<String, Metadata> = mapOf(),
        val wait: Boolean = false,
        val error: Throwable? = null
    ): AuthorState()
}