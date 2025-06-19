package com.cesoft.cesnostr.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cesoft.cesnostr.BuildConfig
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.ui.theme.SepMax
import com.cesoft.cesnostr.ui.theme.SepMed
import kotlinx.coroutines.launch

@Composable
fun MainDrawerMenu(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = SepMax)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(SepMed))

                    /// App header
                    Text(
                        text = stringResource(R.string.app_name),
                        modifier = Modifier.padding(SepMax),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider()

//                    Text("Section 1", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
//                    NavigationDrawerItem(
//                        label = { Text("Item 1") },
//                        selected = false,
//                        onClick = { /* Handle click */ }
//                    )
//                    HorizontalDivider(modifier = Modifier.padding(vertical = SepMed))
//                    Text("Section 2", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)

                    /// Follow
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.menu_follow)) },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Face, contentDescription = null) },
                        onClick = {
                            navController.navigate(Page.Follow.route)
                            scope.launch { drawerState.close() }
                        },
                    )

                    /// Search
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.menu_search)) },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        onClick = { /* Handle click */ },
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = SepMed))

                    /// Account
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.menu_account)) },
                        selected = false,
                        icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                        onClick = {
                            navController.navigate(Page.Account.route)
                            scope.launch { drawerState.close() }
                        }
                    )

                    /// Config
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.menu_settings)) },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        //badge = { Text("20") }, // Placeholder
                        onClick = { /* Handle click */ }
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    ) {
        Toolbar(drawerState, content)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    drawerState: DrawerState,
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    //For using VERSION_NAME -> buildConfig = true  in buildFeatures in gradle
                    val title = stringResource(R.string.app_name) + " " + BuildConfig.VERSION_NAME
                    Text(text = title, color = MaterialTheme.colorScheme.secondary)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    }) {
                        //Icon(Icons.Default.Menu, contentDescription = "Menu")
                        Icon(
                            painter = painterResource(R.mipmap.ic_launcher_round),
                            contentDescription = "",
                            tint = Color.Unspecified
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
