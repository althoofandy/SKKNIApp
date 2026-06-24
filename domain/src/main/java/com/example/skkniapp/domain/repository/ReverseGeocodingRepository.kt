package com.example.skkniapp.domain.repository

interface ReverseGeocodingRepository {
    suspend fun getPlaceName(latitude: Double, longitude: Double): String?
}
