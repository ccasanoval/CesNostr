package com.cesoft.cesnostr.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cesoft.cesnostr.ui.theme.CesNostrTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CesNostrTheme {
                MainDrawerMenu { innerPadding ->
                    PageNavigation(innerPadding)
                }
            }
        }
    }
}