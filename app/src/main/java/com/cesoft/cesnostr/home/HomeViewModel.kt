package com.cesoft.cesnostr.home

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.home.vmi.HomeIntent
import com.cesoft.cesnostr.home.vmi.HomeSideEffect
import com.cesoft.cesnostr.home.vmi.HomeState
import com.cesoft.cesnostr.home.vmi.HomeTransform
import com.cesoft.cesnostr.view.Page
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKindStandard
import com.cesoft.domain.usecase.GetEventsUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getEvents: GetEventsUC,
): ViewModel(), MviHost<HomeIntent, State<HomeState, HomeSideEffect>> {

    private val reducer: Reducer<HomeIntent, State<HomeState, HomeSideEffect>> = Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = Dispatchers.Default,
        initialInnerState = HomeState.Loading,
        logger = null,
        intentExecutor = this::executeIntent
    )
    override val state = reducer.state
    override fun execute(intent: HomeIntent) {
        reducer.executeIntent(intent)
    }
    private fun executeIntent(intent: HomeIntent) =
        when(intent) {
            HomeIntent.Close -> executeClose()
            HomeIntent.Load -> executeLoad()
            HomeIntent.Reload -> executeReload()
        }

    private fun executeClose() = flow {
        emit(HomeTransform.AddSideEffect(HomeSideEffect.Close))
    }

    private fun executeReload() = flow {
        emit(HomeTransform.GoReload)
    }

    private fun executeLoad() = flow {
        emit(fetch())
    }

    private suspend fun fetch(): HomeTransform.GoInit {
        //TODO: List of authors to follow...
        val authList = listOf(
            "npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k",//CES
            "npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv",//BTC
        )
        val res: Result<List<NostrEvent>> = getEvents(
            kind = NostrKindStandard.TEXT_NOTE,
            authList = authList
        )
        return if(res.isSuccess) {
            val events = res.getOrNull()
            if(events != null)
                HomeTransform.GoInit(events = events)//, metadata = map)
            else
                HomeTransform.GoInit(error = UnknownError())
        }
        else {
            val error = res.exceptionOrNull() ?: UnknownError()
            android.util.Log.e(TAG, "fetch:e:--------------- $error")
            HomeTransform.GoInit(error = error)
        }
    }

    fun consumeSideEffect(
        sideEffect: HomeSideEffect,
        navController: NavController,
        context: Context
    ) {
        when(sideEffect) {
            HomeSideEffect.Start -> {
                navController.navigate(Page.Home.route)
            }
            HomeSideEffect.Close -> {
                (context as Activity).finish()
            }
        }
    }

    companion object {
        private const val TAG = "HomeVM"
    }
}