package com.cesoft.cesnostr.account.vmi

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import rust.nostr.sdk.Metadata

internal object AccountTransform {

    data object GoReload : ViewTransform<AccountState, AccountSideEffect>() {
        override fun mutate(currentState: AccountState): AccountState {
            return AccountState.Loading
        }
    }

    data class GoInit(
        val metadata: Map<String, Metadata> = mapOf(),
        val error: Throwable? = null,
    ) : ViewTransform<AccountState, AccountSideEffect>() {
        override fun mutate(currentState: AccountState): AccountState {
            return AccountState.Init(
                metadata = metadata,
                error = error
            )
        }
    }

    data class AddSideEffect(
        val sideEffect: AccountSideEffect
    ) : SideEffectTransform<AccountState, AccountSideEffect>() {
        override fun mutate(sideEffects: SideEffects<AccountSideEffect>): SideEffects<AccountSideEffect> {
            return sideEffects.add(sideEffect)
        }
    }
}