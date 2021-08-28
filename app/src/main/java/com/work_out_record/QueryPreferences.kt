package com.work_out_record

import android.content.Context
import android.preference.PreferenceManager

object QueryPreferences {   //공유 프리퍼런스, 간단한 데이터 보존에 사용되며 키와 값으로 데이터를 저장한다.
                            //해당 저장 파일은 앱의 샌드박스에 저장되고, 따라서 암호 등의 민감한 정보는 저장하지 않는것이 좋다.
    fun getStoredQuery(context: Context, key: String): String { //데이터를 불러오는 함수
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(key, "")!!
    }

    fun setStoredQuery(context: Context, key: String, query: String) {  //데이터를 저장하는 함수
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(key, query)
            .apply()
    }
}