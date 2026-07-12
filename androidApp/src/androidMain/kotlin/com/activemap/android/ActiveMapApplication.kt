package com.activemap.android

import android.app.Application
import com.activemap.shared.di.appModule
import com.activemap.android.di.androidModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ActiveMapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ActiveMapApplication)
            modules(appModule, androidModule)
        }
    }
}
