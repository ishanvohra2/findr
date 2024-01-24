package com.ishanvohra2.findr

import android.app.Application
import com.ishanvohra2.findr.di.datastoreModule
import com.ishanvohra2.findr.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FindrApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FindrApplication)
            modules(networkModule, datastoreModule)
        }
    }

}