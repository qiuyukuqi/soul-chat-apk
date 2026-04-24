package com.soul.app.api

import android.content.Context
import android.content.SharedPreferences

object ApiKeyManager {
    private const val PREFS = "soul_prefs"
    private const val KEY_API_KEY = "api_key"
    private const val KEY_MODEL = "model_name"

    fun get(context: Context): String {
        return getPrefs(context).getString(KEY_API_KEY, "") ?: ""
    }

    fun save(context: Context, key: String) {
        getPrefs(context).edit().putString(KEY_API_KEY, key).apply()
    }

    fun hasKey(context: Context): Boolean = get(context).isNotBlank()

    fun getModel(context: Context): String {
        return getPrefs(context).getString(KEY_MODEL, "glm-4-flash") ?: "glm-4-flash"
    }

    fun saveModel(context: Context, model: String) {
        getPrefs(context).edit().putString(KEY_MODEL, model).apply()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }
}
