package com.example.skkniapp.data.mapper

import com.example.skkniapp.data.remote.dto.GeocodingResultResponse
import com.example.skkniapp.domain.model.CitySearchResultDomainModel

fun GeocodingResultResponse.toDomain(): CitySearchResultDomainModel {
    return CitySearchResultDomainModel(
        name = name,
        region = admin1,
        country = country,
        latitude = latitude,
        longitude = longitude
    )
}
