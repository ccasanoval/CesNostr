package com.cesoft.cesnostr.search.mvi

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.cesoft.domain.entity.NostrEvent

internal object SearchTransform {

    data object GoLoading : ViewTransform<SearchState, SearchSideEffect>() {
        override fun mutate(currentState: SearchState): SearchState {
            return SearchState.Loading()
        }
    }

    data class GoResult(
        val searchText: String,
        val events: List<NostrEvent> = listOf(),
        val error: Throwable? = null,
    ) : ViewTransform<SearchState, SearchSideEffect>() {
        override fun mutate(currentState: SearchState): SearchState {
            return SearchState.Result(
                searchText = searchText,
                events = events,
                error = error
            )
        }
    }

    data class AddSideEffect(
        val sideEffect: SearchSideEffect
    ) : SideEffectTransform<SearchState, SearchSideEffect>() {
        override fun mutate(sideEffects: SideEffects<SearchSideEffect>): SideEffects<SearchSideEffect> {
            return sideEffects.add(sideEffect)
        }
    }
}