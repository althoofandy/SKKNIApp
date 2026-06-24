package com.example.skkniapp.domain.repository

import com.example.skkniapp.domain.model.CityLocationDomainModel
import com.example.skkniapp.domain.model.CitySearchResultDomainModel
import com.example.skkniapp.domain.model.GeoLocationDomainModel
import com.example.skkniapp.domain.model.WeatherDomainModel

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherDomainModel
    suspend fun searchCity(query: String): List<CitySearchResultDomainModel>
    suspend fun getPlaceName(latitude: Double, longitude: Double): String?
    suspend fun getCurrentLocation(): GeoLocationDomainModel?
    suspend fun getFavoriteCities(): List<CityLocationDomainModel>
    suspend fun addFavoriteCity(city: CityLocationDomainModel)
    suspend fun removeFavoriteCity(name: String)
}
