package com.example.skkniapp.di

import com.example.skkniapp.data.local.FavoriteCityDbHelper
import com.example.skkniapp.data.location.LocationProvider
import com.example.skkniapp.data.repository.CitySearchRepositoryImpl
import com.example.skkniapp.data.repository.FavoriteCityRepositoryImpl
import com.example.skkniapp.data.repository.WeatherRepositoryImpl
import com.example.skkniapp.domain.repository.CitySearchRepository
import com.example.skkniapp.domain.repository.FavoriteCityRepository
import com.example.skkniapp.domain.repository.WeatherRepository
import org.koin.dsl.module

val dataModule = module {

    single { LocationProvider(get()) }

    single { FavoriteCityDbHelper(get()) }

    single<WeatherRepository> {
        WeatherRepositoryImpl(get())
    }

    single<FavoriteCityRepository> {
        FavoriteCityRepositoryImpl(get())
    }

    single<CitySearchRepository> {
        CitySearchRepositoryImpl(get())
    }
}
