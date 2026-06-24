package com.example.skkniapp.domain.di

import com.example.skkniapp.domain.usecase.WeatherDashboardUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { WeatherDashboardUseCase(get()) }
}
