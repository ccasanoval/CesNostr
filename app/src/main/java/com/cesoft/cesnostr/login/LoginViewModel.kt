package com.cesoft.cesnostr.login

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.Page
import com.cesoft.cesnostr.home.vmi.HomeSideEffect
import com.cesoft.cesnostr.home.vmi.HomeTransform
import com.cesoft.cesnostr.login.mvi.LoginIntent
import com.cesoft.cesnostr.login.mvi.LoginSideEffect
import com.cesoft.cesnostr.login.mvi.LoginState
import com.cesoft.cesnostr.login.mvi.LoginTransform
import com.cesoft.domain.AppError
import com.cesoft.domain.usecase.SavePrivateKeyUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import rust.nostr.sdk.Client
import rust.nostr.sdk.Filter
import rust.nostr.sdk.PublicKey
import rust.nostr.sdk.Keys
import rust.nostr.sdk.Kind
import rust.nostr.sdk.KindStandard
import rust.nostr.sdk.Metadata
import rust.nostr.sdk.NostrSigner
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savePrivateKey: SavePrivateKeyUC

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
            is LoginIntent.Create -> executeCreate()
            LoginIntent.Accept -> executeAccept()
            LoginIntent.Cancel -> executeCancel()
        }

    private fun executeAccept() = flow {
        //TODO: Save keys to safe prefs
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
            val npub: PublicKey
            val client: Client
//            if (nsec.startsWith("npub")) {
//                npub = PublicKey.parse(nsec)//TODO:????
//                client = Client()
//            }
//            else
            if (nsec.startsWith("nsec")) {
                val keys = Keys.parse(nsec)
                npub = keys.publicKey()
                val signer = NostrSigner.keys(keys)
                client = Client(signer = signer)
            }
            else {
                android.util.Log.e(TAG, "executeSignIn------------ Wrong key: $nsec")
                emit(LoginTransform.GoInit(AppError.InvalidNostrKey))
                return@flow
            }

            //TODO: To use case...
            //TODO: Bigger relay list?
            client.addRelay("wss://relay.damus.io")
            client.addRelay("wss://nostr.bitcoiner.social")
            client.addRelay("wss://nos.lol")
            client.connect()

            val filterMD = Filter()
                .kind(Kind.fromStd(KindStandard.METADATA))
                .author(npub)
                .limit(1u)
            val event = client.fetchEvents(filterMD, Duration.ofSeconds(1L)).toVec().first()
            _nsec = nsec
            emit(LoginTransform.GoSignInSuccess(metadata = Metadata.fromJson(event.content())))
        }
        catch (e: Exception) {
            android.util.Log.e(TAG, "executeSignIn------------ e: $e \n ${e.stackTrace}")
            e.printStackTrace()
            emit(LoginTransform.GoInit(e))
        }
    }

    //TODO: ---------------------------------------------------------------------------------------
    private fun executeCreate() = flow {
        //val keys = Keys.generate()
        //val signer = NostrSigner.keys(keys)
        //val client = Client(signer = signer)
        emit(LoginTransform.GoSignInSuccess())
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