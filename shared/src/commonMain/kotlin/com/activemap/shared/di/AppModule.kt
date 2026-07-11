package com.activemap.shared.di

import com.activemap.shared.viewmodel.LocationViewModel
import org.koin.dsl.module

val appModule = module {
    single { LocationViewModel(get(), get()) }
}