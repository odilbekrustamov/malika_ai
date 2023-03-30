package com.iq_academy.malika_ai.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPref @Inject constructor(@ApplicationContext val context: Context) {
    val sharedPref = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

    fun saveLogIn(key: String, data: Boolean) {
        sharedPref.edit().putBoolean(key, data).apply()
    }

    fun getLogIn(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }
}