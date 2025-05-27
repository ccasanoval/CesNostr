package com.cesoft.cesnostr.login.mvi

import com.adidas.mvi.Intent
import com.cesoft.domain.entity.NostrMetadata

sealed class LoginIntent: Intent {
    data class SignIn(val nsec: String): LoginIntent()
    data class Create(val metadata: NostrMetadata): LoginIntent()

    data object Accept: LoginIntent()
    data object Cancel: LoginIntent()
}