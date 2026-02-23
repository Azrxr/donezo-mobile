package com.jasawira.donezo.presentation.utils

import com.jasawira.donezo.BuildConfig

/**
 * AppVersionUtils
 * Utility untuk mendapatkan informasi versi aplikasi
 */
object AppVersionUtils {
    fun getVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    fun getVersionCode(): Int {
        return BuildConfig.VERSION_CODE
    }

    fun getAppName(): String {
        return "Donezo"
    }
}

