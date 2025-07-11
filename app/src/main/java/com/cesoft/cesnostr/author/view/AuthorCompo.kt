package com.cesoft.cesnostr.author.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.LinkifyText
import com.cesoft.cesnostr.home.view.AuthorIconSize
import com.cesoft.cesnostr.ui.theme.SepMed
import com.cesoft.cesnostr.ui.theme.SepMin
import com.cesoft.domain.entity.NostrMetadata

@Composable
fun AuthorCompo(metadata: NostrMetadata) {
    Row {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(metadata.picture)
                .crossfade(true)
                .build(),
            placeholder = painterResource(android.R.drawable.ic_menu_report_image),
            contentDescription = stringResource(R.string.acc_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(AuthorIconSize),
        )
        Spacer(Modifier.size(SepMin))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(metadata.banner)
                .crossfade(true)
                .build(),
            placeholder = painterResource(android.R.drawable.ic_menu_report_image),
            contentDescription = stringResource(R.string.acc_banner),
            contentScale = ContentScale.Fit,
            modifier = Modifier//.size(AuthorIconSize),
        )
    }
    Spacer(Modifier.size(SepMed))
    LazyColumn {
        item { ItemCompo(stringResource(R.string.public_key), metadata.npub) }
        item { ItemCompo(stringResource(R.string.acc_name), metadata.name) }
        item { ItemCompo(stringResource(R.string.acc_display_name), metadata.displayName) }
        item { ItemCompo(stringResource(R.string.acc_about), metadata.about) }
        item { ItemCompo(stringResource(R.string.acc_picture), metadata.picture) }
        item { ItemCompo(stringResource(R.string.acc_banner), metadata.banner) }
        item { ItemCompo(stringResource(R.string.acc_website), metadata.website) }
        item { ItemCompo(stringResource(R.string.acc_lud16), metadata.lud16) }
        item { ItemCompo(stringResource(R.string.acc_lud06), metadata.lud06) }
        item { ItemCompo(stringResource(R.string.acc_nip05), metadata.nip05) }
    }
}

@Composable
private fun ItemCompo(title: String, value: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold
    )
    LinkifyText(
        text = value,
        color = MaterialTheme.colorScheme.secondary
    )
    Spacer(Modifier.size(SepMin))
}
