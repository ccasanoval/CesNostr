package com.cesoft.cesnostr.author

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.author.vmi.AuthorIntent
import com.cesoft.cesnostr.author.vmi.AuthorSideEffect
import com.cesoft.cesnostr.author.vmi.AuthorState
import com.cesoft.cesnostr.author.vmi.AuthorTransform
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKindStandard
import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.usecase.GetEventsUC
import com.cesoft.domain.usecase.GetKeysUC
import com.cesoft.domain.usecase.GetUserMetadataUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class AuthorViewModel @Inject constructor(
    private val getKeys: GetKeysUC,
    private val getEvents: GetEventsUC,
    private val getUserMetadata: GetUserMetadataUC,
): ViewModel(), MviHost<AuthorIntent, State<AuthorState, AuthorSideEffect>> {

    private val reducer: Reducer<AuthorIntent, State<AuthorState, AuthorSideEffect>> = Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = Dispatchers.Default,
        initialInnerState = AuthorState.Loading,
        logger = null,
        intentExecutor = this::executeIntent
    )
    override val state = reducer.state
    override fun execute(intent: AuthorIntent) {
        reducer.executeIntent(intent)
    }
    private fun executeIntent(intent: AuthorIntent) =
        when(intent) {
            AuthorIntent.Close -> executeClose()
            AuthorIntent.Load -> executeLoad()
            AuthorIntent.Reload -> executeReload()
        }

    private fun executeClose() = flow {
        emit(AuthorTransform.AddSideEffect(AuthorSideEffect.Close))
    }

    private fun executeReload() = flow {
        emit(AuthorTransform.GoReload)
    }

    private fun executeLoad() = flow {
        emit(fetch())
    }


    private suspend fun fetch(): AuthorTransform.GoInit {

        /*val res: Result<List<NostrEvent>> = getEvents(
            kind = NostrKindStandard.TEXT_NOTE,
            authList = listOf(
                "npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k",//CES
                "npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv",//BTC
            )
        )*/
        //TODO: This page should receive de list of authors
        //TODO: Develop this in use case
        val map = mutableMapOf<String, NostrMetadata>()
        val authList = listOf(
            "npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k",//CES
            "npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv",//BTC
        )
        var error: Throwable? = null
        for(a in authList) {
            val res: Result<NostrMetadata> = getUserMetadata(a)
            res.getOrNull()?.let { map[a] = it }
            res.exceptionOrNull()?.let { error = it }
        }
        return if(map.isNotEmpty()) {
            AuthorTransform.GoInit(metadata = map)
        } else {
            AuthorTransform.GoInit(error = error ?: UnknownError())
        }

/*
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
                metadata[m.author().toHex()] = Metadata.fromJson(m.content())
                android.util.Log.e("AA", "---------meta1: ${m.asJson()}")
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
                return AuthorTransform.GoInit(metadata = metadata)
            }
            else {
                val e = AppError.NotFound
                Log.e(TAG, "fetch:e:---------------- $e")
                return AuthorTransform.GoInit(error = e)
            }
        }
        catch (e: Exception) {
            Log.e(TAG, "fetch:failure:---------------- $e")
            return AuthorTransform.GoInit(error = e)
        }*/
    }

    companion object {
        private const val TAG = "AuthorVM"
    }
}