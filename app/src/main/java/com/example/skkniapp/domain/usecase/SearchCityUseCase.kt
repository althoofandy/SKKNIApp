package com.example.skkniapp.domain.usecase

import com.example.skkniapp.domain.model.CitySearchResult
import com.example.skkniapp.domain.repository.CitySearchRepository

class SearchCityUseCase(private val repository: CitySearchRepository) {
    suspend operator fun invoke(query: String): List<CitySearchResult> = repository.searchCity(query)
}
