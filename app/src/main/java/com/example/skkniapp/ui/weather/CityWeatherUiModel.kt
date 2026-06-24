package com.example.skkniapp.ui.weather

data class CityWeatherUiModel(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val temperatureLabel: String,
    val emoji: String
)
