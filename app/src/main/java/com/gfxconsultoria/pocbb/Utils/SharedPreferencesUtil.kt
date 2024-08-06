package com.gfxconsultoria.pocbb.Utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesUtil (context: Context){
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    companion object{
        @Volatile
        private var INSTANCE: SharedPreferencesUtil? = null

        fun initialize(context: Context){
            synchronized(this){
                if (INSTANCE == null){
                    INSTANCE = SharedPreferencesUtil(context)
                }
            }
        }

        fun getInstance(): SharedPreferencesUtil{
            return INSTANCE ?: throw IllegalStateException("SharedPreferencesUtil must be initialized")
        }
    }

    fun saveData(key: String, value: String){
        Log.d("SHARED", "Saving $value to $key")
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun readData(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }
}