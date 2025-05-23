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
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.ErrorHeader
import com.cesoft.cesnostr.login.LoginViewModel
import com.cesoft.cesnostr.login.mvi.LoginIntent
import com.cesoft.cesnostr.login.mvi.LoginState
import com.cesoft.cesnostr.message
import com.cesoft.cesnostr.ui.theme.SepMax
import com.cesoft.cesnostr.ui.theme.SepMed
import com.cesoft.cesnostr.ui.theme.SepMin

@Composable
fun LoginPage(
    navController: NavController,
    viewModel: LoginViewModel,
) {
    val context = LocalContext.current
    MviScreen(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            viewModel.consumeSideEffect(
                sideEffect = sideEffect,
                navController = navController,
                context = context
            )
        },
        onBackPressed = {},
    ) { state: LoginState ->
        when(state) {
            is LoginState.Init -> {
                LoginCompo(state = state, reduce = viewModel::execute)
            }
            is LoginState.SignInSuccess -> {
                SignInSuccess(state = state, reduce = viewModel::execute)
            }
        }
    }
}

@Preview
@Composable
private fun LoginInit_Preview() {
    LoginCompo(LoginState.Init(error = Exception("Error Test!"))) {}
}