package com.cesoft.cesnostr.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.home.view.AuthorIconSize
import com.cesoft.cesnostr.home.view.SeparatorHeight
import com.cesoft.cesnostr.ui.theme.FontSizeMed
import com.cesoft.cesnostr.ui.theme.SepMin
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrMetadata

@Composable
fun EventListItem(event: NostrEvent, onAuthorClick: () -> Unit) {
    Column {
        HorizontalDivider(
            modifier = Modifier.height(SeparatorHeight),
            color = MaterialTheme.colorScheme.primary
        )

        // Author metadata
        AuthorMetadata(event.authMeta, onAuthorClick)

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

@Composable
private fun AuthorMetadata(authMeta: NostrMetadata, onClick: () -> Unit) {
    Row(modifier = Modifier.padding(SepMin).clickable { onClick() }) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(authMeta.picture)
                .crossfade(true)
                .build(),
            placeholder = painterResource(android.R.drawable.ic_menu_report_image),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(AuthorIconSize),
            //colorFilter = ColorFilter.tint(Color.Unspecified)
        )
        var name = authMeta.displayName
        if(name.isBlank()) name = authMeta.name
        if(name.isBlank()) name = "?"
        Column(modifier = Modifier.padding(start = SepMin)) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = FontSizeMed,
            )
            Text(
                text = authMeta.lud16,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
