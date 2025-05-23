package com.cesoft.cesnostr.login.mvi

import com.adidas.mvi.LoggableState
import rust.nostr.sdk.Metadata

sealed class LoginState: LoggableState {
    data class Init(
        val error: Throwable? = null
    ): LoginState()

    data class SignInSuccess(
        val metadata: Metadata? = null,
    ): LoginState()
}