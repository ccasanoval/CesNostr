package com.cesoft.cesnostr.home

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.Page
import com.cesoft.cesnostr.home.vmi.HomeIntent
import com.cesoft.cesnostr.home.vmi.HomeSideEffect
import com.cesoft.cesnostr.home.vmi.HomeState
import com.cesoft.cesnostr.home.vmi.HomeTransform
import com.cesoft.domain.AppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import rust.nostr.protocol.Event
import rust.nostr.protocol.Filter
import rust.nostr.protocol.Keys
import rust.nostr.protocol.PublicKey
import rust.nostr.sdk.Client
import rust.nostr.sdk.ClientSigner
import rust.nostr.sdk.LogLevel
import rust.nostr.sdk.initLogger
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    //private val getByCounty: GetByCountyUC

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
//            is HomeIntent.ChangeAddressZipCode -> executeChangeZipCode(intent.zipCode)
        }

    private fun executeClose() = flow {
        emit(HomeTransform.AddSideEffect(HomeSideEffect.Close))
    }

//    private fun executeChangeZipCode(zipCode: String) = flow {
//        emit(fetch())
//    }

    private fun executeLoad() = flow {
        android.util.Log.e("AAA", "********************************************* LOAD")
        val state = fetch()
        emit(state)
    }

    private suspend fun fetch(): HomeTransform.GoInit {
        android.util.Log.e("AAA", "********************************************* FETCH 0")
        initLogger(LogLevel.INFO)
        val key = PublicKey.fromBech32("npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k")//CES
        val keys = Keys.fromPublicKey(key)
        android.util.Log.e("AAA", "********************************************* FETCH 1")
        val client = Client(ClientSigner.keys(keys))
        client.addRelay("wss://nostr.bitcoiner.social")
        //client.addRelay("wss://nos.lol")
        //client.addRelay("wss://nostr.mom")
        //client.addRelay("wss://relay.damus.io")
        client.connect()
        android.util.Log.e("AAA", "********************************************* FETCH 5")
        val keyBtc = PublicKey.fromBech32("npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv")
        val filter = Filter()
        filter.author(keyBtc)
        val events: List<Event> = client.getEventsOf(listOf(filter), Duration.ofMillis(1000))
        android.util.Log.e("AAA", "********************************************* EVENTS = ${events.size}")

        if(events.isNotEmpty()) {
            return HomeTransform.GoInit(
                events = events,
                error = null
            )
        }
        else {
            val e = AppError.NotFound
            Log.e(TAG, "fetch:e:---------------- $e")
            return HomeTransform.GoInit(
                error = e
            )
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