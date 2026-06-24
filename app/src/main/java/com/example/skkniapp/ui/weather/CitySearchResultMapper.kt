package com.example.skkniapp.ui.weather

import com.example.skkniapp.domain.model.CitySearchResult

fun CitySearchResult.toUiModel(): CitySearchResultUiModel {
    return CitySearchResultUiModel(
        name = name,
        subtitle = listOfNotNull(region, country).joinToString(", "),
        latitude = latitude,
        longitude = longitude
    )
}
