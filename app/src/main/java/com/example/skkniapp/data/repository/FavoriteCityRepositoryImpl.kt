package com.example.skkniapp.data.repository

import com.example.skkniapp.data.local.FavoriteCityDbHelper
import com.example.skkniapp.domain.model.CityLocation
import com.example.skkniapp.domain.repository.FavoriteCityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteCityRepositoryImpl(
    private val dbHelper: FavoriteCityDbHelper
) : FavoriteCityRepository {

    override suspend fun getFavoriteCities(): List<CityLocation> = withContext(Dispatchers.IO) {
        dbHelper.getAllCities()
    }

    override suspend fun addFavoriteCity(city: CityLocation) = withContext(Dispatchers.IO) {
        dbHelper.insertCity(city)
    }

    override suspend fun removeFavoriteCity(name: String) = withContext(Dispatchers.IO) {
        dbHelper.deleteCity(name)
    }
}
