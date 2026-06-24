package com.example.skkniapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.example.skkniapp.data.local.FavoriteCityDao
import com.example.skkniapp.data.local.FavoriteCityEntity
import com.example.skkniapp.data.mapper.toDomain
import com.example.skkniapp.data.remote.WeatherApiService
import com.example.skkniapp.domain.model.CityLocationDomainModel
import com.example.skkniapp.domain.model.CitySearchResultDomainModel
import com.example.skkniapp.domain.model.GeoLocationDomainModel
import com.example.skkniapp.domain.model.WeatherDomainModel
import com.example.skkniapp.domain.repository.WeatherRepository
import com.example.skkniapp.domain.util.DefaultCities
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService,
    private val favoriteCityDao: FavoriteCityDao,
    context: Context
) : WeatherRepository {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherDomainModel {
        return apiService.getCurrentWeather(latitude = latitude, longitude = longitude).toDomain()
    }

    override suspend fun searchCity(query: String): List<CitySearchResultDomainModel> {
        if (query.isBlank()) return emptyList()
        return apiService.searchCity(name = query).results.orEmpty().map { it.toDomain() }
    }

    override suspend fun getPlaceName(latitude: Double, longitude: Double): String? {
        val response = apiService.reverseGeocode(latitude = latitude, longitude = longitude)
        return response.city?.takeIf { it.isNotBlank() }
            ?: response.locality?.takeIf { it.isNotBlank() }
            ?: response.principalSubdivision?.takeIf { it.isNotBlank() }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): GeoLocationDomainModel? {
        val location = fusedClient
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .await()
        return location?.let { GeoLocationDomainModel(it.latitude, it.longitude) }
    }

    override suspend fun getFavoriteCities(): List<CityLocationDomainModel> = withContext(Dispatchers.IO) {
        seedDefaultCitiesIfEmpty()
        favoriteCityDao.getAll().map { CityLocationDomainModel(it.name, it.latitude, it.longitude) }
    }

    override suspend fun addFavoriteCity(city: CityLocationDomainModel) = withContext(Dispatchers.IO) {
        favoriteCityDao.insert(FavoriteCityEntity(name = city.name, latitude = city.latitude, longitude = city.longitude))
    }

    override suspend fun removeFavoriteCity(name: String) = withContext(Dispatchers.IO) {
        favoriteCityDao.deleteByName(name)
    }

    private suspend fun seedDefaultCitiesIfEmpty() {
        if (favoriteCityDao.count() == 0) {
            DefaultCities.all.forEach { city ->
                favoriteCityDao.insert(FavoriteCityEntity(name = city.name, latitude = city.latitude, longitude = city.longitude))
            }
        }
    }
}
