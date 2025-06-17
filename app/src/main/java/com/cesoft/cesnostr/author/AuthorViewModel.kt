package com.cesoft.cesnostr.author

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesnostr.author.vmi.AuthorIntent
import com.cesoft.cesnostr.author.vmi.AuthorSideEffect
import com.cesoft.cesnostr.author.vmi.AuthorState
import com.cesoft.cesnostr.author.vmi.AuthorTransform
import com.cesoft.cesnostr.view.Page
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.usecase.GetKeysUC
import com.cesoft.domain.usecase.GetUserMetadataUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class AuthorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserMetadata: GetUserMetadataUC,
): ViewModel(), MviHost<AuthorIntent, State<AuthorState, AuthorSideEffect>> {
    val npub = Page.Author.getNpub(savedStateHandle)
    init {
        android.util.Log.e("AA", "------------------ $npub")
    }

    private val reducer: Reducer<AuthorIntent, State<AuthorState, AuthorSideEffect>> = Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = Dispatchers.Default,
        initialInnerState = AuthorState.Loading,
        logger = null,
        intentExecutor = this::executeIntent
    )
    override val state = reducer.state
    override fun execute(intent: AuthorIntent) {
        reducer.executeIntent(intent)
    }
    private fun executeIntent(intent: AuthorIntent) =
        when(intent) {
            AuthorIntent.Close -> executeClose()
            AuthorIntent.Load -> executeLoad()
            AuthorIntent.Reload -> executeReload()
        }

    private fun executeClose() = flow {
        emit(AuthorTransform.AddSideEffect(AuthorSideEffect.Close))
    }

    private fun executeReload() = flow {
        emit(AuthorTransform.GoReload)
    }

    private fun executeLoad() = flow {
        emit(fetch())
    }

    private suspend fun fetch(): AuthorTransform.GoInit {
        if(npub == null) {
            return AuthorTransform.GoInit(error = AppError.InvalidMetadata)
        }
        val res: Result<NostrMetadata> = getUserMetadata(npub)
        val meta = res.getOrNull()
        return if(res.isSuccess && meta != null) {
            AuthorTransform.GoInit(metadata = meta)
        } else {
            AuthorTransform.GoInit(error = res.exceptionOrNull() ?: AppError.InvalidMetadata)
        }
    }

    fun consumeSideEffect(
        sideEffect: AuthorSideEffect,
        navController: NavController,
        context: Context
    ) {
        when(sideEffect) {
            AuthorSideEffect.Close -> {
                navController.navigate(Page.Home.route)
            }
        }
    }

    companion object {
        private const val TAG = "AuthorVM"
    }
}