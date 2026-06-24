package com.example.skkniapp.domain.usecase

import com.example.skkniapp.domain.model.CityLocation
import com.example.skkniapp.domain.repository.FavoriteCityRepository

class GetFavoriteCitiesUseCase(private val repository: FavoriteCityRepository) {
    suspend operator fun invoke(): List<CityLocation> = repository.getFavoriteCities()
}

class AddFavoriteCityUseCase(private val repository: FavoriteCityRepository) {
    suspend operator fun invoke(city: CityLocation) = repository.addFavoriteCity(city)
}

class RemoveFavoriteCityUseCase(private val repository: FavoriteCityRepository) {
    suspend operator fun invoke(name: String) = repository.removeFavoriteCity(name)
}
