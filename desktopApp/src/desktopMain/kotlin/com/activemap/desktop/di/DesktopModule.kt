package com.activemap.desktop.di

import com.activemap.desktop.data.JsonFileLocationRepository
import com.activemap.desktop.service.DesktopLocationService
import com.activemap.shared.repository.LocationRepository
import com.activemap.shared.service.LocationService
import org.koin.dsl.module

val desktopModule = module {
    single<LocationRepository> { JsonFileLocationRepository() }
    single<LocationService> { DesktopLocationService() }
}