package com.iq_academy.malika_ai.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */
class SharedPref @Inject constructor(@ApplicationContext val context: Context) {
    val sharedPref = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

    fun saveLogIn(key: String, data: Boolean) {
        sharedPref.edit().putBoolean(key, data).apply()
    }

    fun getLogIn(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun saveLanguage(key: String, data: String) {
        sharedPref.edit().putString(key, data).apply()
    }

    fun getLanguage(key: String): String {
        return sharedPref.getString(key, "uz")!!
    }
}