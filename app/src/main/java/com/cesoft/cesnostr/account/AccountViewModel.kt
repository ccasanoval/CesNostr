package com.cesoft.cesnostr.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.account.vmi.AccountIntent
import com.cesoft.cesnostr.account.vmi.AccountSideEffect
import com.cesoft.cesnostr.account.vmi.AccountState
import com.cesoft.cesnostr.account.vmi.AccountTransform
import com.cesoft.cesnostr.view.Page
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrPrivateKey
import com.cesoft.domain.entity.NostrPublicKey
import com.cesoft.domain.usecase.CreateQrImageUC
import com.cesoft.domain.usecase.GetKeysUC
import com.cesoft.domain.usecase.ReadPrivateKeyUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val readPrivateKey: ReadPrivateKeyUC,
    private val getKeys: GetKeysUC,
    private val createQrImage: CreateQrImageUC,
): ViewModel(), MviHost<AccountIntent, State<AccountState, AccountSideEffect>> {

    private val reducer: Reducer<AccountIntent, State<AccountState, AccountSideEffect>> = Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = Dispatchers.Default,
        initialInnerState = AccountState.Loading,
        logger = null,
        intentExecutor = this::executeIntent
    )
    override val state = reducer.state
    override fun execute(intent: AccountIntent) {
        reducer.executeIntent(intent)
    }
    private fun executeIntent(intent: AccountIntent) =
        when(intent) {
            AccountIntent.Close -> executeClose()
            AccountIntent.Load -> executeLoad()
            AccountIntent.Reload -> executeReload()
        }

    private fun executeClose() = flow {
        emit(AccountTransform.AddSideEffect(AccountSideEffect.Close))
    }

    private fun executeReload() = flow {
        emit(AccountTransform.GoReload)
    }

    private fun executeLoad() = flow {
        emit(fetch())
    }

    private suspend fun fetch(): AccountTransform.GoInit {
        //delay(3_000)
        val nsec: String? = readPrivateKey()
        if(nsec == null || !nsec.contains("nsec")) {
            return AccountTransform.GoInit(error = AppError.InvalidNostrKey)
        }
        val res: Result<NostrKeys> = getKeys(nsec)
        val keys = res.getOrNull()
        if(res.isFailure || keys == null) {
            val nsecImg = createQrImage(nsec).getOrNull() ?: ""
            return AccountTransform.GoInit(
                keys = NostrKeys(NostrPublicKey("-"), NostrPrivateKey(nsec)),
                nsecImg = nsecImg,
                error = AppError.InvalidNostrKey
            )
        }
        val nsecImg = createQrImage(keys.privateKey.nsec).getOrNull() ?: ""
        val npubImg = createQrImage(keys.publicKey.npub).getOrNull() ?: ""
        return AccountTransform.GoInit(
            keys = keys,
            nsecImg = nsecImg,
            npubImg = npubImg
        )
    }

    fun consumeSideEffect(
        sideEffect: AccountSideEffect,
        navController: NavController,
    ) {
        when(sideEffect) {
            AccountSideEffect.Close -> {
                navController.navigate(Page.Home.route)
            }
        }
    }

    companion object {
        private const val TAG = "AccountVM"
    }
}