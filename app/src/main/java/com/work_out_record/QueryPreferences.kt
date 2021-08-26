package com.work_out_record

import android.content.Context
import android.preference.PreferenceManager

object QueryPreferences {

    fun getStoredQuery(context: Context, routineName: String): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(routineName, "")!!
    }

    fun setStoredQuery(context: Context, routineName: String, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(routineName, query)
            .apply()
    }
}