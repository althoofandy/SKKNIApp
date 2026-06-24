package com.example.skkniapp.domain.model

data class CityWeatherDomainModel(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val weather: WeatherDomainModel
)
