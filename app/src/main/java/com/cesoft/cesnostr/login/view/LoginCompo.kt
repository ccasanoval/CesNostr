package com.cesoft.cesnostr.login.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.ErrorHeader
import com.cesoft.cesnostr.common.toHumanMessage
import com.cesoft.cesnostr.login.mvi.LoginIntent
import com.cesoft.cesnostr.login.mvi.LoginState
import com.cesoft.cesnostr.message
import com.cesoft.cesnostr.ui.theme.SepMax
import com.cesoft.cesnostr.ui.theme.SepMed
import com.cesoft.cesnostr.ui.theme.SepMin

@Composable
internal fun LoginCompo(
    state: LoginState.Init,
    reduce: (LoginIntent) -> Unit
) {
    val context = LocalContext.current
    val isErrorVisible = remember { mutableStateOf(true) }
    LaunchedEffect(state.error) { isErrorVisible.value = true }
    Surface {
        Column(modifier = Modifier.padding(SepMin)) {
            if(state.error != null && isErrorVisible.value) {
                ErrorHeader(state.error.toHumanMessage(context), isErrorVisible)
                Spacer(Modifier.padding(SepMed))
            }

            HorizontalDivider(modifier = Modifier.padding(SepMax))

            Text(stringResource(R.string.start_as_guest_title))
            Button(onClick = { reduce(LoginIntent.Accept) }) {
                Text(stringResource(R.string.sign_in_nostr))
            }

            HorizontalDivider(modifier = Modifier.padding(SepMax))

            val nsec = remember { mutableStateOf("") }
            Text(stringResource(R.string.sign_in_nostr_title))
            TextField(
                value = nsec.value,
                label = { Text(stringResource(R.string.npubnsec)) },
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { text: String -> nsec.value = text })
            Button(onClick = { reduce(LoginIntent.SignIn(nsec.value)) }) {
                Text(stringResource(R.string.sign_in_nostr))
            }

            HorizontalDivider(modifier = Modifier.padding(SepMax))

            val displayName = remember { mutableStateOf("") }
            Text(stringResource(R.string.create_nostr_acc_title))
            TextField(
                value = displayName.value,
                label = { Text(stringResource(R.string.acc_display_name)) },
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { text: String -> displayName.value = text })
            Button(onClick = { reduce(LoginIntent.SignIn(nsec.value)) }) {
                Text(stringResource(R.string.create_nostr_acc))
            }
        }
    }
}

@Preview
@Composable
private fun LoginInit_Preview() {
    LoginCompo(LoginState.Init(error = Exception("Error Test!"))) {}
}
