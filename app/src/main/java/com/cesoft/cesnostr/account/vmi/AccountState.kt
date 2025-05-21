package com.cesoft.cesnostr.account.vmi

import com.adidas.mvi.LoggableState
import rust.nostr.sdk.Metadata

sealed class AccountState: LoggableState {
    data object Loading: AccountState()
    data class Init(
        val metadata: Map<String, Metadata> = mapOf(),
        val wait: Boolean = false,
        val error: Throwable? = null
    ): AccountState()
}