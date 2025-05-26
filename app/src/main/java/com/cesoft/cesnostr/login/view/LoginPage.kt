package com.cesoft.cesnostr.login.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesnostr.login.LoginViewModel
import com.cesoft.cesnostr.login.mvi.LoginState

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