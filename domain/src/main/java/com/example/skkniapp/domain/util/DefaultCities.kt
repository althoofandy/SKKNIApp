package com.example.skkniapp.domain.util

import com.example.skkniapp.core.AppConstants
import com.example.skkniapp.domain.model.CityLocation

object DefaultCities {
    val all = listOf(
        CityLocation(AppConstants.DEFAULT_CITY_1_NAME, AppConstants.DEFAULT_CITY_1_LATITUDE, AppConstants.DEFAULT_CITY_1_LONGITUDE),
        CityLocation(AppConstants.DEFAULT_CITY_2_NAME, AppConstants.DEFAULT_CITY_2_LATITUDE, AppConstants.DEFAULT_CITY_2_LONGITUDE),
        CityLocation(AppConstants.DEFAULT_CITY_3_NAME, AppConstants.DEFAULT_CITY_3_LATITUDE, AppConstants.DEFAULT_CITY_3_LONGITUDE)
    )
}
