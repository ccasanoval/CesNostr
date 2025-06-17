package com.cesoft.cesnostr.author.vmi

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.cesoft.domain.entity.NostrMetadata

internal object AuthorTransform {

    data object GoReload : ViewTransform<AuthorState, AuthorSideEffect>() {
        override fun mutate(currentState: AuthorState): AuthorState {
            return AuthorState.Loading
        }
    }

    data class GoInit(
        val metadata: NostrMetadata = NostrMetadata.Empty,
        val error: Throwable? = null,
    ) : ViewTransform<AuthorState, AuthorSideEffect>() {
        override fun mutate(currentState: AuthorState): AuthorState {
            return AuthorState.Init(
                metadata = metadata,
                error = error
            )
        }
    }

    data class AddSideEffect(
        val sideEffect: AuthorSideEffect
    ) : SideEffectTransform<AuthorState, AuthorSideEffect>() {
        override fun mutate(sideEffects: SideEffects<AuthorSideEffect>): SideEffects<AuthorSideEffect> {
            return sideEffects.add(sideEffect)
        }
    }
}