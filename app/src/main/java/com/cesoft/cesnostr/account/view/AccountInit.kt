package com.cesoft.cesnostr.account.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.cesoft.cesnostr.account.vmi.AccountIntent
import com.cesoft.cesnostr.account.vmi.AccountState
import com.cesoft.cesnostr.common.ErrorHeader
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.cesnostr.common.toHumanMessage

@Composable
internal fun AccountInit(
    state: AccountState.Init,
    reduce: (AccountIntent) -> Unit
) {
    Column {
        if(state.wait) LoadingCompo(background = false)

        val isErrorVisible = remember { mutableStateOf(true) }
        LaunchedEffect(state.error) { isErrorVisible.value = true }
        if(state.error != null && isErrorVisible.value) {
            ErrorHeader(state.error.toHumanMessage(LocalContext.current), isErrorVisible)
        }
        else {
            Text(text = "Account")
        }
    }
}