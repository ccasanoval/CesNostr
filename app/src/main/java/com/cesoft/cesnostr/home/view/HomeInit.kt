package com.cesoft.cesnostr.home.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.ErrorHeader
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.cesnostr.common.toHumanMessage
import com.cesoft.cesnostr.home.vmi.HomeIntent
import com.cesoft.cesnostr.home.vmi.HomeState
import com.cesoft.cesnostr.ui.theme.FontSizeBig
import com.cesoft.cesnostr.ui.theme.SepMax

@Composable
internal fun HomeInit(
    state: HomeState.Init,
    reduce: (HomeIntent) -> Unit
) {
    Column {
        if(state.wait) LoadingCompo(background = false)
        val isErrorVisible = remember { mutableStateOf(true) }
        LaunchedEffect(state.error) { isErrorVisible.value = true }
        if(state.error != null && isErrorVisible.value) {
            ErrorHeader(state.error.toHumanMessage(LocalContext.current), isErrorVisible)
        }
        else {
            //HomeHeaderTitle()
            if(state.events.isEmpty())
                EmptyCompo()
            else
                HomeEventList(state, reduce)
        }
    }
}

@Composable
private fun EmptyCompo() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource((R.string.error_not_found)),
            fontWeight = FontWeight.Bold,
            fontSize = FontSizeBig,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(SepMax)
        )
    }
}


