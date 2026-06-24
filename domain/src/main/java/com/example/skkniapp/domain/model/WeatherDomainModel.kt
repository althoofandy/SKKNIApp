package com.example.skkniapp.domain.model

data class WeatherDomainModel(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Double,
    val weatherCode: Int,
    val description: String,
    val dailyForecast: List<DailyForecastDomainModel>,
    val hourlyForecast: List<HourlyForecastDomainModel>
)

data class DailyForecastDomainModel(
    val date: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val weatherCode: Int,
    val precipitationProbability: Int
)

data class HourlyForecastDomainModel(
    val time: String,
    val temperature: Double,
    val weatherCode: Int,
    val precipitationProbability: Int
)
