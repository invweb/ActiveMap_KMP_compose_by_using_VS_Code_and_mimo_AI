package com.activemap.web.di

import com.activemap.web.data.LocalStorageLocationRepository
import com.activemap.web.service.WebLocationService
import com.activemap.shared.repository.LocationRepository
import com.activemap.shared.service.LocationService
import org.koin.dsl.module

val webModule = module {
    single<LocationRepository> { LocalStorageLocationRepository() }
    single<LocationService> { WebLocationService() }
}