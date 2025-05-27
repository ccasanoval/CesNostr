package com.cesoft.cesnostr.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.login.mvi.LoginIntent
import com.cesoft.cesnostr.login.mvi.LoginSideEffect
import com.cesoft.cesnostr.login.mvi.LoginState
import com.cesoft.cesnostr.login.mvi.LoginTransform
import com.cesoft.cesnostr.view.Page
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.usecase.CreateUserUC
import com.cesoft.domain.usecase.GetKeysUC
import com.cesoft.domain.usecase.GetUserMetadataUC
import com.cesoft.domain.usecase.SavePrivateKeyUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savePrivateKey: SavePrivateKeyUC,
    private val getUserMetadata: GetUserMetadataUC,
    private val getKeys: GetKeysUC,
    private val createUser: CreateUserUC,
): ViewModel(), MviHost<LoginIntent, State<LoginState, LoginSideEffect>> {
    var _nsec = ""

    private val reducer: Reducer<LoginIntent, State<LoginState, LoginSideEffect>> = Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = Dispatchers.Default,
        initialInnerState = LoginState.Init(),
        logger = null,
        intentExecutor = this::executeIntent
    )
    override val state = reducer.state
    override fun execute(intent: LoginIntent) {
        reducer.executeIntent(intent)
    }
    private fun executeIntent(intent: LoginIntent) =
        when(intent) {
            is LoginIntent.SignIn -> executeSignIn(intent.nsec)
            is LoginIntent.Create -> executeCreate(intent.metadata)
            LoginIntent.Accept -> executeAccept()
            LoginIntent.Cancel -> executeCancel()
        }

    private fun executeAccept() = flow {
        if(_nsec.isNotBlank()) {
            savePrivateKey(_nsec)
        }
        emit(LoginTransform.AddSideEffect(LoginSideEffect.GoHome))
    }

    private fun executeCancel() = flow {
        emit(LoginTransform.GoInit())
    }

    private fun executeSignIn(nsec: String) = flow {
        try {
            val npub: String
            if (nsec.startsWith("nsec")) {
                val keys: Result<NostrKeys> = getKeys(nsec)
                if(keys.isSuccess) {
                    _nsec = nsec
                    npub = keys.getOrNull()?.publicKey?.npub ?: ""

                    val res: Result<NostrMetadata> = getUserMetadata(npub)
                    if(res.isSuccess)
                        emit(LoginTransform.GoSignInSuccess(res.getOrNull()))
                    else
                        emit(LoginTransform.GoInit(res.exceptionOrNull()))
                }
                else {
                    emit(LoginTransform.GoInit(AppError.InvalidNostrKey))
                }
            }
            //TODO: Can we login with just public key?
            else {
                android.util.Log.e(TAG, "executeSignIn------------ Wrong key: $nsec")
                emit(LoginTransform.GoInit(AppError.InvalidNostrKey))
            }
        }
        catch (e: Exception) {
            android.util.Log.e(TAG, "executeSignIn------------ e: $e \n ${e.stackTrace}")
            e.printStackTrace()
            emit(LoginTransform.GoInit(e))
        }
    }

    private fun executeCreate(metadata: NostrMetadata) = flow {
        val res: Result<NostrKeys> = createUser(metadata)
        if(res.isSuccess) {
            val keys = res.getOrNull()
            val nsec = keys?.privateKey?.nsec ?: ""
            savePrivateKey(nsec)
            emit(LoginTransform.AddSideEffect(LoginSideEffect.GoHome))
        }
        else {
            val e = res.exceptionOrNull()
            android.util.Log.e(TAG, "executeCreate------------ e: $e")
            emit(LoginTransform.GoInit(e))
        }
    }

    fun consumeSideEffect(
        sideEffect: LoginSideEffect,
        navController: NavController,
        context: Context
    ) {
        when(sideEffect) {
            LoginSideEffect.GoHome -> {
                navController.navigate(Page.Home.route)
            }
        }
    }

    companion object {
        private const val TAG = "LoginVM"
    }
}