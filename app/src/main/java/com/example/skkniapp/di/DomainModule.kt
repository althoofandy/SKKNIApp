package com.example.skkniapp.di

import com.example.skkniapp.domain.usecase.AddFavoriteCityUseCase
import com.example.skkniapp.domain.usecase.GetCurrentWeatherUseCase
import com.example.skkniapp.domain.usecase.GetFavoriteCitiesUseCase
import com.example.skkniapp.domain.usecase.GetOtherCitiesWeatherUseCase
import com.example.skkniapp.domain.usecase.RemoveFavoriteCityUseCase
import com.example.skkniapp.domain.usecase.SearchCityUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetCurrentWeatherUseCase(get()) }
    factory { GetOtherCitiesWeatherUseCase(get(), get()) }
    factory { SearchCityUseCase(get()) }
    factory { GetFavoriteCitiesUseCase(get()) }
    factory { AddFavoriteCityUseCase(get()) }
    factory { RemoveFavoriteCityUseCase(get()) }
}
