# ActiveMap

Kotlin Multiplatform + Compose Multiplatform application for tracking and cataloging locations. Supports Android, Desktop (JVM) and Web (Kotlin/JS).

## Screenshots

### Main map view
![Map view](screenshots/map-main.png)

### Route building
![Route building](screenshots/map-route.png)

### Location history
![Location history](screenshots/history.png)

## Stack

- **Kotlin 2.1.10** + **Compose Multiplatform 1.7.3**
- **Android**: OsmDroid (Maps), Room (Storage), Compose Material 3
- **Desktop**: Compose Desktop, Canvas (map rendering)
- **Web**: Kotlin/JS, HTML/CSS, Leaflet.js for maps
- **Common**: Koin (DI), kotlinx.coroutines, kotlinx.serialization, kotlinx.datetime, Ktor (HTTP)

## Structure

```
ActiveMap/
├── shared/             # Common module: models, ViewModel, Repository, shared UI, services
│   └── commonMain/
│       ├── model/      # Location, Route, LocationTrack, LocationPoint, enums
│       ├── repository/ # LocationRepository interface, InMemoryLocationRepository, RoomLocationRepository
│       ├── viewmodel/  # LocationViewModel (DI-ready)
│       ├── service/    # OsrmService, OfflineRouteService, LocationService, DataExporter
│       ├── ui/         # Shared Compose UI (SharedActiveMapApp, history screen, forms, lists)
│       ├── di/         # Koin app module
│       └── resources/  # Localization (RU/EN/DE/UK)
├── androidApp/         # Android: Room, OsmDroid, Koin Android module, LocationHistory repository
├── desktopApp/         # Desktop: JSON file storage, Canvas map, Koin Desktop module
├── webApp/             # Web: localStorage, Leaflet.js, Koin Web module
├── .github/workflows/  # CI/CD (GitHub Actions)
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Launch

```bash
# Requires JDK 17+ (brew install openjdk@17)

# Android (emulator or device)
./gradlew :androidApp:installDebug

# Desktop
./gradlew :desktopApp:run

# Web (dev server)
./gradlew :webApp:jsBrowserDevelopmentRun

