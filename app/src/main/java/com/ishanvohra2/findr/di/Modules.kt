package com.ishanvohra2.findr.di

import com.ishanvohra2.findr.datastore.DataStoreManager
import com.ishanvohra2.findr.networking.RetrofitClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    single { RetrofitClient(androidContext()) }
}

val datastoreModule = module {
    single { DataStoreManager.getInstance(androidContext()) }
}