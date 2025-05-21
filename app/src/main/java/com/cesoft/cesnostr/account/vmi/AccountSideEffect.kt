package com.cesoft.cesnostr.account.vmi

sealed class AccountSideEffect {
    data object Start: AccountSideEffect()
    data object Close: AccountSideEffect()
}
