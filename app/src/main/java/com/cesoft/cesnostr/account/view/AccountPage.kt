package com.cesoft.cesnostr.account.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesnostr.account.AccountViewModel
import com.cesoft.cesnostr.account.vmi.AccountIntent
import com.cesoft.cesnostr.account.vmi.AccountState
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.entity.NostrPrivateKey
import com.cesoft.domain.entity.NostrPublicKey

@Composable
fun AccountPage(
    navController: NavController,
    viewModel: AccountViewModel,
) {
    //TODO: Show back arrow !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    MviScreen(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            viewModel.consumeSideEffect(
                sideEffect = sideEffect,
                navController = navController
            )
        },
        onBackPressed = {
            viewModel.execute(AccountIntent.Close)
        },
    ) { state: AccountState ->
        when(state) {
            is AccountState.Loading -> {
                viewModel.execute(AccountIntent.Load)
                LoadingCompo()
            }
            is AccountState.Init -> {
                AccountInit(state = state, reduce = viewModel::execute)
            }
        }
    }
}

//-----------------------------------------------------------------------------
@Preview
@Composable
private fun AccountPage_Preview() {
        val publicKey = NostrPublicKey("npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k")
    val privateKey = NostrPrivateKey("xxxxxxxxxxyyyyyyyyyyzzzzzzzzzzzzzzzzz")
    val m = NostrMetadata(
        npub = "npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k",
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
    AccountInit(state) {}
}
