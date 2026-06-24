package com.example.skkniapp.data.repository

import com.example.skkniapp.data.local.FavoriteCityDao
import com.example.skkniapp.data.local.FavoriteCityEntity
import com.example.skkniapp.domain.model.CityLocation
import com.example.skkniapp.domain.repository.FavoriteCityRepository
import com.example.skkniapp.domain.util.DefaultCities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteCityRepositoryImpl(
    private val dao: FavoriteCityDao
) : FavoriteCityRepository {

    override suspend fun getFavoriteCities(): List<CityLocation> = withContext(Dispatchers.IO) {
        seedDefaultCitiesIfEmpty()
        dao.getAll().map { CityLocation(it.name, it.latitude, it.longitude) }
    }

    override suspend fun addFavoriteCity(city: CityLocation) = withContext(Dispatchers.IO) {
        dao.insert(FavoriteCityEntity(name = city.name, latitude = city.latitude, longitude = city.longitude))
    }

    override suspend fun removeFavoriteCity(name: String) = withContext(Dispatchers.IO) {
        dao.deleteByName(name)
    }

    private suspend fun seedDefaultCitiesIfEmpty() {
        if (dao.count() == 0) {
            DefaultCities.all.forEach { city ->
                dao.insert(FavoriteCityEntity(name = city.name, latitude = city.latitude, longitude = city.longitude))
            }
        }
    }
}
