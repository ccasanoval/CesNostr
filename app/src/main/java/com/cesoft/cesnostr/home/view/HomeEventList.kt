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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.LinkifyText
import com.cesoft.cesnostr.home.vmi.HomeIntent
import com.cesoft.cesnostr.home.vmi.HomeState
import com.cesoft.cesnostr.ui.theme.SepMin
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrMetadata

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
            for (event in events) {
                item {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier.height(SeparatorHeight),
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Author metadata
                        AuthorMetadata(event)

                        // Time and type
                        Text(event.createdAt.toString())
                        Text("${event.kind} (${event.kind.ordinal})")

                        // Content
                        HorizontalDivider()
                        LinkifyText(event.content)
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthorMetadata(event: NostrEvent) {
    var meta: NostrMetadata? = event.authMeta
    meta?.let {
        Row(modifier = Modifier.padding(SepMin)) {
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
                Text(it.displayName)
                Text(it.lud16, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}