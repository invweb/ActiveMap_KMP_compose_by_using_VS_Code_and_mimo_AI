package com.activemap.android.di

import com.activemap.android.data.LocationDatabase
import com.activemap.android.data.RoomLocationRepository
import com.activemap.android.service.AndroidLocationService
import com.activemap.shared.repository.LocationRepository
import com.activemap.shared.service.LocationService
import org.koin.dsl.module

val androidModule = module {
    single { LocationDatabase.getDatabase(get()) }
    single<LocationRepository> { RoomLocationRepository(get<LocationDatabase>().locationDao()) }
    single<LocationService> { AndroidLocationService(get()) }
}