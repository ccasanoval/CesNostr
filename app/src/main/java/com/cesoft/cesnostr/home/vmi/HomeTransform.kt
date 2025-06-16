package com.cesoft.cesnostr.home.vmi

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.cesoft.domain.entity.NostrEvent

internal object HomeTransform {

    data object GoReload : ViewTransform<HomeState, HomeSideEffect>() {
        override fun mutate(currentState: HomeState): HomeState {
            return HomeState.Loading
        }
    }

    data class GoInit(
        val events: List<NostrEvent> = listOf(),
        val error: Throwable? = null,
    ) : ViewTransform<HomeState, HomeSideEffect>() {
        override fun mutate(currentState: HomeState): HomeState {
            return HomeState.Init(
                events = events,
                error = error
            )
        }
    }

    data class AddSideEffect(
        val sideEffect: HomeSideEffect
    ) : SideEffectTransform<HomeState, HomeSideEffect>() {
        override fun mutate(sideEffects: SideEffects<HomeSideEffect>): SideEffects<HomeSideEffect> {
            return sideEffects.add(sideEffect)
        }
    }
}