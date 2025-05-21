package com.cesoft.cesnostr

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cesoft.cesnostr.home.view.HomePage
import com.cesoft.cesnostr.home.HomeViewModel


sealed class Page(val route: String) {
    data object Home: Page("home")
}

@Composable
fun PageNavigation(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    NavHost(navController, Page.Home.route, modifier = Modifier.padding(innerPadding)) {
        composable(route = Page.Home.route) {
            HomePage(navController, hiltViewModel<HomeViewModel>())
        }
//        composable(route = Page.Map.route) {
//            MapPage(navController, hiltViewModel<MapViewModel>())
//        }
    }
}
