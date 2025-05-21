package com.cesoft.domain.repo

interface PrefsRepositoryContract {
    suspend fun useBiometrics(): Boolean
    suspend fun useBiometrics(value: Boolean)
    suspend fun showBiometricsScreen(): Boolean
    suspend fun showBiometricsScreen(value: Boolean)
}