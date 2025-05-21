package com.cesoft.data

import android.content.Context
import com.cesoft.data.pref.readBool
import com.cesoft.data.pref.writeBool
import com.cesoft.domain.repo.PrefsRepositoryContract

class PrefsRepository(
    private val context: Context,
): PrefsRepositoryContract {

    override suspend fun useBiometrics() = context.readBool(PREF_USE_BIOMETRICS, true)
    override suspend fun useBiometrics(value: Boolean) = context.writeBool(PREF_USE_BIOMETRICS, value)

    override suspend fun showBiometricsScreen() = context.readBool(PREF_SHOW_BIOMETRICS, true)
    override suspend fun showBiometricsScreen(value: Boolean) = context.writeBool(PREF_SHOW_BIOMETRICS, value)

    companion object {
        private const val PREF_USE_BIOMETRICS = "PrefUseBiometrics"
        private const val PREF_SHOW_BIOMETRICS = "PrefShowBiometrics"
    }
}