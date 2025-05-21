package com.cesoft.cesnostr.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.LinkifyText
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.cesnostr.home.HomeViewModel
import com.cesoft.cesnostr.home.vmi.HomeIntent
import com.cesoft.cesnostr.home.vmi.HomeState
import com.cesoft.cesnostr.ui.theme.SepMin
import rust.nostr.sdk.Event
import rust.nostr.sdk.Metadata

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
    val m1 = Metadata()
        .setDisplayName("CESoft")
        .setPicture("https://cortados.freevar.com/web/front/images/scorpion_s.png")
    val m2 = Metadata()
        .setPicture("https://blossom.primal.net/7e830d7297998db82e8e0ba409639c8581f85e99d7649208100879d84ac537d3.jpg")
        .setName("Test Name")
    val state = HomeState.Init(
        events = listOf(
            Event.fromJson("{\"id\":\"9edcf548dce279d324a052bed5e70201377f3858df89c129122f1c4c48f53b4f\",\"pubkey\":\"1e67de3754171071d3cf9b44b6e546bd94fd0a2ca3fb4dbbb1b054685c9116e4\",\"created_at\":1747399522,\"kind\":1,\"tags\":[],\"content\":\"Interviewé sur RTL ! \\nEnlèvements liés aux cryptomonnaies : les mythes d’argent facile qui attirent les ravisseurs \\nhttps://www.rtl.fr/actu/sciences-tech/enlevements-lies-aux-cryptomonnaies-les-mythes-d-argent-facile-qui-attirent-les-ravisseurs-7900505683\\n#nostrfr\",\"sig\":\"9badf62758cad7aeb0b69bf1814e1e05010dd067f76a23a9909e1722916e80b4293f9fc2e4adefa3a75688b780ac109392f39ea4761d5676a9ef75c813db64b9\"}"),
            Event.fromJson("{\"id\":\"24364fd48889af7408ed60bcea29b6d18bc00c99a39256c1827c0741f92256f2\",\"pubkey\":\"aac9cfd55415fb5a175f131238c37386f7c24a7e0fc9ddace56fac72edf06307\",\"created_at\":1747399520,\"kind\":1,\"tags\":[[\"t\",\"meme\"],[\"t\",\"memestr\"],[\"t\",\"nostrmeme\"]],\"content\":\"https://cdn.nostr.build/i/cced0b8d75dca9ac959b3810f11286768e3fca227c93deee97f69b0485afb78b.jpg #meme #memestr #nostrmeme\",\"sig\":\"47ec307abb97c087de104afefd400c3670a50ccffb1152790fce6f1ff7eaca7c7f97c691cf936b4ff5bd79d91253b38c40083cfd3e52fa127265b9116f914a87\"}"),
        ),
        metadata = mapOf<String, Metadata>(
            Pair("1e67de3754171071d3cf9b44b6e546bd94fd0a2ca3fb4dbbb1b054685c9116e4", m1),
            Pair("aac9cfd55415fb5a175f131238c37386f7c24a7e0fc9ddace56fac72edf06307", m2),
        )
    )
    HomeInit(state) {}
}
