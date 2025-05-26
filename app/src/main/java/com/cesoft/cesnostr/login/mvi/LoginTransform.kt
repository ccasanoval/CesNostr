package com.cesoft.cesnostr.login.mvi

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.cesoft.domain.entity.NostrMetadata

internal object LoginTransform {

//    data object GoReload : ViewTransform<LoginState, LoginSideEffect>() {
//        override fun mutate(currentState: LoginState): LoginState {
//            return LoginState.Loading
//        }
//    }

    data class GoSignInSuccess(
        val metadata: NostrMetadata? = null,
        val error: Throwable? = null,
    ) : ViewTransform<LoginState, LoginSideEffect>() {
        override fun mutate(currentState: LoginState): LoginState {
            return LoginState.SignInSuccess(
                metadata = metadata,
            )
        }
    }

    data class GoInit(
        val error: Throwable? = null,
    ) : ViewTransform<LoginState, LoginSideEffect>() {
        override fun mutate(currentState: LoginState): LoginState {
            return LoginState.Init(
                error = error
            )
        }
    }

    data class AddSideEffect(
        val sideEffect: LoginSideEffect
    ) : SideEffectTransform<LoginState, LoginSideEffect>() {
        override fun mutate(sideEffects: SideEffects<LoginSideEffect>): SideEffects<LoginSideEffect> {
            return sideEffects.add(sideEffect)
        }
    }
}