package com.example.skkniapp.data.mapper

import com.example.skkniapp.data.remote.dto.WeatherResponse
import com.example.skkniapp.domain.model.DailyForecastDomainModel
import com.example.skkniapp.domain.model.HourlyForecastDomainModel
import com.example.skkniapp.domain.model.WeatherDomainModel
import com.example.skkniapp.data.util.WeatherCodeMapper

fun WeatherResponse.toDomain(): WeatherDomainModel {
    val dailyResponse = daily
    val forecast = dailyResponse?.time?.indices?.map { index ->
        DailyForecastDomainModel(
            date = dailyResponse.time[index],
            maxTemperature = dailyResponse.maxTemperature[index],
            minTemperature = dailyResponse.minTemperature[index],
            weatherCode = dailyResponse.weatherCode[index],
            precipitationProbability = dailyResponse.precipitationProbabilityMax?.getOrNull(index) ?: 0
        )
    }.orEmpty()

    val hourlyResponse = hourly
    val hourlyForecast = hourlyResponse?.time?.indices?.map { index ->
        HourlyForecastDomainModel(
            time = hourlyResponse.time[index],
            temperature = hourlyResponse.temperature[index],
            weatherCode = hourlyResponse.weatherCode[index],
            precipitationProbability = hourlyResponse.precipitationProbability?.getOrNull(index) ?: 0
        )
    }.orEmpty()

    return WeatherDomainModel(
        temperature = current.temperature,
        feelsLike = current.feelsLike,
        humidity = current.humidity,
        windSpeed = current.windSpeed,
        windDirection = current.windDirection,
        weatherCode = current.weatherCode,
        description = WeatherCodeMapper.toDescription(current.weatherCode),
        dailyForecast = forecast,
        hourlyForecast = hourlyForecast
    )
}
