package com.cesoft.cesnostr.follow.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.follow.vmi.FollowIntent
import com.cesoft.cesnostr.home.view.AuthorIconSize
import com.cesoft.cesnostr.ui.theme.SepMin
import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.entity.bitcoinMetadata
import com.cesoft.domain.entity.opusMetadata

@Composable
fun FollowListItem(author: NostrMetadata, reduce: (FollowIntent) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(SepMin)
            .fillMaxWidth()
            .clickable{ reduce(FollowIntent.GoAuthor(author.npub)) }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(author.picture)
                .crossfade(true)
                .build(),
            placeholder = painterResource(android.R.drawable.ic_menu_report_image),
            contentDescription = stringResource(R.string.acc_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(AuthorIconSize).padding(end = SepMin),
        )
        Column {
            Text(text = "${author.displayName} (${author.name})")
            Text(text = author.about, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = author.lud16)//TODO: Let user send sats
        }
    }
    HorizontalDivider()
}

@Preview
@Composable
private fun FollowListItem_Preview() {
    Surface {
        Column {
            FollowListItem(bitcoinMetadata) { }
            FollowListItem(opusMetadata) { }
        }
    }
}