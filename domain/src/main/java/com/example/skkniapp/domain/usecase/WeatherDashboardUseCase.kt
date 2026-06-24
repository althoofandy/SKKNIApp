package com.example.skkniapp.domain.usecase

import com.example.skkniapp.domain.model.CityLocationDomainModel
import com.example.skkniapp.domain.model.CitySearchResultDomainModel
import com.example.skkniapp.domain.model.CityWeatherDomainModel
import com.example.skkniapp.domain.model.WeatherDomainModel
import com.example.skkniapp.domain.repository.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

data class CurrentLocationWeather(
    val weather: WeatherDomainModel,
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

    suspend fun loadWeatherForCity(latitude: Double, longitude: Double): WeatherDomainModel =
        repository.getCurrentWeather(latitude, longitude)

    suspend fun loadFavoriteCitiesWeather(): List<CityWeatherDomainModel> = coroutineScope {
        repository.getFavoriteCities().map { city ->
            async {
                CityWeatherDomainModel(
                    cityName = city.name,
                    latitude = city.latitude,
                    longitude = city.longitude,
                    weather = repository.getCurrentWeather(city.latitude, city.longitude)
                )
            }
        }.awaitAll()
    }

    suspend fun searchCity(query: String): List<CitySearchResultDomainModel> = repository.searchCity(query)

    suspend fun addFavoriteCity(city: CityLocationDomainModel) = repository.addFavoriteCity(city)

    suspend fun removeFavoriteCity(name: String) = repository.removeFavoriteCity(name)
}
