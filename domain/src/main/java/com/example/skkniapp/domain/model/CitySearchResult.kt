package com.example.skkniapp.domain.model

data class CitySearchResult(
    val name: String,
    val region: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double
)
