package com.example.skkniapp.domain.model

data class CitySearchResultDomainModel(
    val name: String,
    val region: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double
)
