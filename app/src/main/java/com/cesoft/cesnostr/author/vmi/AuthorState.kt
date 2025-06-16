package com.cesoft.cesnostr.author.vmi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.entity.NostrMetadata

sealed class AuthorState: LoggableState {
    data object Loading: AuthorState()
    data class Init(
        val metadata: Map<String, NostrMetadata> = mapOf(),
        val wait: Boolean = false,
        val error: Throwable? = null
    ): AuthorState()
}