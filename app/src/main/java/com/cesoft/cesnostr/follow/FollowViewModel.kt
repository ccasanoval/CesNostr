package com.cesoft.cesnostr.follow

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.follow.vmi.FollowIntent
import com.cesoft.cesnostr.follow.vmi.FollowSideEffect
import com.cesoft.cesnostr.follow.vmi.FollowState
import com.cesoft.cesnostr.follow.vmi.FollowTransform
import com.cesoft.cesnostr.view.Page
import com.cesoft.data.toMetadata
import com.cesoft.domain.AppError.NotKnownError
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKindStandard
import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.usecase.FetchEventsUC
import com.cesoft.domain.usecase.SendFollowListUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val fetchEvents: FetchEventsUC,
    private val sendFollowList: SendFollowListUC,
): ViewModel(), MviHost<FollowIntent, State<FollowState, FollowSideEffect>> {
    private val reducer: Reducer<FollowIntent, State<FollowState, FollowSideEffect>> = Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = Dispatchers.Default,
        initialInnerState = FollowState.Loading,
        logger = null,
        intentExecutor = this::executeIntent
    )
    override val state = reducer.state
    override fun execute(intent: FollowIntent) {
        reducer.executeIntent(intent)
    }
    private fun executeIntent(intent: FollowIntent) =
        when(intent) {
            FollowIntent.Close -> executeClose()
            FollowIntent.Load -> executeLoad()
            FollowIntent.Reload -> executeReload()
            //is FollowIntent.Author -> executeAuthor(intent.npub)
        }

    private fun executeClose() = flow {
        emit(FollowTransform.AddSideEffect(FollowSideEffect.Close))
    }
    private fun executeReload() = flow {
        emit(FollowTransform.GoReload)
    }
//    private fun executeAuthor(npub: String) = flow {
//        emit(FollowTransform.AddSideEffect(FollowSideEffect.Author(npub)))
//    }
    private fun executeLoad() = flow {
        emit(fetch())
    }

    private suspend fun fetch(): FollowTransform.GoInit {
        android.util.Log.e(TAG, "fetch------- ----------- 0000")
        //TODO: TESTING
        /*val r1: Result<Unit> = sendFollowList(
            listOf("npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv")
        )
        if(r1.isSuccess) {
            android.util.Log.e(TAG, "fetch------- CONTACT_LIST OK -----------")
        }*/

        val res: Result<List<NostrEvent>> = fetchEvents(NostrKindStandard.CONTACT_LIST) //FOLLOW_SET) //CONTACT_LIST)
        return if(res.isSuccess) {
            val list = res.getOrNull()?.map { e: NostrEvent -> e.toMetadata(e.npub) }
            if(list != null && list.isNotEmpty()) {
                android.util.Log.e(TAG, "fetch------- CONTACT_LIST OK : ${list.size} -----------")
                FollowTransform.GoInit(list = list)
            }
            else {
                android.util.Log.e(TAG, "fetch------- $list ----------- 0000")
                FollowTransform.GoInit()
            }
        }
        else {
            val error = res.exceptionOrNull() ?: NotKnownError
            android.util.Log.e(TAG, "fetch:e:--------------- $error")
            FollowTransform.GoInit(error = error)
        }
    }

    fun consumeSideEffect(
        sideEffect: FollowSideEffect,
        navController: NavController,
        context: Context
    ) {
        when(sideEffect) {
            FollowSideEffect.Start -> {
                navController.navigate(Page.Home.route)
            }
            FollowSideEffect.Close -> {
                (context as Activity).finish()
            }
//            is FollowSideEffect.Author -> {
//                navController.navigate(Page.Author.createRoute(sideEffect.npub))
//            }
        }
    }

    companion object {
        private const val TAG = "HomeVM"
    }
}