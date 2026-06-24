package com.example.skkniapp.domain.repository

import com.example.skkniapp.domain.model.CityLocation

interface FavoriteCityRepository {
    suspend fun getFavoriteCities(): List<CityLocation>
    suspend fun addFavoriteCity(city: CityLocation)
    suspend fun removeFavoriteCity(name: String)
}
