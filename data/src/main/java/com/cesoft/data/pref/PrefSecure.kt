package com.cesoft.data.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.core.content.edit

private const val sharedPrefsFile = "CesNostrSecure"
private val mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
private fun getEncryptedPrefs(context: Context) = EncryptedSharedPreferences.create(
    sharedPrefsFile,
    mainKeyAlias,
    context,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
fun Context.writeSecure(key: String, value: String) {
    val pref: SharedPreferences = getEncryptedPrefs(this)
    pref.edit { putString(key, value) }
}
fun Context.readSecure(key: String): String? {
    val pref: SharedPreferences = getEncryptedPrefs(this)
    return pref.getString(key, null)
}
fun Context.deleteSecure(key: String) {
    val pref: SharedPreferences = getEncryptedPrefs(this)
    pref.edit { remove(key) }
}

/// Fields -----------------------------------------------------------------------------------------
private const val privateKeyField = "CesNostrPrivateKey"
fun Context.setPrivateKey(token: String?) {
    token?.let {
        this.writeSecure(privateKeyField, token)
    } ?: run {
        this.deleteSecure(privateKeyField)
    }
}
fun Context.getPrivateKey() = this.readSecure(privateKeyField)

