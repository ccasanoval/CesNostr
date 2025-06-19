package com.cesoft.cesnostr.home.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.LinkifyText
import com.cesoft.cesnostr.home.vmi.HomeIntent
import com.cesoft.cesnostr.home.vmi.HomeState
import com.cesoft.cesnostr.ui.theme.FontSizeBig
import com.cesoft.cesnostr.ui.theme.FontSizeMed
import com.cesoft.cesnostr.ui.theme.SepMax
import com.cesoft.cesnostr.ui.theme.SepMin
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKindStandard
import com.cesoft.domain.entity.NostrMetadata
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeEventList(
    state: HomeState.Init,
    reduce: (HomeIntent) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    val events = state.events
    var isRefreshing = state.wait
    LaunchedEffect(state) {
        isRefreshing = state.wait
    }
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { reduce(HomeIntent.Reload) },
        state = refreshState
    ) {
        LazyColumn {
            if(events.isEmpty()) {
                item { Spacer(Modifier.fillMaxWidth().height(SepMax*5)) }
            }
            for (event in events) {
                item {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier.height(SeparatorHeight),
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Author metadata
                        AuthorMetadata(event) {
                            reduce(HomeIntent.Author(event.npub))
                        }

                        // Time and type
                        Text(event.createdAt.toString())
                        Text("${event.kind}")

                        // Content
                        HorizontalDivider()
                        LinkifyText(
                            text = event.content,
                            style = TextStyle.Default.copy(
                                lineBreak = LineBreak.Paragraph,
                                hyphens = Hyphens.Auto
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthorMetadata(event: NostrEvent, onClick: () -> Unit) {
    var meta: NostrMetadata? = event.authMeta
    meta?.let {
        Row(modifier = Modifier.padding(SepMin).clickable { onClick() }) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it.picture)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(android.R.drawable.ic_menu_report_image),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(AuthorIconSize),
                //colorFilter = ColorFilter.tint(Color.Unspecified)
            )
            Column(modifier = Modifier.padding(start = SepMin)) {
                Text(
                    text = it.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSizeMed,
                )
                Text(
                    text = it.lud16,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

//-----------------------------------------------------------------------------
val bitcoinMetadata = NostrMetadata(
    npub = "npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv",
    about = "Bitcoin is an open source censorship-resistant peer-to-peer immutable network. Trackable digital gold. Don’t trust; verify. Not your keys; not your coins.",
    name = "bitcoin",
    displayName = "Bitcoin",
    website = "https://bitcoin.org/bitcoin.pdf",
    picture = "https://blossom.primal.net/7e830d7297998db82e8e0ba409639c8581f85e99d7649208100879d84ac537d3.jpg",
    banner = "https://blossom.primal.net/74b6896813c18781d6edc1c00472a1c7121903424a7d99606c8f99709a41e2c9.jpg",
    lud06 = "",
    lud16 = "bd7ea219a0929a84@coinos.io",
    nip05 = "Bitcoin@NostrVerified.com"
)
val opusMetadata = NostrMetadata(
    npub="npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k",
    about="Cyberpunk writer and creator",
    name="Opus2501",
    displayName="Opus2501",
    website="https://cortados.freevar.com",
    picture="https://cortados.freevar.com/web/front/images/scorpion_s.png",
    banner="https://cortados.freevar.com/web/front/images/logo.png",
    lud06="",
    lud16="wordybritish81@walletofsatoshi.com",
    nip05=""
)

val testEvents = listOf(
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
        npub = bitcoinMetadata.npub,
        authMeta = bitcoinMetadata,
        createdAt = LocalDateTime.now(),
        content = "This is more content of another Nostr event",
        json = "{\"id\":\"24364fd48889af7408ed60bcea29b6d18bc00c99a39256c1827c0741f92256f2\",\"pubkey\":\"aac9cfd55415fb5a175f131238c37386f7c24a7e0fc9ddace56fac72edf06307\",\"created_at\":1747399520,\"kind\":1,\"tags\":[[\"t\",\"meme\"],[\"t\",\"memestr\"],[\"t\",\"nostrmeme\"]],\"content\":\"https://cdn.nostr.build/i/cced0b8d75dca9ac959b3810f11286768e3fca227c93deee97f69b0485afb78b.jpg #meme #memestr #nostrmeme\",\"sig\":\"47ec307abb97c087de104afefd400c3670a50ccffb1152790fce6f1ff7eaca7c7f97c691cf936b4ff5bd79d91253b38c40083cfd3e52fa127265b9116f914a87\"}"
    )
)

@Preview
@Composable
private fun HomeEventList_Preview() {
    val state = HomeState.Init(
        events = testEvents
    )
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeEventList(state) {}
    }
}