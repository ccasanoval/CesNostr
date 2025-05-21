package com.cesoft.cesnostr.account.vmi

import com.adidas.mvi.Intent

sealed class AccountIntent: Intent {
    data object Close: AccountIntent()
    data object Load: AccountIntent()
    data object Reload: AccountIntent()
}