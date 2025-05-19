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
import com.cesoft.domain.entity.parseEventKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import rust.nostr.sdk.Client
import rust.nostr.sdk.Event
import rust.nostr.sdk.Events
import rust.nostr.sdk.ExternalContentId
import rust.nostr.sdk.Filter
import rust.nostr.sdk.Keys
import rust.nostr.sdk.Kind
import rust.nostr.sdk.KindStandard
import rust.nostr.sdk.LogLevel
import rust.nostr.sdk.NostrConnect
import rust.nostr.sdk.NostrSigner
import rust.nostr.sdk.PublicKey
import rust.nostr.sdk.initLogger
import java.security.PrivateKey
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
        Log.e("AAA", "********************************************* LOAD")
        val state = fetch()
        emit(state)
    }

    private suspend fun fetch(): HomeTransform.GoInit {
        try {
            Log.e("AAA", "********************************************* FETCH 0")
            initLogger(LogLevel.INFO)
            //val pubKey = PublicKey.parse("npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k")//CES
            //var keys = Keys.parse(        "nsec1j4c6269y9w0q2er2xjw8sv2ehyrtfxq3jwgdlxj6qfn8z4gjsq5qfvfk99")
            val keys = Keys.parse("nsec1dh2c86ga0ajrcgaye0zr53h7nnxlfe67dxqnvhmcqld62jx8rk7qnvfzre")
            //val keys = Keys.generate()
            val signer = NostrSigner.keys(keys)
            val client = Client(signer = signer)
            client.addRelay("wss://nostr.bitcoiner.social")
            //client.addRelay("wss://nos.lol")
            //client.addRelay("wss://nostr.mom")
            //client.addRelay("wss://relay.damus.io")
            client.connect()
            //val keyBtc = PublicKey.fromBech32("npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv")
            //val filter = Filter()
            //val filter: Filter = Filter().kind(Kind.fromStd(KindStandard.METADATA)).limit(3u)
            //filter.author(keyBtc)
            //filter.kinds(listOf(1u))
            //val events: List<Event> = client.getEventsOf(listOf(filter), Duration.ofMillis(1000))
            val filter: Filter = Filter().kind(Kind.fromStd(KindStandard.TEXT_NOTE)).limit(5u)
//            val events = client.fetchEventsFrom(
//                urls = listOf(
//                    "wss://nostr.bitcoiner.social",
//                    "wss://relay.damus.io",
//                    "wss://nos.lol"
//                ),
//                filter = filter,
//                timeout = Duration.ofSeconds(10L)
//            ).toVec()
            val events = client.fetchEvents(filter, Duration.ofSeconds(10L)).toVec()
            Log.e("AAA", "************************* EVENTS = ${events.size}")
            client.close()

            //--------------------
            events.forEach { e ->
                Log.e("AAA", "*********************************************")
                //android.util.Log.e("AAA", "-------------------- id   = ${e.id()}")
                //android.util.Log.e("AAA", "-------------------- kind = ${e.identifier()}")
                Log.e("AAA", "-------------------- kind = ${e.kind()} ")
                //android.util.Log.e("AAA", "-------------------- #tag = ${e.tags().size}")
                var tags = ""
                for (tag in e.tags().toVec()) {
                    tags += tag
                }
                Log.e("AAA", "----------------------- tags = $tags")
                Log.e("AAA", "-------------------- auth = ${e.author().toNostrUri()}")
                //android.util.Log.e("AAA", "-------------------- auth = ${e.author().toBech32()}")
                Log.e("AAA", "-------------------- crat = ${e.createdAt().toHumanDatetime()}")
                Log.e("AAA", "-------------------- cont = ${e.content()}")
                //android.util.Log.e("AAA", "-------------------- vrfy = ${e.verify()}")
                //android.util.Log.e("AAA", "-------------------- sign = ${e.signature()}")
                Log.e("AAA", "-------------------- json = ${e.asJson()}")
            }
            //--------------------

            if (events.isNotEmpty()) {
                return HomeTransform.GoInit(events = events)
            }
            else {
                val e = AppError.NotFound
                Log.e(TAG, "fetch:e:---------------- $e")
                return HomeTransform.GoInit(error = e)
            }
        }
        catch (e: Exception) {
            Log.e(TAG, "fetch:failure:---------------- $e")
            return HomeTransform.GoInit(error = e)
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