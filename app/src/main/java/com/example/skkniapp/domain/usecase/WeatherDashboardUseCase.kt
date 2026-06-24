package com.example.skkniapp.domain.usecase

import com.example.skkniapp.data.location.LocationProvider
import com.example.skkniapp.domain.model.CityLocation
import com.example.skkniapp.domain.model.CitySearchResult
import com.example.skkniapp.domain.model.CityWeatherDomain
import com.example.skkniapp.domain.model.WeatherDomain
import com.example.skkniapp.domain.repository.CitySearchRepository
import com.example.skkniapp.domain.repository.FavoriteCityRepository
import com.example.skkniapp.domain.repository.ReverseGeocodingRepository
import com.example.skkniapp.domain.repository.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

data class CurrentLocationWeather(
    val weather: WeatherDomain,
    val placeName: String?
)

class WeatherDashboardUseCase(
    private val weatherRepository: WeatherRepository,
    private val favoriteCityRepository: FavoriteCityRepository,
    private val citySearchRepository: CitySearchRepository,
    private val reverseGeocodingRepository: ReverseGeocodingRepository,
    private val locationProvider: LocationProvider
) {

    suspend fun loadCurrentLocationWeather(): CurrentLocationWeather = coroutineScope {
        val location = locationProvider.getCurrentLocation()
            ?: error("Lokasi tidak ditemukan, pastikan GPS aktif")

        val weatherDeferred = async { weatherRepository.getCurrentWeather(location.latitude, location.longitude) }
        val placeNameDeferred = async {
            runCatching { reverseGeocodingRepository.getPlaceName(location.latitude, location.longitude) }.getOrNull()
        }
        CurrentLocationWeather(weatherDeferred.await(), placeNameDeferred.await())
    }

    suspend fun loadWeatherForCity(latitude: Double, longitude: Double): WeatherDomain =
        weatherRepository.getCurrentWeather(latitude, longitude)

    suspend fun loadFavoriteCitiesWeather(): List<CityWeatherDomain> = coroutineScope {
        favoriteCityRepository.getFavoriteCities().map { city ->
            async {
                CityWeatherDomain(
                    cityName = city.name,
                    latitude = city.latitude,
                    longitude = city.longitude,
                    weather = weatherRepository.getCurrentWeather(city.latitude, city.longitude)
                )
            }
        }.awaitAll()
    }

    suspend fun searchCity(query: String): List<CitySearchResult> = citySearchRepository.searchCity(query)

    suspend fun addFavoriteCity(city: CityLocation) = favoriteCityRepository.addFavoriteCity(city)

    suspend fun removeFavoriteCity(name: String) = favoriteCityRepository.removeFavoriteCity(name)
}
