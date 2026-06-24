package com.example.skkniapp.domain.repository

import com.example.skkniapp.domain.model.WeatherDomain

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherDomain
}
