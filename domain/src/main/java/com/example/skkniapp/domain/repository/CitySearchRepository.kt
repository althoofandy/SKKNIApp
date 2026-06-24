package com.example.skkniapp.domain.repository

import com.example.skkniapp.domain.model.CitySearchResult

interface CitySearchRepository {
    suspend fun searchCity(query: String): List<CitySearchResult>
}
