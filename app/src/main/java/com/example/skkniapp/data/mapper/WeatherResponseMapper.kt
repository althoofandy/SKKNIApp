package com.example.skkniapp.data.mapper

import com.example.skkniapp.data.remote.dto.WeatherResponse
import com.example.skkniapp.domain.model.DailyForecastDomain
import com.example.skkniapp.domain.model.HourlyForecastDomain
import com.example.skkniapp.domain.model.WeatherDomain
import com.example.skkniapp.domain.util.WeatherCodeMapper

fun WeatherResponse.toDomain(): WeatherDomain {
    val forecast = daily?.time?.indices?.map { index ->
        DailyForecastDomain(
            date = daily.time[index],
            maxTemperature = daily.maxTemperature[index],
            minTemperature = daily.minTemperature[index],
            weatherCode = daily.weatherCode[index],
            precipitationProbability = daily.precipitationProbabilityMax?.getOrNull(index) ?: 0
        )
    }.orEmpty()

    val hourlyForecast = hourly?.time?.indices?.map { index ->
        HourlyForecastDomain(
            time = hourly.time[index],
            temperature = hourly.temperature[index],
            weatherCode = hourly.weatherCode[index],
            precipitationProbability = hourly.precipitationProbability?.getOrNull(index) ?: 0
        )
    }.orEmpty()

    return WeatherDomain(
        temperature = current.temperature,
        feelsLike = current.feelsLike,
        humidity = current.humidity,
        windSpeed = current.windSpeed,
        weatherCode = current.weatherCode,
        description = WeatherCodeMapper.toDescription(current.weatherCode),
        dailyForecast = forecast,
        hourlyForecast = hourlyForecast
    )
}
