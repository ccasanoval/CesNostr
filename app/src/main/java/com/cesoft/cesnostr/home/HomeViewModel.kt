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
import com.cesoft.cesnostr.view.Page
import com.cesoft.cesnostr.home.vmi.HomeIntent
import com.cesoft.cesnostr.home.vmi.HomeSideEffect
import com.cesoft.cesnostr.home.vmi.HomeState
import com.cesoft.cesnostr.home.vmi.HomeTransform
import com.cesoft.domain.AppError
import com.cesoft.domain.usecase.ReadPrivateKeyUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import rust.nostr.sdk.Client
import rust.nostr.sdk.Filter
import rust.nostr.sdk.Keys
import rust.nostr.sdk.Kind
import rust.nostr.sdk.KindStandard
import rust.nostr.sdk.LogLevel
import rust.nostr.sdk.Metadata
import rust.nostr.sdk.NostrSigner
import rust.nostr.sdk.PublicKey
import rust.nostr.sdk.initLogger
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val readPrivateKey: ReadPrivateKeyUC,
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
        try {
            initLogger(LogLevel.INFO)
            val pubKeyCes = PublicKey.parse("npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k")//CES
            val pubKeyBtc = PublicKey.parse("npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv")

            //TODO: Meter todo en repositorio -> dominio:caso_uso
            var client = readPrivateKey()?.let {
                val keys = Keys.parse(it)
                val signer = NostrSigner.keys(keys)
                Client(signer = signer)
            } ?: run {
                Client()
            }

            client.addRelay("wss://nos.lol")
            client.addRelay("wss://nostr.bitcoiner.social")
            client.addRelay("wss://nostr.mom")
            client.addRelay("wss://nostr.oxtr.dev")
            client.addRelay("wss://relay.nostr.band")
            client.addRelay("wss://relay.damus.io")
            client.addRelay("wss://nostr.swiss-enigma.ch")
            client.connect()

            //----------- Metadata
            val metadata = mutableMapOf<String, Metadata>()
            val filterMD = Filter()
                .kind(Kind.fromStd(KindStandard.METADATA))
                .authors(listOf(pubKeyCes, pubKeyBtc))
                .limit(5u)
            val metadataEvents = client.fetchEvents(filterMD, Duration.ofSeconds(1L)).toVec()
            metadataEvents.forEach { m ->
                metadata[m.author().toHex()] = Metadata.fromJson(m.content())
                android.util.Log.e("AA", "---------meta1: ${m.asJson()}")
                //      "id":"2ae631c0e256d373f87f47418fdc25fea168cd0b8c18949ad7923c18bd137ff9",
                //      "pubkey":"cc5036ac7ef9c69eb7780e439cd5023fdefcefc06ecfdf455ca26880dfed8df1",
                //      "created_at":1745489671,"kind":0,
                //      "tags":[["alt","User profile for Opus2501"]],
                //      "content":"{
                //          "name":"Opus2501",
                //          "display_name":"Opus2501",
                //          "picture":"https://cortados.freevar.com/web/front/images/scorpion_s.png",
                //          "website":"https://cortados.freevar.com",
                //          "lud16":"wordybritish81@walletofsatoshi.com",
                //          "banner":"https://cortados.freevar.com"}",
                //          "sig":"94c21c13ddf9a31f1d5571555a095b79271d3ff5b650fda70b3219bf572904d73b98953f8583cda79fd4c10e341dffe0d23768bbcee00b55cb59824f7d5d0a0c"
            }

            val filter = Filter()
                .kind(Kind.fromStd(KindStandard.TEXT_NOTE))
                .authors(listOf(pubKeyCes, pubKeyBtc))
                .limit(15u)
//            val events = client.fetchEventsFrom(
//                urls = listOf("wss://relay.damus.io"),
//                filter = filter,timeout = Duration.ofSeconds(10L)).toVec()
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
                return HomeTransform.GoInit(events = events, metadata = metadata)
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