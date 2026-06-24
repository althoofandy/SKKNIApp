package com.example.skkniapp.domain.model

data class WeatherDomain(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Double,
    val weatherCode: Int,
    val description: String,
    val dailyForecast: List<DailyForecastDomain>,
    val hourlyForecast: List<HourlyForecastDomain>
)

data class DailyForecastDomain(
    val date: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val weatherCode: Int,
    val precipitationProbability: Int
)

data class HourlyForecastDomain(
    val time: String,
    val temperature: Double,
    val weatherCode: Int,
    val precipitationProbability: Int
)
