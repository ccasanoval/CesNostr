package com.cesoft.cesnostr.home.vmi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrMetadata

sealed class HomeState: LoggableState {
    data object Loading: HomeState()
    data class Init(
        val events: List<NostrEvent> = listOf(),
        val metadata: Map<String, NostrMetadata> = mapOf(),
        val wait: Boolean = false,
        val error: Throwable? = null
    ): HomeState()
}