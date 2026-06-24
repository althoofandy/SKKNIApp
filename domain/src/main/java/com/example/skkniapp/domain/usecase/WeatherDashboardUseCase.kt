package com.example.skkniapp.domain.usecase

import com.example.skkniapp.domain.model.CityLocation
import com.example.skkniapp.domain.model.CitySearchResult
import com.example.skkniapp.domain.model.CityWeatherDomain
import com.example.skkniapp.domain.model.WeatherDomain
import com.example.skkniapp.domain.repository.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

data class CurrentLocationWeather(
    val weather: WeatherDomain,
    val placeName: String?
)

class WeatherDashboardUseCase(
    private val repository: WeatherRepository
) {

    suspend fun loadCurrentLocationWeather(): CurrentLocationWeather = coroutineScope {
        val location = repository.getCurrentLocation()
            ?: error("Lokasi tidak ditemukan, pastikan GPS aktif")

        val weatherDeferred = async { repository.getCurrentWeather(location.latitude, location.longitude) }
        val placeNameDeferred = async {
            runCatching { repository.getPlaceName(location.latitude, location.longitude) }.getOrNull()
        }
        CurrentLocationWeather(weatherDeferred.await(), placeNameDeferred.await())
    }

    suspend fun loadWeatherForCity(latitude: Double, longitude: Double): WeatherDomain =
        repository.getCurrentWeather(latitude, longitude)

    suspend fun loadFavoriteCitiesWeather(): List<CityWeatherDomain> = coroutineScope {
        repository.getFavoriteCities().map { city ->
            async {
                CityWeatherDomain(
                    cityName = city.name,
                    latitude = city.latitude,
                    longitude = city.longitude,
                    weather = repository.getCurrentWeather(city.latitude, city.longitude)
                )
            }
        }.awaitAll()
    }

    suspend fun searchCity(query: String): List<CitySearchResult> = repository.searchCity(query)

    suspend fun addFavoriteCity(city: CityLocation) = repository.addFavoriteCity(city)

    suspend fun removeFavoriteCity(name: String) = repository.removeFavoriteCity(name)
}
