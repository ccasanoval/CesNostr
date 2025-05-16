package com.cesoft.cesnostr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cesoft.cesnostr.ui.theme.CesNostrTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import rust.nostr.protocol.Event
import rust.nostr.protocol.Filter
import rust.nostr.protocol.Keys
import rust.nostr.protocol.PublicKey
import rust.nostr.sdk.Client
import rust.nostr.sdk.ClientSigner
import rust.nostr.sdk.LogLevel
import rust.nostr.sdk.initLogger
import java.time.Duration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CesNostrTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainCompose(innerPadding)
                }
            }
        }
    }

    @Composable
    private fun MainCompose(innerPadding: PaddingValues) {
        Surface(modifier = Modifier.padding(innerPadding)) {
            PageNavigation()
        }
    }

    override fun onResume() {
        super.onResume()
        //runNostr()
    }

    private fun runNostr() {
        runBlocking {
        //MainScope().launch {

            initLogger(LogLevel.INFO)

            val key = PublicKey.fromBech32("npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k")//CES
            //val key = PublicKey.fromNostrUri("npub1e3g:csnt0q7k")//CES
            val keys = Keys.fromPublicKey(key)

            val client = Client(ClientSigner.keys(keys))
            //client.addRelay("wss://relay.damus.io")
            client.addRelay("wss://nos.lol")
            client.addRelay("wss://nostr.bitcoiner.social")
            //client.addRelay("wss://nostr.mom")
            client.connect()

            val keyBtc = PublicKey.fromBech32("npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv")
            val filter = Filter()
            filter.author(keyBtc)
            val events: List<Event> = client.getEventsOf(listOf(filter), Duration.ofMillis(1000))
            events.forEach { e ->
                android.util.Log.e("AAA", "*********************************************")
                android.util.Log.e("AAA", "-------------------- id   = ${e.id()}")
                android.util.Log.e("AAA", "-------------------- kind = ${e.identifier()}")
                android.util.Log.e("AAA", "-------------------- kind = ${e.kind()}")
                android.util.Log.e("AAA", "-------------------- #tag = ${e.tags().size}")
//                for(tag in e.tags()) {
//                    for(t in tag.asVec())
//                        android.util.Log.e("AAA", "----------------------- tag = ${t}")
//                }
                android.util.Log.e("AAA", "-------------------- auth = ${e.author().toNostrUri()}")
                //android.util.Log.e("AAA", "-------------------- auth = ${e.author().toBech32()}")
                android.util.Log.e("AAA", "-------------------- crat = ${e.createdAt().toHumanDatetime()}")
                android.util.Log.e("AAA", "-------------------- cont = ${e.content()}")
                //android.util.Log.e("AAA", "-------------------- vrfy = ${e.verify()}")
                //android.util.Log.e("AAA", "-------------------- sign = ${e.signature()}")
                android.util.Log.e("AAA", "-------------------- json = ${e.asJson()}")
            }



            //Fatal signal 11 (SIGSEGV), code 2 (SEGV_ACCERR), fault addr 0x7793b56c9140 in tid 2500 (cesoft.cesnostr), pid 2500 (cesoft.cesnostr)
            /*val new = true

            val keys = Keys.generate()
            val signer = NostrSigner.keys(keys)
            val client = Client(signer = signer)
            client.addRelay("wss://relay.damus.io")
            client.connect()

            if (false) {
                val builder = EventBuilder.textNote("Hello, rust-nostr!")
                val output = client.sendEventBuilder(builder)

                println("Event ID: ${output.id.toBech32()}")
                println("Sent to: ${output.success}")
                println("Not sent to: ${output.failed}")
            }

            //val filter1: Filter = Filter().kind(Kind.fromStd(KindStandard.METADATA)).limit(3u)
            //val events1: Events = client.fetchEvents(filter = filter1, timeout = Duration.ofSeconds(10L))

            val filter2: Filter = Filter().kind(Kind.fromStd(KindStandard.TEXT_NOTE)).limit(5u)
            val events2: Events = client.fetchEventsFrom(
                urls = listOf("wss://relay.damus.io"),
                filter = filter2,
                timeout = Duration.ofSeconds(10L)
            )

            val first = events2.first()?.let {
                android.util.Log.e("AAA", "-----------------------  id  = ${it.id()}")
                android.util.Log.e("AAA", "----------------------- kind = ${it.kind()}")
                android.util.Log.e("AAA", "----------------------- c at = ${it.createdAt()}")
                android.util.Log.e("AAA", "----------------------- auth = ${it.author()}")
                android.util.Log.e("AAA", "----------------------- cont = ${it.content()}")
                android.util.Log.e("AAA", "----------------------- sign = ${it.signature()}")
            }*/
        }
    }
}

