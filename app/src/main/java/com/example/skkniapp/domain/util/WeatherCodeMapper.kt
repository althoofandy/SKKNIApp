package com.example.skkniapp.domain.util

object WeatherCodeMapper {

    fun toDescription(code: Int): String = when (code) {
        0 -> "Cerah"
        1, 2, 3 -> "Berawan"
        45, 48 -> "Berkabut"
        51, 53, 55 -> "Gerimis"
        61, 63, 65 -> "Hujan"
        71, 73, 75 -> "Salju"
        80, 81, 82 -> "Hujan Deras"
        95, 96, 99 -> "Badai Petir"
        else -> "Tidak diketahui"
    }

    fun toEmoji(code: Int): String = when (code) {
        0 -> "☀️"
        1, 2, 3 -> "⛅"
        45, 48 -> "🌫️"
        51, 53, 55 -> "🌦️"
        61, 63, 65 -> "🌧️"
        71, 73, 75 -> "❄️"
        80, 81, 82 -> "🌧️"
        95, 96, 99 -> "⛈️"
        else -> "🌡️"
    }
}
