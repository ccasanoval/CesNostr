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
import rust.nostr.sdk.Client
import rust.nostr.sdk.Filter
import rust.nostr.sdk.Keys
import rust.nostr.sdk.Kind
import rust.nostr.sdk.Metadata
import rust.nostr.sdk.KindStandard
import rust.nostr.sdk.LogLevel
import rust.nostr.sdk.NostrSigner
import rust.nostr.sdk.PublicKey
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
        Log.e("AAA", "********************************************* LOAD")
        val state = fetch()
        emit(state)
    }

    private suspend fun fetch(): HomeTransform.GoInit {
        try {
            Log.e("AAA", "********************************************* FETCH 0")
            initLogger(LogLevel.INFO)
            val pubKeyCes = PublicKey.parse("npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k")//CES
            val pubKeyBtc = PublicKey.parse("npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv")
            var privKeyCes = Keys.parse(        "nsec1j4c6269y9w0q2er2xjw8sv2ehyrtfxq3jwgdlxj6qfn8z4gjsq5qfvfk99")//fzre
            //val privKeyCes = Keys.parse("nsec1dh2c86ga0ajrcgaye0zr53h7nnxlfe67dxqnvhmcqld62jx8rk7qnvzort")
            //val keys = Keys.generate()

            val signer = NostrSigner.keys(privKeyCes)
            val client = Client(signer = signer)
            client.addRelay("wss://relay.damus.io")
            client.addRelay("wss://nostr.bitcoiner.social")
            client.addRelay("wss://nos.lol")
            client.connect()

            //----------- Metadata
            val metadata = mutableMapOf<String, Metadata>()
            val filterMD = Filter()
                .kind(Kind.fromStd(KindStandard.METADATA))
                .authors(listOf(pubKeyCes, pubKeyBtc))
                .limit(5u)
            val metadataEvents = client.fetchEvents(filterMD, Duration.ofSeconds(1L)).toVec()

            metadataEvents.forEach { m ->
                val a = Metadata.fromJson(m.content())
                metadata[m.author().toHex()] = a
                android.util.Log.e("AA", "---------meta1: ${m.asJson()}")
                android.util.Log.e("AA", "---------meta2: ${a.getDisplayName()} / ${m.author().toHex()}")
                //meta: {"id":"3b3d94d7996b68dec9a9237bf1033b680421d46cc8447c93bbc7555e9e046fda",
                //      "pubkey":"a2c580ed966ff13d295aab54f2073e2675e6e2e72d8e0711050eaf243db651c1",
                //      "created_at":1747512474,"kind":0,"tags":[],
                //      "content":"{
                //          "website":"https://bitcoin.org/bitcoin.pdf",
                //          "lud16":"flaxcat6@primal.net",
                //          "picture":"https://blossom.primal.net/7e830d7297998db82e8e0ba409639c8581f85e99d7649208100879d84ac537d3.jpg",
                //          "nip05":"Bitcoin@NostrVerified.com",
                //          "display_name":"Bitcoin",
                //          "lud06":"",
                //          "name":"bitcoin",
                //          "about":"Bitcoin is an open source censorship-resistant peer-to-peer immutable network. Trackable digital gold. Don’t trust; verify. Not your keys; not your coins.",
                //          "banner":"https://blossom.primal.net/74b6896813c18781d6edc1c00472a1c7121903424a7d99606c8f99709a41e2c9.jpg"
                //      }",
                //      "sig":"ea123d43e1c5abefe7b09ea2f4f36f4331f3f9d3824b4f6c3279e24d854ada58a00f3fe1377c1556cc994c20a20a6b0846124fd6949dff2396e073f9de6e34a8"
                //}
                //meta: {
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
                //}
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