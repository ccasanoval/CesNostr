package com.cesoft.cesnostr.account.vmi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrMetadata

sealed class AccountState: LoggableState {
    data object Loading: AccountState()
    data class Init(
        val keys: NostrKeys,
        val metadata: NostrMetadata,
        val nsecImg: String,
        val npubImg: String,
        val wait: Boolean = false,
        val error: Throwable? = null
    ): AccountState()
}