package com.example.skkniapp.ui.weather

import com.example.skkniapp.domain.model.CityWeatherDomain
import com.example.skkniapp.domain.model.DailyForecastDomain
import com.example.skkniapp.domain.model.HourlyForecastDomain
import com.example.skkniapp.domain.model.WeatherDomain
import com.example.skkniapp.domain.util.WeatherCodeMapper
import com.example.skkniapp.domain.util.WindDirectionMapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun WeatherDomain.toUiModel(cityName: String, locationDetailLabel: String? = null): WeatherUiModel {
    return WeatherUiModel(
        cityName = cityName,
        locationDetailLabel = locationDetailLabel,
        temperatureLabel = "${temperature.toInt()}°C",
        feelsLikeLabel = "${feelsLike.toInt()}°C",
        humidityLabel = "${humidity}%",
        windSpeedLabel = "${windSpeed} m/s",
        windDirectionDegrees = windDirection.toFloat(),
        windDirectionLabel = WindDirectionMapper.toLabel(windDirection),
        description = description,
        emoji = WeatherCodeMapper.toEmoji(weatherCode),
        dailyForecast = dailyForecast.map { it.toUiModel() },
        hourlyForecast = hourlyForecast.map { it.toUiModel() }
    )
}

private fun todayDateString(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID")).format(Date())

fun DailyForecastDomain.toUiModel(): DailyForecastUiModel {
    val isToday = date == todayDateString()
    val dayLabel = if (isToday) {
        "Hari Ini"
    } else {
        runCatching {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
            val outputFormat = SimpleDateFormat("EEE", Locale("id", "ID"))
            outputFormat.format(inputFormat.parse(date)!!)
        }.getOrDefault(date)
    }

    return DailyForecastUiModel(
        date = date,
        dayLabel = dayLabel,
        isToday = isToday,
        maxTemperatureLabel = "${maxTemperature.toInt()}°",
        minTemperatureLabel = "${minTemperature.toInt()}°",
        precipitationLabel = "💧 ${precipitationProbability}%",
        emoji = WeatherCodeMapper.toEmoji(weatherCode)
    )
}

fun HourlyForecastDomain.toUiModel(): HourlyForecastUiModel {
    val date = time.substringBefore("T")
    val timeLabel = runCatching {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale("id", "ID"))
        val outputFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        outputFormat.format(inputFormat.parse(time)!!)
    }.getOrDefault(time.substringAfter("T"))

    return HourlyForecastUiModel(
        date = date,
        timeLabel = timeLabel,
        temperatureLabel = "${temperature.toInt()}°C",
        precipitationLabel = "💧 ${precipitationProbability}%",
        emoji = WeatherCodeMapper.toEmoji(weatherCode)
    )
}

fun CityWeatherDomain.toUiModel(): CityWeatherUiModel {
    return CityWeatherUiModel(
        cityName = cityName,
        latitude = latitude,
        longitude = longitude,
        temperatureLabel = "${weather.temperature.toInt()}°C",
        emoji = WeatherCodeMapper.toEmoji(weather.weatherCode)
    )
}
