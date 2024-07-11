package com.example.poc_bb.Utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesUtil (context: Context){
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String){
        Log.d("SHARED", "Saving $value to $key")
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun readData(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }
}