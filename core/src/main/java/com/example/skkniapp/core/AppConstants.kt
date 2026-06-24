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
}
