package com.ishanvohra2.findr.di

import com.ishanvohra2.findr.networking.RetrofitClient
import com.ishanvohra2.findr.repositories.HomeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    single { RetrofitClient(androidContext()) }
}