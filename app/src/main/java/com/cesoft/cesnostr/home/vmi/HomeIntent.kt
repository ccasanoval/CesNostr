package com.cesoft.cesnostr.home.vmi

import com.adidas.mvi.Intent

sealed class HomeIntent: Intent {
    data object Close: HomeIntent()
    data object Load: HomeIntent()
    data object Reload: HomeIntent()
}