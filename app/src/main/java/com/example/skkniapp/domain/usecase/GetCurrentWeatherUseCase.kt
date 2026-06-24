package com.example.skkniapp.domain.usecase

import com.example.skkniapp.domain.model.WeatherDomain
import com.example.skkniapp.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase(private val repository: WeatherRepository) {

    suspend operator fun invoke(latitude: Double, longitude: Double): WeatherDomain {
        return repository.getCurrentWeather(latitude, longitude)
    }
}
