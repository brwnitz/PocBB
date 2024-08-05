package com.example.pocbb.Models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SplashModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplashModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}