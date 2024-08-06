package com.gfxconsultoria.pocbb

import com.gfxconsultoria.pocbb.Presenter.QrPresenter
import com.gfxconsultoria.pocbb.Services.PixManager
import com.gfxconsultoria.pocbb.Utils.SharedPreferencesUtil
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.factory
import org.koin.dsl.module

val appModule = module{
    single { PixManager(androidContext()) }
    factory{ SharedPreferencesUtil(androidContext()) }
    viewModel{ QrPresenter(get(), androidContext()) }
}