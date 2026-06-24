# Location-Based Service & Navigation

← [Back to README](../README.md)

The weather dashboard's "current location" mode combines **GPS positioning**, **reverse geocoding** (coordinates → place name), and **forward geocoding** (city name → coordinates), then routes to a second screen (wind compass) via the in-app navigation graph.

## Required permissions

`app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

These are **runtime** permissions on API 24+, requested through `ActivityResultContracts.RequestPermission` rather than assumed from the manifest alone.

## Permission flow

`app/src/main/java/com/example/skkniapp/ui/weather/WeatherFragment.kt`:

```kotlin
private val permissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { granted ->
    if (granted) {
        viewModel.loadWeatherForCurrentLocation()
    } else {
        Snackbar.make(binding.root, getString(R.string.permission_required), Snackbar.LENGTH_LONG).show()
    }
}

private fun requestLocationAndLoadWeather() {
    val hasPermission = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (hasPermission) {
        viewModel.loadWeatherForCurrentLocation()
    } else {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
```

If the user denies the permission, the app shows a Snackbar and simply skips location-based weather — it never crashes or repeatedly re-prompts.

## Getting the device's current location

`data/src/main/java/com/example/skkniapp/data/repository/WeatherRepositoryImpl.kt` wraps Google Play Services' **Fused Location Provider**:

```kotlin
private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

@SuppressLint("MissingPermission")
override suspend fun getCurrentLocation(): GeoLocationDomainModel? {
    val location = fusedClient
        .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
        .await()
    return location?.let { GeoLocationDomainModel(it.latitude, it.longitude) }
}
```

- `Priority.PRIORITY_BALANCED_POWER_ACCURACY` trades a bit of accuracy for much lower battery drain than high-accuracy GPS — appropriate for a weather app that doesn't need meter-level precision.
- `@SuppressLint("MissingPermission")` is safe here because the permission check already happened in the UI layer before this is ever called (see [Permission flow](#permission-flow)).
- `getCurrentLocation()` (not `getLastLocation()`) is used deliberately, since the latter can return stale/cached locations.

## Reverse & forward geocoding

Both go through the same `WeatherApiService` described in [`docs/NETWORK.md`](NETWORK.md), backed by Open-Meteo (forward) and BigDataCloud (reverse):

```kotlin
override suspend fun getPlaceName(latitude: Double, longitude: Double): String? {
    val response = apiService.reverseGeocode(latitude = latitude, longitude = longitude)
    return response.city?.takeIf { it.isNotBlank() }
        ?: response.locality?.takeIf { it.isNotBlank() }
        ?: response.principalSubdivision?.takeIf { it.isNotBlank() }
}

override suspend fun searchCity(query: String): List<CitySearchResultDomainModel> {
    if (query.isBlank()) return emptyList()
    return apiService.searchCity(name = query).results.orEmpty().map { it.toDomain() }
}
```

The reverse-geocoding fallback chain (`city` → `locality` → `principalSubdivision`) handles sparsely-populated areas where a precise city name isn't available from the API.

## Combining location + weather + place name

`domain/src/main/java/com/example/skkniapp/domain/usecase/WeatherDashboardUseCase.kt` fetches the weather and the place name **concurrently** once the location is known:

```kotlin
suspend fun loadCurrentLocationWeather(): CurrentLocationWeather = coroutineScope {
    val location = repository.getCurrentLocation()
        ?: error("Lokasi tidak ditemukan, pastikan GPS aktif")

    val weatherDeferred = async { repository.getCurrentWeather(location.latitude, location.longitude) }
    val placeNameDeferred = async {
        runCatching { repository.getPlaceName(location.latitude, location.longitude) }.getOrNull()
    }
    CurrentLocationWeather(weatherDeferred.await(), placeNameDeferred.await())
}
```

Reverse geocoding failures are swallowed with `runCatching { }.getOrNull()` — a missing place name shouldn't prevent the weather itself from loading.

## In-app navigation

`app/src/main/res/navigation/nav_graph.xml` defines two destinations:

```xml
<navigation
    android:id="@+id/nav_graph"
    app:startDestination="@id/weatherFragment">

    <fragment
        android:id="@+id/weatherFragment"
        android:name="com.example.skkniapp.ui.weather.WeatherFragment"
        android:label="@string/weather_title"
        tools:layout="@layout/fragment_weather" />

    <fragment
        android:id="@id/compassFragment"
        android:name="com.example.skkniapp.ui.compass.CompassFragment"
        android:label="@string/compass_title"
        tools:layout="@layout/fragment_compass" />

</navigation>
```

Navigating from the weather dashboard to the wind compass passes the currently-loaded weather's wind data as navigation arguments (`WeatherFragment.kt`):

```kotlin
private fun openWindCompass() {
    val uiModel = currentWeatherUiModel ?: return
    val cityName = viewModel.selectedCityName.value ?: getString(R.string.weather_title)
    val args = CompassFragment.newArgs(
        windDirectionDegrees = uiModel.windDirectionDegrees,
        windDirectionLabel = uiModel.windDirectionLabel,
        windSpeedLabel = uiModel.windSpeedLabel,
        cityName = cityName
    )
    findNavController().navigate(R.id.compassFragment, args)
}
```

This way `CompassFragment` doesn't need its own network/location call — it just visualizes data the weather screen already fetched. See [`docs/SENSORS.md`](SENSORS.md) for how the compass dial itself is rendered.
