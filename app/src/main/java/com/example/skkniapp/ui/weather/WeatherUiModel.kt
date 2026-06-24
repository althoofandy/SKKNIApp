package com.example.skkniapp.ui.weather

data class WeatherUiModel(
    val cityName: String,
    val locationDetailLabel: String?,
    val temperatureLabel: String,
    val feelsLikeLabel: String,
    val humidityLabel: String,
    val windSpeedLabel: String,
    val windDirectionDegrees: Float,
    val windDirectionLabel: String,
    val description: String,
    val emoji: String,
    val dailyForecast: List<DailyForecastUiModel>,
    val hourlyForecast: List<HourlyForecastUiModel>
)

data class DailyForecastUiModel(
    val date: String,
    val dayLabel: String,
    val isToday: Boolean,
    val maxTemperatureLabel: String,
    val minTemperatureLabel: String,
    val precipitationLabel: String,
    val emoji: String
)

data class HourlyForecastUiModel(
    val date: String,
    val timeLabel: String,
    val temperatureLabel: String,
    val precipitationLabel: String,
    val emoji: String
)
