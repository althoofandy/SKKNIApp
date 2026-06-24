package com.example.skkniapp.data.mapper

import com.example.skkniapp.data.remote.dto.GeocodingResultResponse
import com.example.skkniapp.domain.model.CitySearchResult

fun GeocodingResultResponse.toDomain(): CitySearchResult {
    return CitySearchResult(
        name = name,
        region = admin1,
        country = country,
        latitude = latitude,
        longitude = longitude
    )
}
