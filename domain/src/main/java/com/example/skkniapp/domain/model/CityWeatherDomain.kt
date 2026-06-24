package com.example.skkniapp.domain.model

data class CityWeatherDomain(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val weather: WeatherDomain
)
