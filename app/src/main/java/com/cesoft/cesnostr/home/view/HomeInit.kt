package com.cesoft.cesnostr.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.cesoft.cesnostr.common.ErrorHeader
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.cesnostr.home.vmi.HomeIntent
import com.cesoft.cesnostr.home.vmi.HomeState
import com.cesoft.cesnostr.message

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
            //HomeHeaderError(state.error.message(LocalContext.current), isErrorVisible)
            ErrorHeader(state.error.message(LocalContext.current), isErrorVisible)
        }
        else {
            HomeHeaderTitle()
            HomeEventList(state, reduce)
        }
    }
}


