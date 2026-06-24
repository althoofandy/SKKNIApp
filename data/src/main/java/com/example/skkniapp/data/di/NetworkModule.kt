package com.example.skkniapp.data.di

import com.example.skkniapp.core.AppConstants
import com.example.skkniapp.data.remote.GeocodingApiService
import com.example.skkniapp.data.remote.ReverseGeocodingApiService
import com.example.skkniapp.data.remote.WeatherApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val FORECAST_RETROFIT = named("forecastRetrofit")
val GEOCODING_RETROFIT = named("geocodingRetrofit")
val REVERSE_GEOCODING_RETROFIT = named("reverseGeocodingRetrofit")

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
            .baseUrl(AppConstants.FORECAST_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single(GEOCODING_RETROFIT) {
        Retrofit.Builder()
            .baseUrl(AppConstants.GEOCODING_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single(REVERSE_GEOCODING_RETROFIT) {
        Retrofit.Builder()
            .baseUrl(AppConstants.REVERSE_GEOCODING_BASE_URL)
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

    single {
        get<Retrofit>(REVERSE_GEOCODING_RETROFIT).create(ReverseGeocodingApiService::class.java)
    }
}
