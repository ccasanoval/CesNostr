package com.cesoft.cesnostr.account.vmi

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrMetadata

internal object AccountTransform {

    data object GoReload : ViewTransform<AccountState, AccountSideEffect>() {
        override fun mutate(currentState: AccountState): AccountState {
            return AccountState.Loading
        }
    }

    data class GoInit(
        val keys: NostrKeys = NostrKeys.Empty,
        val metadata: NostrMetadata = NostrMetadata.Empty,
        val nsecImg: String = "",
        val npubImg: String = "",
        val error: Throwable? = null,
    ) : ViewTransform<AccountState, AccountSideEffect>() {
        override fun mutate(currentState: AccountState): AccountState {
            return AccountState.Init(
                keys = keys,
                metadata = metadata,
                nsecImg = nsecImg,
                npubImg = npubImg,
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