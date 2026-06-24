package com.example.skkniapp.domain.util

import com.example.skkniapp.core.AppConstants

object WeatherCodeMapper {

    fun toDescription(code: Int): String = when (code) {
        AppConstants.WMO_CLEAR -> AppConstants.WEATHER_DESC_CLEAR
        AppConstants.WMO_PARTLY_CLOUDY_1, AppConstants.WMO_PARTLY_CLOUDY_2, AppConstants.WMO_PARTLY_CLOUDY_3 ->
            AppConstants.WEATHER_DESC_CLOUDY
        AppConstants.WMO_FOG_1, AppConstants.WMO_FOG_2 -> AppConstants.WEATHER_DESC_FOG
        AppConstants.WMO_DRIZZLE_1, AppConstants.WMO_DRIZZLE_2, AppConstants.WMO_DRIZZLE_3 ->
            AppConstants.WEATHER_DESC_DRIZZLE
        AppConstants.WMO_RAIN_1, AppConstants.WMO_RAIN_2, AppConstants.WMO_RAIN_3 -> AppConstants.WEATHER_DESC_RAIN
        AppConstants.WMO_SNOW_1, AppConstants.WMO_SNOW_2, AppConstants.WMO_SNOW_3 -> AppConstants.WEATHER_DESC_SNOW
        AppConstants.WMO_HEAVY_RAIN_1, AppConstants.WMO_HEAVY_RAIN_2, AppConstants.WMO_HEAVY_RAIN_3 ->
            AppConstants.WEATHER_DESC_HEAVY_RAIN
        AppConstants.WMO_THUNDERSTORM_1, AppConstants.WMO_THUNDERSTORM_2, AppConstants.WMO_THUNDERSTORM_3 ->
            AppConstants.WEATHER_DESC_THUNDERSTORM
        else -> AppConstants.WEATHER_DESC_UNKNOWN
    }

    fun toEmoji(code: Int): String = when (code) {
        AppConstants.WMO_CLEAR -> AppConstants.WEATHER_EMOJI_CLEAR
        AppConstants.WMO_PARTLY_CLOUDY_1, AppConstants.WMO_PARTLY_CLOUDY_2, AppConstants.WMO_PARTLY_CLOUDY_3 ->
            AppConstants.WEATHER_EMOJI_CLOUDY
        AppConstants.WMO_FOG_1, AppConstants.WMO_FOG_2 -> AppConstants.WEATHER_EMOJI_FOG
        AppConstants.WMO_DRIZZLE_1, AppConstants.WMO_DRIZZLE_2, AppConstants.WMO_DRIZZLE_3 ->
            AppConstants.WEATHER_EMOJI_DRIZZLE
        AppConstants.WMO_RAIN_1, AppConstants.WMO_RAIN_2, AppConstants.WMO_RAIN_3 -> AppConstants.WEATHER_EMOJI_RAIN
        AppConstants.WMO_SNOW_1, AppConstants.WMO_SNOW_2, AppConstants.WMO_SNOW_3 -> AppConstants.WEATHER_EMOJI_SNOW
        AppConstants.WMO_HEAVY_RAIN_1, AppConstants.WMO_HEAVY_RAIN_2, AppConstants.WMO_HEAVY_RAIN_3 ->
            AppConstants.WEATHER_EMOJI_HEAVY_RAIN
        AppConstants.WMO_THUNDERSTORM_1, AppConstants.WMO_THUNDERSTORM_2, AppConstants.WMO_THUNDERSTORM_3 ->
            AppConstants.WEATHER_EMOJI_THUNDERSTORM
        else -> AppConstants.WEATHER_EMOJI_UNKNOWN
    }
}
