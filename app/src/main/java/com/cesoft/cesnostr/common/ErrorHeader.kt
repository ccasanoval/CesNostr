package com.cesoft.cesnostr.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.home.view.TitleHeight
import com.cesoft.cesnostr.ui.theme.SepMed
import com.cesoft.cesnostr.ui.theme.SepMin

@Composable
internal fun ErrorHeader(
    text: String,
    isErrorVisible: MutableState<Boolean>,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.errorContainer)
            .fillMaxWidth()
            .height(TitleHeight)
            .padding(SepMed)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = SepMin)
                .weight(.5f)
        )
        IconButton(onClick = { isErrorVisible.value = false }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}