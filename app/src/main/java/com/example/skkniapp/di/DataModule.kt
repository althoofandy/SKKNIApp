package com.example.skkniapp.di

import com.example.skkniapp.data.local.AppDatabase
import com.example.skkniapp.data.location.LocationProvider
import com.example.skkniapp.data.repository.CitySearchRepositoryImpl
import com.example.skkniapp.data.repository.FavoriteCityRepositoryImpl
import com.example.skkniapp.data.repository.ReverseGeocodingRepositoryImpl
import com.example.skkniapp.data.repository.WeatherRepositoryImpl
import com.example.skkniapp.domain.repository.CitySearchRepository
import com.example.skkniapp.domain.repository.FavoriteCityRepository
import com.example.skkniapp.domain.repository.ReverseGeocodingRepository
import com.example.skkniapp.domain.repository.WeatherRepository
import org.koin.dsl.module

val dataModule = module {

    single { LocationProvider(get()) }

    single { AppDatabase.getInstance(get()) }

    single { get<AppDatabase>().favoriteCityDao() }

    single<WeatherRepository> {
        WeatherRepositoryImpl(get())
    }

    single<FavoriteCityRepository> {
        FavoriteCityRepositoryImpl(get())
    }

    single<CitySearchRepository> {
        CitySearchRepositoryImpl(get())
    }

    single<ReverseGeocodingRepository> {
        ReverseGeocodingRepositoryImpl(get())
    }
}
