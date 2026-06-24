package com.example.skkniapp.core

object AppConstants {
    const val FORECAST_BASE_URL = "https://api.open-meteo.com/"
    const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"
    const val REVERSE_GEOCODING_BASE_URL = "https://api.bigdatacloud.net/"

    const val FORECAST_DAYS = 7
    const val GEOCODING_RESULT_LIMIT = 10

    const val SEARCH_DEBOUNCE_MS = 400L
    const val COLLAPSED_FORECAST_DAYS = 3
    const val FORECAST_TOGGLE_ANIMATION_MS = 200L

    const val SHAKE_THRESHOLD_G = 2.7f
    const val SHAKE_COOLDOWN_MS = 500L
    const val SHAKE_EVENTS_REQUIRED = 3
    const val SHAKE_EVENT_WINDOW_MS = 800L
    const val COMPASS_LOW_PASS_ALPHA = 0.15f
    const val DEGREES_FULL_CIRCLE = 360f
    const val DEGREES_HALF_CIRCLE = 180f
    const val CHEVRON_ROTATED_DEGREES = 180f
    const val CHEVRON_DEFAULT_DEGREES = 0f

    const val WIND_DIRECTIONS_COUNT = 16
    const val DEGREES_PER_WIND_DIRECTION = 22.5
    const val DEGREE_ROUNDING_OFFSET = 0.5

    const val DATABASE_NAME = "skkni_room.db"
    const val FAVORITE_CITY_TABLE_NAME = "favorite_city"

    const val CURRENT_LOCATION_LABEL = "Lokasi Saya"

    const val DEFAULT_CITY_1_NAME = "Jakarta"
    const val DEFAULT_CITY_1_LATITUDE = -6.2088
    const val DEFAULT_CITY_1_LONGITUDE = 106.8456

    const val DEFAULT_CITY_2_NAME = "Yogyakarta"
    const val DEFAULT_CITY_2_LATITUDE = -7.7956
    const val DEFAULT_CITY_2_LONGITUDE = 110.3695

    const val DEFAULT_CITY_3_NAME = "Denpasar"
    const val DEFAULT_CITY_3_LATITUDE = -8.6705
    const val DEFAULT_CITY_3_LONGITUDE = 115.2126

    const val FORECAST_PATH = "v1/forecast"
    const val GEOCODING_PATH = "v1/search"
    const val REVERSE_GEOCODING_PATH = "data/reverse-geocode-client"

    const val WEATHER_CURRENT_PARAMS =
        "temperature_2m,relative_humidity_2m,apparent_temperature,wind_speed_10m,wind_direction_10m,weather_code"
    const val WEATHER_DAILY_PARAMS =
        "temperature_2m_max,temperature_2m_min,weather_code,precipitation_probability_max"
    const val WEATHER_HOURLY_PARAMS = "temperature_2m,weather_code,precipitation_probability"
    const val DEFAULT_LANGUAGE = "id"
    const val DEFAULT_TIMEZONE = "auto"

    // Kode cuaca WMO (World Meteorological Organization) yang dipakai Open-Meteo
    const val WMO_CLEAR = 0
    const val WMO_PARTLY_CLOUDY_1 = 1
    const val WMO_PARTLY_CLOUDY_2 = 2
    const val WMO_PARTLY_CLOUDY_3 = 3
    const val WMO_FOG_1 = 45
    const val WMO_FOG_2 = 48
    const val WMO_DRIZZLE_1 = 51
    const val WMO_DRIZZLE_2 = 53
    const val WMO_DRIZZLE_3 = 55
    const val WMO_RAIN_1 = 61
    const val WMO_RAIN_2 = 63
    const val WMO_RAIN_3 = 65
    const val WMO_SNOW_1 = 71
    const val WMO_SNOW_2 = 73
    const val WMO_SNOW_3 = 75
    const val WMO_HEAVY_RAIN_1 = 80
    const val WMO_HEAVY_RAIN_2 = 81
    const val WMO_HEAVY_RAIN_3 = 82
    const val WMO_THUNDERSTORM_1 = 95
    const val WMO_THUNDERSTORM_2 = 96
    const val WMO_THUNDERSTORM_3 = 99

    const val WEATHER_DESC_CLEAR = "Cerah"
    const val WEATHER_DESC_CLOUDY = "Berawan"
    const val WEATHER_DESC_FOG = "Berkabut"
    const val WEATHER_DESC_DRIZZLE = "Gerimis"
    const val WEATHER_DESC_RAIN = "Hujan"
    const val WEATHER_DESC_SNOW = "Salju"
    const val WEATHER_DESC_HEAVY_RAIN = "Hujan Deras"
    const val WEATHER_DESC_THUNDERSTORM = "Badai Petir"
    const val WEATHER_DESC_UNKNOWN = "Tidak diketahui"

    const val WEATHER_EMOJI_CLEAR = "☀️"
    const val WEATHER_EMOJI_CLOUDY = "⛅"
    const val WEATHER_EMOJI_FOG = "🌫️"
    const val WEATHER_EMOJI_DRIZZLE = "🌦️"
    const val WEATHER_EMOJI_RAIN = "🌧️"
    const val WEATHER_EMOJI_SNOW = "❄️"
    const val WEATHER_EMOJI_HEAVY_RAIN = "🌧️"
    const val WEATHER_EMOJI_THUNDERSTORM = "⛈️"
    const val WEATHER_EMOJI_UNKNOWN = "🌡️"

    val WIND_DIRECTION_LABELS = listOf(
        "Utara", "Utara Timur Laut", "Timur Laut", "Timur Timur Laut",
        "Timur", "Timur Tenggara", "Tenggara", "Selatan Tenggara",
        "Selatan", "Selatan Barat Daya", "Barat Daya", "Barat Barat Daya",
        "Barat", "Barat Barat Laut", "Barat Laut", "Utara Barat Laut"
    )
}
