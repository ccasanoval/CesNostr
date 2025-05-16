package com.cesoft.cesnostr.home

import android.app.usage.UsageEvents
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.cesnostr.home.vmi.HomeIntent
import com.cesoft.cesnostr.home.vmi.HomeState
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.message
import com.cesoft.cesnostr.ui.theme.SepMax
import com.cesoft.cesnostr.ui.theme.SepMed
import rust.nostr.protocol.Event


private val TitleHeight = 50.dp

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
                Init(state = state, reduce = viewModel::execute)
            }
        }
    }
}

@Composable
private fun Init(
    state: HomeState.Init,
    reduce: (HomeIntent) -> Unit
) {
    Column {
        if(state.wait) LoadingCompo(background = false)

        val isErrorVisible = remember { mutableStateOf(true) }
        LaunchedEffect(state.error) { isErrorVisible.value = true }
        if(state.error != null && isErrorVisible.value) {
            HeaderError(state.error.message(LocalContext.current), isErrorVisible)
        }
        else {
            HeaderTitle()
            EventList(state.events)
        }
    }
}

@Composable
private fun HeaderError(
    text: String,
    isErrorVisible: MutableState<Boolean>,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.inverseSurface)
            .fillMaxWidth()
            .height(TitleHeight)
            .padding(SepMed)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier
                .padding(start = SepMax)
                .weight(.5f)
        )
        IconButton(onClick = { isErrorVisible.value = false }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.inversePrimary
            )
        }
    }
}

@Composable
private fun HeaderTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(TitleHeight)
            .padding(SepMed)
    ) {
//        Icon(
//            painter = painterResource(R.mipmap.ic_launcher_round),
//            contentDescription = stringResource(R.string.app_name),
//            tint = Color.Unspecified,
//            modifier = Modifier.size(32.dp)
//        )
        Text(
            text = stringResource(R.string.app_name),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = SepMed)
        )
    }
}

@Composable
private fun EventList(events: List<Event>) {
    LazyColumn {
        for(e in events) {
            item {
                Column {
                    HorizontalDivider()
                    Text(e.identifier() ?: "")
                    Text(e.createdAt().toHumanDatetime())
                    Text(e.content())
                }
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
            Event.fromJson("{\"id\":\"9edcf548dce279d324a052bed5e70201377f3858df89c129122f1c4c48f53b4f\",\"pubkey\":\"1e67de3754171071d3cf9b44b6e546bd94fd0a2ca3fb4dbbb1b054685c9116e4\",\"created_at\":1747399522,\"kind\":1,\"tags\":[],\"content\":\"Interviewé sur RTL ! \\nEnlèvements liés aux cryptomonnaies : les mythes d’argent facile qui attirent les ravisseurs \\nhttps://www.rtl.fr/actu/sciences-tech/enlevements-lies-aux-cryptomonnaies-les-mythes-d-argent-facile-qui-attirent-les-ravisseurs-7900505683\\n#nostrfr\",\"sig\":\"9badf62758cad7aeb0b69bf1814e1e05010dd067f76a23a9909e1722916e80b4293f9fc2e4adefa3a75688b780ac109392f39ea4761d5676a9ef75c813db64b9\"}"),
            Event.fromJson("{\"id\":\"24364fd48889af7408ed60bcea29b6d18bc00c99a39256c1827c0741f92256f2\",\"pubkey\":\"aac9cfd55415fb5a175f131238c37386f7c24a7e0fc9ddace56fac72edf06307\",\"created_at\":1747399520,\"kind\":1,\"tags\":[[\"t\",\"meme\"],[\"t\",\"memestr\"],[\"t\",\"nostrmeme\"]],\"content\":\"https://cdn.nostr.build/i/cced0b8d75dca9ac959b3810f11286768e3fca227c93deee97f69b0485afb78b.jpg #meme #memestr #nostrmeme\",\"sig\":\"47ec307abb97c087de104afefd400c3670a50ccffb1152790fce6f1ff7eaca7c7f97c691cf936b4ff5bd79d91253b38c40083cfd3e52fa127265b9116f914a87\"}"),
        )
    )
    Init(state) {}
}
