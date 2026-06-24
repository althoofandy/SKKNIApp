package com.example.skkniapp.data.repository

import com.example.skkniapp.data.mapper.toDomain
import com.example.skkniapp.data.remote.GeocodingApiService
import com.example.skkniapp.domain.model.CitySearchResult
import com.example.skkniapp.domain.repository.CitySearchRepository

class CitySearchRepositoryImpl(
    private val geocodingApiService: GeocodingApiService
) : CitySearchRepository {

    override suspend fun searchCity(query: String): List<CitySearchResult> {
        if (query.isBlank()) return emptyList()
        return geocodingApiService.searchCity(query).results.orEmpty().map { it.toDomain() }
    }
}
