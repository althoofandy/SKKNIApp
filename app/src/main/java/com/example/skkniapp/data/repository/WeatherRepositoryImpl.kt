package com.example.skkniapp.data.repository

import com.example.skkniapp.data.mapper.toDomain
import com.example.skkniapp.data.remote.WeatherApiService
import com.example.skkniapp.domain.model.WeatherDomain
import com.example.skkniapp.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService
) : WeatherRepository {

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherDomain {
        return apiService.getCurrentWeather(latitude, longitude).toDomain()
    }
}
