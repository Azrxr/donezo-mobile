package com.jasawira.donezo.presentation.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * UserPreferencesManager
 * Mengelola user preferences seperti username, theme preference, etc
 */
class UserPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "donezo_user_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_THEME_MODE = "theme_mode"  // light, dark, system
        private const val KEY_COLOR_PRESET = "color_preset"  // 0-9
        private const val DEFAULT_USERNAME = "Sobat"
        private const val DEFAULT_THEME_MODE = "system"
        private const val DEFAULT_COLOR_PRESET = 6  // Pastel Mint
    }

    // USERNAME
    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, DEFAULT_USERNAME) ?: DEFAULT_USERNAME
    }

    fun setUsername(username: String) {
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply()
    }

    // THEME MODE (light, dark, system)
    fun getThemeMode(): String {
        return sharedPreferences.getString(KEY_THEME_MODE, DEFAULT_THEME_MODE) ?: DEFAULT_THEME_MODE
    }

    fun setThemeMode(themeMode: String) {
        sharedPreferences.edit().putString(KEY_THEME_MODE, themeMode).apply()
    }

    // COLOR PRESET (0-9)
    fun getColorPreset(): Int {
        return sharedPreferences.getInt(KEY_COLOR_PRESET, DEFAULT_COLOR_PRESET)
    }

    fun setColorPreset(colorPresetId: Int) {
        sharedPreferences.edit().putInt(KEY_COLOR_PRESET, colorPresetId).apply()
    }
}

