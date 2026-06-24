package com.example.skkniapp.data.repository

import com.example.skkniapp.data.remote.ReverseGeocodingApiService
import com.example.skkniapp.domain.repository.ReverseGeocodingRepository

class ReverseGeocodingRepositoryImpl(
    private val apiService: ReverseGeocodingApiService
) : ReverseGeocodingRepository {

    override suspend fun getPlaceName(latitude: Double, longitude: Double): String? {
        val response = apiService.reverseGeocode(latitude, longitude)
        return response.city?.takeIf { it.isNotBlank() }
            ?: response.locality?.takeIf { it.isNotBlank() }
            ?: response.principalSubdivision?.takeIf { it.isNotBlank() }
    }
}
