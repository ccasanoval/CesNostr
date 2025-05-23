package com.cesoft.cesnostr.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun InetImage(
    image: String?,
    modifier: Modifier = Modifier,
    desc: String = "",
    scale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(image)
            .crossfade(true)
            .build(),
        placeholder = painterResource(android.R.drawable.ic_menu_report_image),
        contentDescription = desc,
        contentScale = scale,
        modifier = modifier,
        //colorFilter = ColorFilter.tint(Color.Unspecified)
    )
}