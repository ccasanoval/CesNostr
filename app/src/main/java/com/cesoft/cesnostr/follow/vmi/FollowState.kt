package com.cesoft.cesnostr.follow.vmi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.entity.NostrMetadata

sealed class FollowState: LoggableState {
    data object Loading: FollowState()
    data class Init(
        val list: List<NostrMetadata> = listOf(),
        val error: Throwable? = null
    ): FollowState()
}