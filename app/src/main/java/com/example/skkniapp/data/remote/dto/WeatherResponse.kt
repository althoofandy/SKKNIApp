package com.example.skkniapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeatherResponse,
    @SerializedName("daily") val daily: DailyWeatherResponse?,
    @SerializedName("hourly") val hourly: HourlyWeatherResponse?
)

data class CurrentWeatherResponse(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("apparent_temperature") val feelsLike: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("weather_code") val weatherCode: Int
)

data class DailyWeatherResponse(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m_max") val maxTemperature: List<Double>,
    @SerializedName("temperature_2m_min") val minTemperature: List<Double>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>?
)

data class HourlyWeatherResponse(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int>?
)
