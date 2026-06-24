# SKKNI App

An Android weather application (Kotlin) with automatic location-based weather, city search, a favorites carousel, hourly/daily forecasts, and a wind compass. Built with a multi-module clean architecture and Koin for dependency injection.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Implementation Notes](#implementation-notes)
- [Deep-Dive Docs](#deep-dive-docs)

## Features

### Platform & Language
- Native Android app written entirely in Kotlin, targeting `minSdk` 24 and `compileSdk`/`targetSdk` 36.
- Single-Activity architecture (`MainActivity`) with AndroidX Navigation handling fragment transitions (`WeatherFragment` ↔ `CompassFragment`).
- View Binding for all layouts (no `findViewById`), Koin for dependency injection across modules.

### Data Persistence
- Favorite cities are persisted locally with **Room** (SQLite) so they survive app restarts.
- Remote weather/geocoding data is fetched on demand via Retrofit and mapped into immutable domain models; no remote data is cached beyond the favorites table.
- 📄 **Full schema, entity/DAO code, and migration policy: [`docs/DATABASE.md`](docs/DATABASE.md)**

### Location-Based Service & Navigation
- Current-location weather using **Google Play Services Location** (`FusedLocationProviderClient`), requested through the runtime `ACCESS_FINE_LOCATION` permission flow.
- Reverse geocoding (coordinates → place name) and forward geocoding (city name search → coordinates) via the Open-Meteo Geocoding API and BigDataCloud reverse-geocoding endpoint.
- In-app navigation graph (`nav_graph.xml`) routes between the weather dashboard and the wind-compass screen, passing wind data as navigation arguments.
- 📄 **Full location flow, permission handling, and geocoding details: [`docs/LOCATION.md`](docs/LOCATION.md)**

### Mobile Interface
- Responsive, scrollable dashboard (`fragment_weather.xml`) built with `ConstraintLayout`, Material Components, shimmer skeleton loaders, and empty/error states with retry actions.
- Favorites are presented as a **ViewPager2** carousel (up to 3 cities per page) with a dot-indicator strip below it; the indicator auto-hides when there is only one page.
- City search with a debounced text watcher and a result dropdown list (`RecyclerView`).
- Expandable daily forecast list and a per-day hourly forecast `BottomSheetDialogFragment`.

### Mobile Security Fundamentals
- `android:allowBackup="false"`, runtime location permission, HTTPS-only networking, and `singleInstance`/empty-`taskAffinity` hardening on `MainActivity`.
- **Runtime Application Self-Protection (RASP)** via [freeRASP](https://docs.talsec.app/freerasp) (Talsec): root/jailbreak, emulator, debugger, hooking frameworks (Frida/Xposed), tampering, untrusted install sources, multi-instance, malware, unlocked bootloader, dev mode, ADB.
- 📄 **Full integration code, Gradle/repo setup, and pre-release checklist: [`docs/SECURITY.md`](docs/SECURITY.md)**

### Mobile Sensors
- Shake-to-refresh via the accelerometer (`ShakeDetector`), with a magnitude threshold and debounce/cooldown window to avoid false positives.
- Wind-compass screen combining accelerometer + magnetometer readings with a low-pass-filtered rotation animation.
- 📄 **Full sensor code and tuning constants: [`docs/SENSORS.md`](docs/SENSORS.md)**

### Mobile Network
- All HTTP calls use **Retrofit2** + **OkHttp** (with a `HttpLoggingInterceptor` for debugging).
- Responses parsed with Gson and mapped through dedicated mapper classes before reaching the UI layer.
- Network/API failures surface as a typed `UiState.Error` so the UI can show a retry affordance instead of crashing.
- 📄 **Full API client, Retrofit/OkHttp wiring, and error handling: [`docs/NETWORK.md`](docs/NETWORK.md)**

## Architecture

The codebase is split into 4 Gradle modules following clean-architecture layering (dependencies only point inward):

```
:core      (pure Kotlin/JVM — no business or infrastructure logic)
  └─ AppConstants.kt, UiState.kt

:domain    (pure Kotlin/JVM — business rules)
  ├─ model/        WeatherDomainModel, CityLocationDomainModel, etc.
  ├─ repository/   WeatherRepository (interface)
  └─ usecase/      WeatherDashboardUseCase
  depends on → :core

:data      (Android library — all infrastructure details)
  ├─ remote/       WeatherApiService (Retrofit) + DTOs
  ├─ local/        Room (AppDatabase, FavoriteCityDao, FavoriteCityEntity)
  ├─ mapper/       DTO → domain model mappers
  ├─ repository/   WeatherRepositoryImpl
  └─ di/DataModule.kt (Koin)
  depends on → :domain, :core

:app       (UI layer — fragments, view models, adapters)
  ├─ ui/weather/   WeatherFragment, WeatherViewModel, adapters & UI models
  ├─ ui/compass/   CompassFragment
  ├─ security/     RaspGuard (freeRASP integration)
  ├─ di/ViewModelModule.kt
  └─ res/          single shared resource pool (layouts, drawables, strings)
  depends on → :domain, :data, :core
```

- **Dependency Injection**: [Koin](https://insert-koin.io/) — each layer registers its own module (`dataModule` in `:data`, `ViewModelModule` in `:app`), wired together in `SkkniApp.kt`.
- **UI state**: wrapped in a sealed `UiState<T>` (`Loading` / `Success` / `Error`) exposed as `StateFlow` from the ViewModel and collected by the Fragment.

## Tech Stack

| Category | Library |
|---|---|
| Language | Kotlin |
| Dependency Injection | Koin (`koin-android`) |
| Networking | Retrofit2 + Gson converter, OkHttp logging interceptor |
| Local Storage | Room (with KSP annotation processing) |
| Concurrency | Kotlin Coroutines + `kotlinx-coroutines-play-services` |
| UI | View Binding, ConstraintLayout, Material Components, RecyclerView, **ViewPager2** |
| Location | Google Play Services Location |
| Loading Placeholder | Facebook Shimmer |
| Navigation | AndroidX Navigation (fragment + UI) |
| App Security (RASP) | [freeRASP](https://docs.talsec.app/freerasp) — `TalsecSecurity-Community` |

Weather and geocoding data is sourced from the free, key-less [Open-Meteo API](https://open-meteo.com/) (forecast + geocoding) and [BigDataCloud](https://www.bigdatacloud.com/) (reverse geocoding), accessed through `WeatherApiService` in the `:data` module — see [`docs/NETWORK.md`](docs/NETWORK.md).

## Getting Started

### Prerequisites

- Android Studio compatible with AGP 8.13.x
- JDK 11
- Android SDK: `compileSdk` / `targetSdk` 36, `minSdk` 24

### Run

1. Clone this repository.
2. Open it in Android Studio and let Gradle sync finish.
3. Run the `app` configuration on an emulator or physical device (API 24+).
4. On first launch, grant the location permission (`ACCESS_FINE_LOCATION`) to see weather for your current location.

Build from the command line:

```bash
./gradlew :app:assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/`.

> ⚠️ Before shipping a **signed release build**, you must configure freeRASP with your real release signing certificate hash — see [`docs/SECURITY.md`](docs/SECURITY.md#configuring-freerasp-before-a-release-build), otherwise the app will flag itself as tampered.

## Project Structure

```
app/
  src/main/java/com/example/skkniapp/
    MainActivity.kt, SkkniApp.kt
    di/ViewModelModule.kt
    security/     ← RaspGuard (freeRASP)
    ui/weather/   ← WeatherFragment, WeatherViewModel, adapters, mappers, UI models
    ui/compass/   ← CompassFragment
  src/main/res/   ← layouts, drawables, values (single shared resource pool)

core/    src/main/java/com/example/skkniapp/core/        ← AppConstants, UiState
domain/  src/main/java/com/example/skkniapp/domain/       ← models, repository interface, use case
data/    src/main/java/com/example/skkniapp/data/         ← remote (Retrofit + DTOs), local (Room), mappers, repository impl, DI

docs/    DATABASE.md, SECURITY.md, NETWORK.md, SENSORS.md ← deep-dive documentation
```

## Implementation Notes

- **Favorites carousel**: `FavoriteCitiesPagerAdapter` splits the favorites list into pages of at most 3 cities (`chunked(3)`); each page renders its items dynamically into a `LinearLayout`, left-packed (no stretching when the last page has fewer than 3 items). The dot indicator (`llDotsIndicator`) is only shown when there is more than one page.
- **Permission flow**: location is requested via `ActivityResultContracts.RequestPermission`; if denied, the app falls back to a Snackbar message and skips loading location-based weather.
- **Favorite star icon**: hidden while the weather state is still `Loading`, to avoid the star flashing into view before the newly selected city's data has actually finished loading.

## Deep-Dive Docs

These were split out of this README to keep it scannable — each one is self-contained with full code snippets:

| Doc | Covers |
|---|---|
| [`docs/DATABASE.md`](docs/DATABASE.md) | Room schema, entity/DAO code, migration policy |
| [`docs/SECURITY.md`](docs/SECURITY.md) | Platform hardening + full freeRASP (RASP) integration & pre-release setup |
| [`docs/NETWORK.md`](docs/NETWORK.md) | Retrofit/OkHttp client, API definitions, error handling |
| [`docs/SENSORS.md`](docs/SENSORS.md) | Shake-to-refresh and wind-compass sensor code |
| [`docs/LOCATION.md`](docs/LOCATION.md) | Location flow, permission handling, geocoding |
