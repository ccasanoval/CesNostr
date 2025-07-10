package com.cesoft.cesnostr.search

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.search.mvi.SearchIntent
import com.cesoft.cesnostr.search.mvi.SearchSideEffect
import com.cesoft.cesnostr.search.mvi.SearchState
import com.cesoft.cesnostr.search.mvi.SearchTransform
import com.cesoft.cesnostr.view.Page
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.usecase.FetchEventsUC
import com.cesoft.domain.usecase.SearchAuthorsUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getEvents: FetchEventsUC,
    private val searchAuthors: SearchAuthorsUC,
): ViewModel(), MviHost<SearchIntent, State<SearchState, SearchSideEffect>> {

    private val reducer: Reducer<SearchIntent, State<SearchState, SearchSideEffect>> = Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = Dispatchers.Default,
        initialInnerState = SearchState.Init,
        logger = null,
        intentExecutor = this::executeIntent
    )
    override val state = reducer.state
    override fun execute(intent: SearchIntent) {
        reducer.executeIntent(intent)
    }

    private fun executeIntent(intent: SearchIntent) =
        when (intent) {
            SearchIntent.Close -> executeClose()
            SearchIntent.Load -> executeLoad()
            is SearchIntent.Search -> { executeSearch(intent.searchText) }
        }

    private fun executeClose() = flow {
        emit(SearchTransform.AddSideEffect(SearchSideEffect.Close))
    }
    private fun executeLoad() = flow {
        emit(fetch())
    }
    private fun executeSearch(searchText: String) = flow {
        emit(search(searchText))
    }

    private suspend fun fetch(): SearchTransform.GoReload {
        return SearchTransform.GoReload
    }

    private suspend fun search(searchText: String): SearchTransform.GoResult {
        val res: Result<List<NostrEvent>> = searchAuthors(searchText)
        //val resEvents: Result<List<NostrMetadata>> = searchEvents(searchText)
        if(res.isSuccess) {
            val events = res.getOrNull() ?: listOf()
            for(a in events) {
                android.util.Log.e(TAG, "search:authors:------------------------- $a")
            }
            //val events = resEvents.getOrNull() ?: listOf()
            return SearchTransform.GoResult(
                //authors = authors,
                events = events,
                error = null
            )
        }
        else {
            val failure = res.exceptionOrNull()
                //?: resEvents.exceptionOrNull()
                ?: AppError.NotKnownError
            android.util.Log.e(TAG, "search:e:------------------------- $failure")
            return SearchTransform.GoResult(error = failure)
        }
    }

    fun consumeSideEffect(
        sideEffect: SearchSideEffect,
        navController: NavController,
        context: Context
    ) {
        when(sideEffect) {
            SearchSideEffect.Start -> {
                navController.navigate(Page.Home.route)
            }
            SearchSideEffect.Close -> {
                (context as Activity).finish()
            }
            is SearchSideEffect.Author -> {
                navController.navigate(Page.Author.createRoute(sideEffect.npub))
            }
        }
    }

    companion object {
        private const val TAG = "SearchVM"
    }
}