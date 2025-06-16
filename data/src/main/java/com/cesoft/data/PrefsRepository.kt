package com.cesoft.data

import android.content.Context
import com.cesoft.data.pref.deleteSecure
import com.cesoft.data.pref.readSecure
import com.cesoft.data.pref.writeSecure
import com.cesoft.domain.repo.PrefsRepositoryContract
import javax.inject.Inject

class PrefsRepository @Inject constructor(
    private val context: Context,
): PrefsRepositoryContract {

    override suspend fun readPrivateKey(): String? {
        return context.readSecure(PREF_PRIVATE_KEY)
    }

    override suspend fun writePrivateKey(value: String?) {
        if(value.isNullOrBlank()) {
            context.deleteSecure(PREF_PRIVATE_KEY)
        }
        else {
            context.writeSecure(PREF_PRIVATE_KEY, value)
        }
    }

    companion object {
        private const val PREF_PRIVATE_KEY = "CesNostrPrivateKey"
    }
}