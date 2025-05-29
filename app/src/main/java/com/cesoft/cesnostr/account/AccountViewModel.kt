package com.cesoft.cesnostr.account

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.account.view.AccountInit
import com.cesoft.cesnostr.account.vmi.AccountIntent
import com.cesoft.cesnostr.account.vmi.AccountSideEffect
import com.cesoft.cesnostr.account.vmi.AccountState
import com.cesoft.cesnostr.account.vmi.AccountTransform
import com.cesoft.cesnostr.view.Page
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.NostrKeys
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
        val nsec: String? = readPrivateKey()
        if(nsec == null || !nsec.contains("nsec")) {
            return AccountTransform.GoInit(error = AppError.InvalidNostrKey)
        }
        val res: Result<NostrKeys> = getKeys(nsec)
        val keys = res.getOrNull()
        if(res.isFailure || keys == null) {
            return AccountTransform.GoInit(error = AppError.InvalidNostrKey)//TODO: Different error
        }
        return AccountTransform.GoInit(keys = keys)

//        catch (e: Exception) {
//            Log.e(TAG, "fetch:failure:---------------- $e")
//            return AccountTransform.GoInit(error = e)
//        }
    }


    fun consumeSideEffect(
        sideEffect: AccountSideEffect,
        navController: NavController,
        context: Context
    ) {
        when(sideEffect) {
            AccountSideEffect.Start -> {
                navController.navigate(Page.Home.route)
            }
            AccountSideEffect.Close -> {
                (context as Activity).finish()
            }
        }
    }

    companion object {
        private const val TAG = "AccountVM"
    }
}