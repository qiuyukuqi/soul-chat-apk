package com.soul.app.api

import android.content.Context
import android.content.SharedPreferences

object ApiKeyManager {
    private const val PREFS = "soul_prefs"
    private const val KEY_API_KEY = "zhipu_api_key"

    fun get(context: Context): String {
        return getPrefs(context).getString(KEY_API_KEY, "") ?: ""
    }

    fun save(context: Context, key: String) {
        getPrefs(context).edit().putString(KEY_API_KEY, key).apply()
    }

    fun hasKey(context: Context): Boolean = get(context).isNotBlank()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }
}
