package com.work_out_record

import android.content.Context
import android.preference.PreferenceManager

object QueryPreferences {

    fun getStoredQuery(context: Context, key: String): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(key, "")!!
    }

    fun setStoredQuery(context: Context, key: String, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(key, query)
            .apply()
    }
}