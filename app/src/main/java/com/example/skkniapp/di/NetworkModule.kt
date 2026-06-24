package com.example.skkniapp.di

import com.example.skkniapp.data.remote.GeocodingApiService
import com.example.skkniapp.data.remote.WeatherApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val FORECAST_BASE_URL = "https://api.open-meteo.com/"
private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"

val FORECAST_RETROFIT = named("forecastRetrofit")
val GEOCODING_RETROFIT = named("geocodingRetrofit")

val networkModule = module {

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

    single(FORECAST_RETROFIT) {
        Retrofit.Builder()
            .baseUrl(FORECAST_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single(GEOCODING_RETROFIT) {
        Retrofit.Builder()
            .baseUrl(GEOCODING_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>(FORECAST_RETROFIT).create(WeatherApiService::class.java)
    }

    single {
        get<Retrofit>(GEOCODING_RETROFIT).create(GeocodingApiService::class.java)
    }
}
