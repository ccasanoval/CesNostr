package com.cesoft.cesnostr.login.mvi

import com.adidas.mvi.Intent

sealed class LoginIntent: Intent {
    data class SignIn(val nsec: String): LoginIntent()
    data class Create(val name: String): LoginIntent()

    data object Accept: LoginIntent()
    data object Cancel: LoginIntent()
}