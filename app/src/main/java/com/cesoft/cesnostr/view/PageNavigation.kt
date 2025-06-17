package com.cesoft.cesnostr.view

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cesoft.cesnostr.account.AccountViewModel
import com.cesoft.cesnostr.account.view.AccountPage
import com.cesoft.cesnostr.author.AuthorViewModel
import com.cesoft.cesnostr.author.view.AuthorPage
import com.cesoft.cesnostr.home.view.HomePage
import com.cesoft.cesnostr.home.HomeViewModel
import com.cesoft.cesnostr.login.LoginViewModel
import com.cesoft.cesnostr.login.view.LoginPage
import com.cesoft.data.pref.getPrivateKey

sealed class Page(val route: String) {
    data object Home: Page("home")
    data object Login: Page("login")
    data object Account: Page("account")
    //data class Author(): Page("author")
    data object Author: Page("author/{npub}") {
        private const val ARG_NPUB = "npub"
        fun createRoute(npub: String) = "author/$npub"
        fun getNpub(savedStateHandle: SavedStateHandle): String? =
            savedStateHandle.get<String>(ARG_NPUB)
    }
}

@Composable
fun PageNavigation() {
    val navController = rememberNavController()
    val nsec = LocalContext.current.getPrivateKey()
    val initialPage = if(nsec.isNullOrBlank()) Page.Login.route else Page.Home.route
    MainDrawerMenu(navController) { innerPadding ->
        NavHost(navController, initialPage, modifier = Modifier.padding(innerPadding)) {
            composable(route = Page.Login.route) {
                LoginPage(navController, hiltViewModel<LoginViewModel>())
            }
            composable(route = Page.Home.route) {
                HomePage(navController, hiltViewModel<HomeViewModel>())
            }
            composable(route = Page.Account.route) {
                AccountPage(navController, hiltViewModel<AccountViewModel>())
            }
            composable(route = Page.Author.route) {
                AuthorPage(navController, hiltViewModel<AuthorViewModel>())
            }
        }
    }
}
