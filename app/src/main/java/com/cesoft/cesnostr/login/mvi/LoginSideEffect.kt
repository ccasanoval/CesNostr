package com.cesoft.cesnostr.login.mvi

sealed class LoginSideEffect {
    data object GoHome: LoginSideEffect()
}
