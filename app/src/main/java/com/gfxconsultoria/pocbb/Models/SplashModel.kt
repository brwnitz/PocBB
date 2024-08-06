package com.gfxconsultoria.pocbb.Models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gfxconsultoria.pocbb.Utils.SharedPreferencesUtil
import kotlinx.coroutines.launch

class SplashModel : ViewModel() {
    private val sharedPreferencesUtil = SharedPreferencesUtil.getInstance()

    var showSplashScreen: Boolean = sharedPreferencesUtil.readData("showSplashScreen","true").toBoolean()!!
        private set

    fun setSplashScreenShow(){
        viewModelScope.launch {
            sharedPreferencesUtil.saveData("showSplashScreen", "false")
            showSplashScreen = false
        }
    }
}