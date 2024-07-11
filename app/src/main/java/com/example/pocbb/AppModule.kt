package com.example.pocbb

import com.example.poc_bb.Services.PixManager
import com.example.poc_bb.Utils.SharedPreferencesUtil
import com.example.pocbb.Presenter.MainPresenter
import com.example.pocbb.Presenter.QrPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.factory
import org.koin.dsl.module

val appModule = module{
    single { PixManager(androidContext()) }
    factory{ SharedPreferencesUtil(androidContext())}
    viewModel{QrPresenter(get(), androidContext())}
}