package com.cesoft.cesnostr.follow.vmi

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.cesoft.domain.entity.NostrMetadata

internal object FollowTransform {
    data object GoReload : ViewTransform<FollowState, FollowSideEffect>() {
        override fun mutate(currentState: FollowState): FollowState {
            return FollowState.Loading
        }
    }

    data class GoInit(
        val list: List<NostrMetadata> = listOf(),
        val error: Throwable? = null,
    ) : ViewTransform<FollowState, FollowSideEffect>() {
        override fun mutate(currentState: FollowState): FollowState {
            return FollowState.Init(
                list = list,
                error = error
            )
        }
    }

    data class AddSideEffect(
        val sideEffect: FollowSideEffect
    ) : SideEffectTransform<FollowState, FollowSideEffect>() {
        override fun mutate(sideEffects: SideEffects<FollowSideEffect>): SideEffects<FollowSideEffect> {
            return sideEffects.add(sideEffect)
        }
    }
}