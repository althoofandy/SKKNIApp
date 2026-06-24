package com.example.skkniapp

import android.app.Application
import com.example.skkniapp.data.di.dataModule
import com.example.skkniapp.di.viewModelModule
import com.example.skkniapp.security.RaspGuard
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SkkniApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SkkniApp)
            modules(dataModule, viewModelModule)
        }
        RaspGuard.install(this, isProductionBuild = !BuildConfig.DEBUG)
    }
}
