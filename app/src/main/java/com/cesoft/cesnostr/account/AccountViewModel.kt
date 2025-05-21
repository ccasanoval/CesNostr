package com.cesoft.cesnostr.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.account.vmi.AccountIntent
import com.cesoft.cesnostr.account.vmi.AccountSideEffect
import com.cesoft.cesnostr.account.vmi.AccountState
import com.cesoft.cesnostr.account.vmi.AccountTransform
import com.cesoft.domain.AppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import rust.nostr.sdk.Client
import rust.nostr.sdk.Filter
import rust.nostr.sdk.Keys
import rust.nostr.sdk.Kind
import rust.nostr.sdk.KindStandard
import rust.nostr.sdk.LogLevel
import rust.nostr.sdk.Metadata
import rust.nostr.sdk.NostrSigner
import rust.nostr.sdk.PublicKey
import rust.nostr.sdk.initLogger
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    //private val getByCounty: GetByCountyUC
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
        try {
            initLogger(LogLevel.INFO)

            //val privKeyCes = Keys.parse("nsec1dh2c86ga0ajrcgaye0zr53h7nnxlfe67dxqnvhmcqld62jx8rk7qnvzort")
            //val keys = Keys.generate()



            //else {
                val e = AppError.NotFound
                Log.e(TAG, "fetch:e:---------------- $e")
                return AccountTransform.GoInit(error = e)
            //}
        }
        catch (e: Exception) {
            Log.e(TAG, "fetch:failure:---------------- $e")
            return AccountTransform.GoInit(error = e)
        }
    }

    companion object {
        private const val TAG = "AccountVM"
    }
}