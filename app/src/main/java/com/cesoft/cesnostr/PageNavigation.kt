package com.cesoft.cesnostr

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cesoft.cesnostr.home.view.HomePage
import com.cesoft.cesnostr.home.HomeViewModel
import com.cesoft.cesnostr.login.LoginViewModel
import com.cesoft.cesnostr.login.view.LoginPage
import com.cesoft.data.pref.getPrivateKey

sealed class Page(val route: String) {
    data object Home: Page("home")
    data object Login: Page("login")
}

@Composable
fun PageNavigation(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    val nsec = LocalContext.current.getPrivateKey()
    val initialPage = if(nsec.isNullOrBlank()) Page.Login.route else Page.Home.route
    NavHost(navController, initialPage, modifier = Modifier.padding(innerPadding)) {
        composable(route = Page.Login.route) {
            LoginPage(navController, hiltViewModel<LoginViewModel>())
        }
        composable(route = Page.Home.route) {
            HomePage(navController, hiltViewModel<HomeViewModel>())
        }
//        composable(route = Page.Map.route) {
//            MapPage(navController, hiltViewModel<MapViewModel>())
//        }
    }
}
