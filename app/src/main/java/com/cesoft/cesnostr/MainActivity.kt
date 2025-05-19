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
import rust.nostr.protocol.Tag
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
}

