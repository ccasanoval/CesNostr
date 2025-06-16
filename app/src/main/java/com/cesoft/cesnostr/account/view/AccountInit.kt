package com.cesoft.cesnostr.account.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.account.vmi.AccountIntent
import com.cesoft.cesnostr.account.vmi.AccountState
import com.cesoft.cesnostr.common.ErrorHeader
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.cesnostr.common.toHumanMessage
import com.cesoft.cesnostr.ui.theme.SepMax
import com.cesoft.cesnostr.ui.theme.SepMed
import com.cesoft.cesnostr.ui.theme.SepMin
import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.entity.NostrPrivateKey
import com.cesoft.domain.entity.NostrPublicKey

@Composable
internal fun AccountInit(
    state: AccountState.Init,
    reduce: (AccountIntent) -> Unit
) {
    Column(modifier = Modifier.padding(SepMed)) {
        if(state.wait) LoadingCompo(background = false)
        val isErrorVisible = remember { mutableStateOf(true) }
        LaunchedEffect(state.error) { isErrorVisible.value = true }
        if(state.error != null && isErrorVisible.value) {
            ErrorHeader(state.error.toHumanMessage(LocalContext.current), isErrorVisible)
        }
        else {
            LazyColumn {
                item {
                    KeyComposable(
                        stringResource(R.string.private_key),
                        state.keys.privateKey.nsec,
                        state.nsecImg
                    )
                }
                item { Spacer(modifier = Modifier.height(SepMax)) }
                item {
                    KeyComposable(
                        stringResource(R.string.public_key),
                        state.keys.publicKey.npub,
                        state.npubImg
                    )
                }
                item { Spacer(modifier = Modifier.height(SepMax)) }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("account", text)
    clipboard.setPrimaryClip(clip)
}

@Composable
private fun KeyComposable(title: String, key: String, imagePath: String) {
    val context = LocalContext.current
    Spacer(modifier = Modifier.height(SepMax))
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(SepMin)
    )
    Text(
        text = key,
        modifier = Modifier.padding(SepMin).clickable { copyToClipboard(context, key) }
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
//        Button(
//            onClick = { copyToClipboard(context, key) },
//            modifier = Modifier.padding(SepMed)
//        ) {
//            Text(stringResource(R.string.copy_to_clipboard))
//        }
        //android.util.Log.e("AA", "------------ $imagePath")
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imagePath)
                .crossfade(true)
                .build(),
            placeholder = painterResource(android.R.drawable.ic_menu_report_image),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(200.dp)//.border(1.dp, Color.Red),
            //colorFilter = ColorFilter.tint(Color.Unspecified)
        )
    }
}

//-----------------------------------------------------------------------------
@Preview
@Composable
private fun AccountInit_Preview() {
    val publicKey = NostrPublicKey("1e67de3754171071d3cf9b44b6e546bd94fd0a2ca3fb4dbbb1b054685c9116e4")
    val privateKey = NostrPrivateKey("1e67de3754171071d3cf9b44b6e546bd94fd0a2ca3fb4dbbb1b054685c9116e4")
    val m = NostrMetadata(
        npub = "1e67de3754171071d3cf9b44b6e546bd94fd0a2ca3fb4dbbb1b054685c9116e4",
        name = "CESoft",
        displayName = "CES Soft",
        about = "Test testest setae stest etse test",
        website = "https://cortados.freevar.com",
        picture = "https://cortados.freevar.com/web/front/images/scorpion_s.png",
        banner = "https://cortados.freevar.com/web/front/images/scorpion_s.png",
        lud06 = "LNURL de cesoft",
        lud16 = "cesoft@walletofsatoshi.com",
        nip05 = "Nip 05",
    )
    val state = AccountState.Init(
        keys = NostrKeys(publicKey, privateKey),
        metadata = m,
        nsecImg = "",
        npubImg = "",
    )
    Surface(modifier = Modifier.fillMaxSize()) {
        AccountInit(state) {}
    }
}
