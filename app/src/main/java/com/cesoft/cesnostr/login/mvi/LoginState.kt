package com.cesoft.cesnostr.login.mvi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.entity.NostrMetadata

sealed class LoginState: LoggableState {
    data class Init(
        val error: Throwable? = null
    ): LoginState()

    data class SignInSuccess(
        val metadata: NostrMetadata? = null,
    ): LoginState()
}