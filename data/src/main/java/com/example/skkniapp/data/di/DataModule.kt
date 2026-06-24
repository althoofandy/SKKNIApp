package com.example.skkniapp.data.di

import com.example.skkniapp.core.AppConstants
import com.example.skkniapp.data.remote.WeatherApiService
import com.example.skkniapp.domain.repository.WeatherRepository
import com.example.skkniapp.domain.usecase.WeatherDashboardUseCase
import com.example.skkniapp.data.local.AppDatabase
import com.example.skkniapp.data.repository.WeatherRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(AppConstants.FORECAST_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(WeatherApiService::class.java)
    }

    single { AppDatabase.getInstance(get()) }

    single { get<AppDatabase>().favoriteCityDao() }

    single<WeatherRepository> {
        WeatherRepositoryImpl(get(), get(), get())
    }

    factory { WeatherDashboardUseCase(get()) }
}
