package com.example.skkniapp.domain.repository

import com.example.skkniapp.domain.model.CityLocation
import com.example.skkniapp.domain.model.CitySearchResult
import com.example.skkniapp.domain.model.GeoLocation
import com.example.skkniapp.domain.model.WeatherDomain

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherDomain
    suspend fun searchCity(query: String): List<CitySearchResult>
    suspend fun getPlaceName(latitude: Double, longitude: Double): String?
    suspend fun getCurrentLocation(): GeoLocation?
    suspend fun getFavoriteCities(): List<CityLocation>
    suspend fun addFavoriteCity(city: CityLocation)
    suspend fun removeFavoriteCity(name: String)
}
