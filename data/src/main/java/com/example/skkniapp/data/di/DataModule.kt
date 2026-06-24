package com.example.skkniapp.data.di

import com.example.skkniapp.data.local.AppDatabase
import com.example.skkniapp.data.repository.WeatherRepositoryImpl
import com.example.skkniapp.domain.repository.WeatherRepository
import org.koin.dsl.module

val dataModule = module {

    single { AppDatabase.getInstance(get()) }

    single { get<AppDatabase>().favoriteCityDao() }

    single<WeatherRepository> {
        WeatherRepositoryImpl(get(), get(), get())
    }
}
