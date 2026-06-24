package com.example.skkniapp.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skkniapp.core.AppConstants
import com.example.skkniapp.core.Resource
import com.example.skkniapp.domain.model.CityLocation
import com.example.skkniapp.domain.usecase.WeatherDashboardUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherDashboardUseCase: WeatherDashboardUseCase
) : ViewModel() {

    private val _weatherState = MutableStateFlow<Resource<WeatherUiModel>>(Resource.Loading)
    val weatherState: StateFlow<Resource<WeatherUiModel>> = _weatherState.asStateFlow()

    private val _otherCitiesState = MutableStateFlow<Resource<List<CityWeatherUiModel>>>(Resource.Loading)
    val otherCitiesState: StateFlow<Resource<List<CityWeatherUiModel>>> = _otherCitiesState.asStateFlow()

    private val _searchResultsState = MutableStateFlow<Resource<List<CitySearchResultUiModel>>>(Resource.Success(emptyList()))
    val searchResultsState: StateFlow<Resource<List<CitySearchResultUiModel>>> = _searchResultsState.asStateFlow()

    private val _selectedCityName = MutableStateFlow<String?>(null)
    val selectedCityName: StateFlow<String?> = _selectedCityName.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadOtherCitiesWeather()
    }

    fun loadWeatherForCurrentLocation() {
        _selectedCityName.value = null
        viewModelScope.launch {
            _weatherState.value = Resource.Loading
            runCatching {
                val result = weatherDashboardUseCase.loadCurrentLocationWeather()
                result.weather.toUiModel(AppConstants.CURRENT_LOCATION_LABEL, result.placeName)
            }.onSuccess { uiModel ->
                _weatherState.value = Resource.Success(uiModel)
            }.onFailure { throwable ->
                _weatherState.value = Resource.Error(throwable.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun selectCity(cityName: String, latitude: Double, longitude: Double) {
        _selectedCityName.value = cityName
        viewModelScope.launch {
            _weatherState.value = Resource.Loading
            runCatching {
                weatherDashboardUseCase.loadWeatherForCity(latitude, longitude).toUiModel(cityName)
            }.onSuccess { uiModel ->
                _weatherState.value = Resource.Success(uiModel)
            }.onFailure { throwable ->
                _weatherState.value = Resource.Error(throwable.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun loadOtherCitiesWeather() {
        viewModelScope.launch {
            _otherCitiesState.value = Resource.Loading
            runCatching {
                weatherDashboardUseCase.loadFavoriteCitiesWeather().map { it.toUiModel() }
            }.onSuccess { uiModels ->
                _otherCitiesState.value = Resource.Success(uiModels)
            }.onFailure { throwable ->
                _otherCitiesState.value = Resource.Error(throwable.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun searchCity(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResultsState.value = Resource.Success(emptyList())
            return
        }
        searchJob = viewModelScope.launch {
            delay(AppConstants.SEARCH_DEBOUNCE_MS)
            _searchResultsState.value = Resource.Loading
            runCatching {
                weatherDashboardUseCase.searchCity(query).map { it.toUiModel() }
            }.onSuccess { results ->
                _searchResultsState.value = Resource.Success(results)
            }.onFailure { throwable ->
                _searchResultsState.value = Resource.Error(throwable.message ?: "Gagal mencari kota")
            }
        }
    }

    fun isCityFavorited(cityName: String): Boolean {
        return (_otherCitiesState.value as? Resource.Success)?.data.orEmpty().any { it.cityName == cityName }
    }

    fun addCityToFavorites(cityName: String, latitude: Double, longitude: Double, temperatureLabel: String, emoji: String) {
        val newItem = CityWeatherUiModel(
            cityName = cityName,
            latitude = latitude,
            longitude = longitude,
            temperatureLabel = temperatureLabel,
            emoji = emoji
        )
        val currentList = (_otherCitiesState.value as? Resource.Success)?.data.orEmpty()
        _otherCitiesState.update { Resource.Success(currentList + newItem) }

        viewModelScope.launch {
            weatherDashboardUseCase.addFavoriteCity(CityLocation(cityName, latitude, longitude))
        }
    }

    fun removeCityFromFavorites(cityName: String) {
        val currentList = (_otherCitiesState.value as? Resource.Success)?.data.orEmpty()
        _otherCitiesState.update { Resource.Success(currentList.filterNot { it.cityName == cityName }) }

        viewModelScope.launch {
            weatherDashboardUseCase.removeFavoriteCity(cityName)
        }
    }
}
