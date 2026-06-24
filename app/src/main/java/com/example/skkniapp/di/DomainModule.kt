package com.example.skkniapp.di

import com.example.skkniapp.domain.usecase.WeatherDashboardUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { WeatherDashboardUseCase(get(), get(), get(), get(), get()) }
}
