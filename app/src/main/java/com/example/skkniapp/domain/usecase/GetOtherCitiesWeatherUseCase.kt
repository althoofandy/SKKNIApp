package com.example.skkniapp.domain.usecase

import com.example.skkniapp.domain.model.CityWeatherDomain
import com.example.skkniapp.domain.repository.FavoriteCityRepository
import com.example.skkniapp.domain.repository.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetOtherCitiesWeatherUseCase(
    private val weatherRepository: WeatherRepository,
    private val favoriteCityRepository: FavoriteCityRepository
) {

    suspend operator fun invoke(): List<CityWeatherDomain> = coroutineScope {
        val cities = favoriteCityRepository.getFavoriteCities()
        cities.map { city ->
            async {
                CityWeatherDomain(
                    cityName = city.name,
                    latitude = city.latitude,
                    longitude = city.longitude,
                    weather = weatherRepository.getCurrentWeather(city.latitude, city.longitude)
                )
            }
        }.map { it.await() }
    }
}
