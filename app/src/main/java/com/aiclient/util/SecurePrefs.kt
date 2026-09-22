package com.aiclient.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePrefs {
    private const val FILE = "secure_prefs"
    private const val KEY_API = "api_key"
    private const val KEY_URL = "base_url"
    private const val KEY_MODEL = "model"

    const val DEFAULT_URL = "https://api.metisai.ir/openai/v1/"
    const val DEFAULT_MODEL = "gpt-5.5"

    private fun prefs(ctx: Context) = EncryptedSharedPreferences.create(
        ctx,
        FILE,
        MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getApiKey(ctx: Context): String = prefs(ctx).getString(KEY_API, "") ?: ""
    fun setApiKey(ctx: Context, v: String) = prefs(ctx).edit().putString(KEY_API, v).apply()

    fun getBaseUrl(ctx: Context): String = prefs(ctx).getString(KEY_URL, DEFAULT_URL) ?: DEFAULT_URL
    fun setBaseUrl(ctx: Context, v: String) = prefs(ctx).edit().putString(KEY_URL, v).apply()

    fun getModel(ctx: Context): String = prefs(ctx).getString(KEY_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
    fun setModel(ctx: Context, v: String) = prefs(ctx).edit().putString(KEY_MODEL, v).apply()
}