# Tests
./gradlew :shared:desktopTest :shared:testDebugUnitTest :shared:testReleaseUnitTest :shared:jsNodeTest
```

## Functionality

- **Map** with location markers (colors by activity type), long press to select a point
- **Geolocation** — center map on current position (Android with permission request, Web via browser API)
- **Routing** — build routes between two points via OSRM API (with offline straight-line fallback)
- **Location history** — track and save movement paths with automatic GPS sampling
- **List of locations** with filtering by type/status and search by name
- **Adding a location** with full form and validation (name, coordinates, rating required)
- **Detailed card** with viewing and editing
- **Data persistence** — Room (Android), JSON file `~/.activemap/locations.json` (Desktop), localStorage (Web)
- **Export/Import** — JSON export and import of all locations
- **Localization** — Russian, English, German, Ukrainian
- **Error handling** — validation errors, operation feedback via Snackbar

## Features

### Location History Tracking

- **Automatic GPS sampling** — records location points every 5 seconds
- **Distance calculation** — computes total distance using Haversine formula
- **Track management** — start/stop tracking, view history
- **Persistent storage** — Room database (Android), in-memory + file serialization (Desktop/Web)
- **Track statistics** — duration, distance, number of points

### Technical Implementation

| Component | Description |
|-----------|-------------|
| `LocationPoint` | GPS coordinate with timestamp and accuracy |
| `LocationTrack` | Sequence of points with computed distance/duration |
| `RoomLocationRepository` | Android Room DAOs for tracks and points |
| `InMemoryLocationRepository` | Cross-platform in-memory with persistence |
| `LocationViewModel` | State management for tracking (start/stop, updates) |
| `SharedHistoryScreen` | Compose UI for history list and current track |

### Data Model (Tracks)

| Field | Type | Description |
|-------|------|-------------|
| id | String | Unique track identifier |
| name | String | User-assigned name |
| startDate | Long | Track start timestamp |
| endDate | Long? | Track end timestamp (null if active) |
| points | List<LocationPoint> | GPS coordinates |
| distanceMeters | Double | Total distance (computed) |
| durationMs | Long? | Total duration (computed) |

### Tests

- `LocationHistoryModelTest.kt` — 14 tests for models, serialization, distance calculation
- `LocationHistoryRepositoryTest.kt` — 14 tests for repository operations, track lifecycle

## Architecture

- **shared** — common module with `@Serializable` models, `LocationViewModel` with DI, `LocationRepository` interface, shared Compose UI (`SharedActiveMapApp`, history screen, forms, lists), services (`OsrmService`, `OfflineRouteService`, `LocationService`, `DataExporter`). Platform-specific implementations in `androidMain`, `desktopMain`, `jsMain`.
- **DI** — Koin with platform-specific modules providing `LocationRepository` and `LocationService`
- **AndroidApp** — Room database with TrackDao/TrackPointDao, OsmDroid maps, location permissions, Koin Android module
- **DesktopApp** — JSON file storage, Canvas-based map, Koin Desktop module
- **WebApp** — localStorage, Leaflet.js maps, Koin Web module
- **CI/CD** — GitHub Actions: build Android + Desktop, run all tests (JVM, Android, JS Node)

## Data model

| Field | Types |
|------|------|
| Name | line (required) |
| Type of activity | SPORT, WORK, REST, EDUCATION, ENTERTAINMENT |
| Coordinates | latitude (-90..90), longitude (-180..180) (required) |
| Coverage | NONE, PARTIAL, MEDIUM, FULL |
| Lighting | NONE, LOW, MEDIUM, BRIGHT |
| Cleanliness | DIRTY, POOR, MEDIUM, CLEAN, PERFECT |
| Noise | QUIET, LOW, MEDIUM, LOUD, VERY_LOUD |
| Inventory | line |
| Rating | 1-5 (required) |
| Status | WAS_THERE, WANT_TO_VISIT, NOT_SUITABLE |
| Notes | line |
| Photos | list of links |
| Created/Updated | timestamps |

## Location History Model

| Field | Type |
|-------|------|
| latitude | Double |
| longitude | Double |
| timestamp | Long |
| accuracy | Float? |
| speed | Float? |

## Requirements

- JDK 17+
- Android SDK (API 34+)
- Gradle 9.6.1 (wrapper in the project)
- Node.js (for JS tests)

## About

This project was built using **GigaCode** — an AI-powered coding assistant. The location history tracking feature was added on July 12, 2026.

### Technologies used during development

| Category | Tool |
|----------|------|
| AI assistant | GigaCode (Sber) |
| Language | Kotlin 2.1.10 |
| UI framework | Compose Multiplatform 1.7.3 |
| Build system | Gradle 9.6.1 + AGP 8.8.2 |
| IDE | VS Code + MiMo Code extension |
| Maps (Android) | OsmDroid 6.1.18 |
| Maps (Desktop) | Canvas (OSM tiles) |
| Maps (Web) | Leaflet.js |
| DI | Koin |
| Networking | Ktor (OSRM API) |
| Serialization | kotlinx.serialization |
| Storage | Room (Android), JSON (Desktop), localStorage (Web) |
| CI/CD | GitHub Actions |

### Development timeline

| Date | Milestone |
|------|-----------|
| Jul 9 | Initial commit — project scaffold, shared module, Android/Desktop/Web apps |
| Jul 10 | Route building (OSRM), localization system (RU/EN/DE/UK) |
| Jul 11 | Major refactoring — DI, shared UI, persistence, error handling, geolocation, export/import, tests, CI/CD, Leaflet.js maps, desktop zoom |
| Jul 12 | Bug fixes — Koin crash, marker tap in route mode, route info formatting |
| Jul 12 | Location history tracking — models, repository, Room, UI, tests (28 tests added) |

## License

MIT