package com.cesoft.cesnostr.login.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.ErrorHeader
import com.cesoft.cesnostr.common.toHumanMessage
import com.cesoft.cesnostr.login.mvi.LoginIntent
import com.cesoft.cesnostr.login.mvi.LoginState
import com.cesoft.cesnostr.ui.theme.SepMax
import com.cesoft.cesnostr.ui.theme.SepMed
import com.cesoft.cesnostr.ui.theme.SepMin
import com.cesoft.domain.entity.NostrMetadata
import io.github.g00fy2.quickie.ScanQRCode

@Composable
internal fun LoginCompo(
    state: LoginState.Init,
    reduce: (LoginIntent) -> Unit
) {
    val context = LocalContext.current
    val isErrorVisible = remember { mutableStateOf(true) }
    val useAccountVisible = remember { mutableStateOf(false) }
    val createAccountVisible = remember { mutableStateOf(false) }
    LaunchedEffect(state.error) { isErrorVisible.value = true }
    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(SepMin)
        ) {
            if(state.error != null && isErrorVisible.value) {
                ErrorHeader(state.error.toHumanMessage(context), isErrorVisible)
                Spacer(Modifier.padding(SepMed))
            }
            Spacer(Modifier.height(SepMax))

            //---------------------------------------------------------------------------------
            Spacer(Modifier.height(SepMax))
            ButtonMain(
                label = R.string.start_as_guest_title,
                onClick = { reduce(LoginIntent.Accept) }
            )
            HorizontalDivider(Modifier.padding(SepMax))

            //---------------------------------------------------------------------------------
            Spacer(Modifier.height(SepMax))
            ButtonMain(
                label = R.string.sign_in_nostr_title,
                onClick = {
                    useAccountVisible.value = !useAccountVisible.value
                    createAccountVisible.value = false
                }
            )
            if(useAccountVisible.value) {
                Spacer(Modifier.height(SepMax))
                val nsec = remember { mutableStateOf("") }
//                Text(
//                    text = stringResource(R.string.sign_in_nostr_title),
//                    modifier = Modifier.fillMaxWidth(.9f)
//                )
                Texto(nsec, R.string.nsec)
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        modifier = Modifier.padding(SepMin),
                        onClick = { reduce(LoginIntent.SignIn(nsec.value)) }
                    ) {
                        Text(text = stringResource(R.string.sign_in_nostr))
                    }
                }
                Text("O escanea:")

            }
            HorizontalDivider(Modifier.padding(SepMax))

            //---------------------------------------------------------------------------------
            Spacer(Modifier.height(SepMax))
            ButtonMain(
                label = R.string.create_nostr_acc_title,
                onClick = {
                    useAccountVisible.value = false
                    createAccountVisible.value = !createAccountVisible.value
                }
            )
            if(createAccountVisible.value) {
                val name = remember { mutableStateOf("") }
                val displayName = remember { mutableStateOf("") }
                val about = remember { mutableStateOf("") }
                val website = remember { mutableStateOf("") }
                val wallet = remember { mutableStateOf("") }
                val lnurl = remember { mutableStateOf("") }
                val picture = remember { mutableStateOf("") }
                val banner = remember { mutableStateOf("") }
                val nip05 = remember { mutableStateOf("") }
                Spacer(Modifier.height(SepMax))
//                Text(
//                    text = stringResource(R.string.create_nostr_acc_title),
//                    modifier = Modifier.fillMaxWidth(.9f)
//                )
                LazyColumn {
                    item { Texto(name, R.string.acc_name) }
                    item { Texto(displayName, R.string.acc_display_name) }
                    item { Texto(about, R.string.acc_about) }
                    item { Texto(wallet, R.string.acc_lud16) }
                    item { Texto(lnurl, R.string.acc_lud06) }
                    item { Spacer(Modifier.height(SepMax)) }
                }
                val onClick = {
                    val metadata = NostrMetadata(
                        npub = "",
                        name = name.value,
                        displayName = displayName.value,
                        about = about.value,
                        website = website.value,
                        picture = picture.value,
                        banner = banner.value,
                        lud06 = lnurl.value,
                        lud16 = wallet.value,
                        nip05 = nip05.value,
                    )
                    reduce(LoginIntent.Create(metadata))
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        modifier = Modifier.padding(SepMin),
                        onClick = onClick
                    ) {
                        Text(stringResource(R.string.create_nostr_acc))
                    }
                }
            }
            Spacer(Modifier.height(SepMax))
        }
    }
}

@Composable
private fun Texto(
    value: MutableState<String>,
    @StringRes label: Int,
) {
    TextField(
        value = value.value,
        label = { Text(stringResource(label)) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = SepMed),
        onValueChange = { text: String -> value.value = text }
    )
}

@Composable
private fun ButtonMain(
    @StringRes label: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(stringResource(label))
    }
}

@Composable
fun GetQRCodeExample() {
    val scanQrCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        //TODO: Use code
        android.util.Log.e("AAA", "---------- Código: $result")
    }
    Button(onClick = { scanQrCodeLauncher.launch(null) }) {
        Text("Escanea el codigo")
    }
}

//--------------------------------------------------------------------------------------------------
@Preview
@Composable
private fun LoginInit_Preview() {
    LoginCompo(LoginState.Init(error = Exception("Error Test! and the snake is long, seven miles"))) {}
}
