package com.example.skkniapp.data.remote

import com.example.skkniapp.core.AppConstants
import com.example.skkniapp.data.remote.dto.GeocodingResponse
import com.example.skkniapp.data.remote.dto.ReverseGeocodeResponse
import com.example.skkniapp.data.remote.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeatherApiService {

    @GET
    suspend fun getCurrentWeather(
        @Url url: String = AppConstants.FORECAST_BASE_URL + AppConstants.FORECAST_PATH,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = AppConstants.WEATHER_CURRENT_PARAMS,
        @Query("daily") daily: String = AppConstants.WEATHER_DAILY_PARAMS,
        @Query("hourly") hourly: String = AppConstants.WEATHER_HOURLY_PARAMS,
        @Query("forecast_days") forecastDays: Int = AppConstants.FORECAST_DAYS,
        @Query("timezone") timezone: String = AppConstants.DEFAULT_TIMEZONE
    ): WeatherResponse

    @GET
    suspend fun searchCity(
        @Url url: String = AppConstants.GEOCODING_BASE_URL + AppConstants.GEOCODING_PATH,
        @Query("name") name: String,
        @Query("count") count: Int = AppConstants.GEOCODING_RESULT_LIMIT,
        @Query("language") language: String = AppConstants.DEFAULT_LANGUAGE
    ): GeocodingResponse

    @GET
    suspend fun reverseGeocode(
        @Url url: String = AppConstants.REVERSE_GEOCODING_BASE_URL + AppConstants.REVERSE_GEOCODING_PATH,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localityLanguage") localityLanguage: String = AppConstants.DEFAULT_LANGUAGE
    ): ReverseGeocodeResponse
}
