# Mobile Network

← [Back to README](../README.md)

All remote data (weather, geocoding, reverse-geocoding) is fetched over HTTPS from the free, key-less [Open-Meteo API](https://open-meteo.com/) and [BigDataCloud](https://www.bigdatacloud.com/), using **Retrofit2 + OkHttp**, isolated inside the `:data` module.

## API client

`data/src/main/java/com/example/skkniapp/data/remote/WeatherApiService.kt`:

```kotlin
interface WeatherApiService {

    @GET
    suspend fun getCurrentWeather(
        @Url url: String = AppConstants.FORECAST_BASE_URL + AppConstants.FORECAST_PATH,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = AppConstants.WEATHER_CURRENT_PARAMS,
        @Query("daily") daily: String = AppConstants.WEATHER_DAILY_PARAMS,
        @Query("hourly") hourly: String = AppConstants.WEATHER_HOURLY_PARAMS,
        @Query("forecast_days") forecastDays: Int = AppConstants.FORECAST_DAYS,
        @Query("timezone") timezone: String = AppConstants.DEFAULT_TIMEZONE
    ): WeatherResponse

    @GET
    suspend fun searchCity(
        @Url url: String = AppConstants.GEOCODING_BASE_URL + AppConstants.GEOCODING_PATH,
        @Query("name") name: String,
        @Query("count") count: Int = AppConstants.GEOCODING_RESULT_LIMIT,
        @Query("language") language: String = AppConstants.DEFAULT_LANGUAGE
    ): GeocodingResponse

    @GET
    suspend fun reverseGeocode(
        @Url url: String = AppConstants.REVERSE_GEOCODING_BASE_URL + AppConstants.REVERSE_GEOCODING_PATH,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localityLanguage") localityLanguage: String = AppConstants.DEFAULT_LANGUAGE
    ): ReverseGeocodeResponse
}
```

## Retrofit / OkHttp wiring

`data/src/main/java/com/example/skkniapp/data/di/DataModule.kt` (Koin):

```kotlin
single {
    HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
}

single {
    OkHttpClient.Builder()
        .addInterceptor(get<HttpLoggingInterceptor>())
        .build()
}

single {
    Retrofit.Builder()
        .baseUrl(AppConstants.FORECAST_BASE_URL)
        .client(get())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

single {
    get<Retrofit>().create(WeatherApiService::class.java)
}
```

- **OkHttp logging interceptor** runs at `BASIC` level, useful for debugging request/response status and timing without dumping full bodies.
- Each endpoint overrides `@Url` per-call so a single `Retrofit` instance can hit three different base hosts (forecast, geocoding, reverse-geocoding).

## Response handling

Raw DTOs (`data/src/main/java/com/example/skkniapp/data/remote/dto/`) are never exposed past the `:data` module. They're converted into domain models through dedicated mappers (`WeatherResponseMapper`, `GeocodingResponseMapper`) before reaching `WeatherRepositoryImpl`, which implements the `:domain` module's `WeatherRepository` interface.

Network failures (timeouts, non-2xx responses, parsing errors) are caught in the repository/use-case layer and surfaced to the UI as a typed `UiState.Error(message)`, so `WeatherFragment` can render a retry button instead of crashing:

```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

## Required permission

```xml
<uses-permission android:name="android.permission.INTERNET" />
```
