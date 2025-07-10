package com.cesoft.cesnostr.home.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.cesnostr.home.HomeViewModel
import com.cesoft.cesnostr.home.mvi.HomeIntent
import com.cesoft.cesnostr.home.mvi.HomeState
import com.cesoft.domain.AppError.NotKnownError
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKindStandard
import java.time.LocalDateTime

internal val TitleHeight = 50.dp
internal val AuthorIconSize = 50.dp
internal val SeparatorHeight = 5.dp

@Composable
fun HomePage(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val context = LocalContext.current
    MviScreen(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            viewModel.consumeSideEffect(
                sideEffect = sideEffect,
                navController = navController,
                context = context
            )
        },
        onBackPressed = {
            viewModel.execute(HomeIntent.Close)
        },
    ) { state: HomeState ->
        when(state) {
            is HomeState.Loading -> {
                viewModel.execute(HomeIntent.Load)
                LoadingCompo()
            }
            is HomeState.Init -> {
                HomeInit(state = state, reduce = viewModel::execute)
            }
        }
    }
}

//-----------------------------------------------------------------------------
@Preview
@Composable
private fun HomePage_Preview() {
    val state = HomeState.Init(
        events = listOf(
            NostrEvent(
                kind = NostrKindStandard.TEXT_NOTE,
                tags = listOf("Tag1", "Tag2"),
                npub = opusMetadata.npub,
                authMeta = opusMetadata,
                createdAt = LocalDateTime.now(),
                content = "This is the content of the Nostr event",
                json = "{\"id\":\"9edcf548dce279d324a052bed5e70201377f3858df89c129122f1c4c48f53b4f\",\"pubkey\":\"1e67de3754171071d3cf9b44b6e546bd94fd0a2ca3fb4dbbb1b054685c9116e4\",\"created_at\":1747399522,\"kind\":1,\"tags\":[],\"content\":\"Interviewé sur RTL ! \\nEnlèvements liés aux cryptomonnaies : les mythes d’argent facile qui attirent les ravisseurs \\nhttps://www.rtl.fr/actu/sciences-tech/enlevements-lies-aux-cryptomonnaies-les-mythes-d-argent-facile-qui-attirent-les-ravisseurs-7900505683\\n#nostrfr\",\"sig\":\"9badf62758cad7aeb0b69bf1814e1e05010dd067f76a23a9909e1722916e80b4293f9fc2e4adefa3a75688b780ac109392f39ea4761d5676a9ef75c813db64b9\"}"
            ),
            NostrEvent(
                kind = NostrKindStandard.TEXT_NOTE,
                tags = listOf("Tag01", "Tag02"),
                npub  = bitcoinMetadata.npub,
                authMeta = bitcoinMetadata,
                createdAt = LocalDateTime.now(),
                content = "This is more content of another Nostr event",
                json = "{\"id\":\"24364fd48889af7408ed60bcea29b6d18bc00c99a39256c1827c0741f92256f2\",\"pubkey\":\"aac9cfd55415fb5a175f131238c37386f7c24a7e0fc9ddace56fac72edf06307\",\"created_at\":1747399520,\"kind\":1,\"tags\":[[\"t\",\"meme\"],[\"t\",\"memestr\"],[\"t\",\"nostrmeme\"]],\"content\":\"https://cdn.nostr.build/i/cced0b8d75dca9ac959b3810f11286768e3fca227c93deee97f69b0485afb78b.jpg #meme #memestr #nostrmeme\",\"sig\":\"47ec307abb97c087de104afefd400c3670a50ccffb1152790fce6f1ff7eaca7c7f97c691cf936b4ff5bd79d91253b38c40083cfd3e52fa127265b9116f914a87\"}"
            )
        )
    )
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeInit(state) {}
    }
}

@Preview
@Composable
private fun HomePage_Empty_Preview() {
    val state = HomeState.Init()
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeInit(state) {}
    }
}

@Preview
@Composable
private fun HomePage_Error_Preview() {
    val state = HomeState.Init(error = NotKnownError)
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeInit(state) {}
    }
}
