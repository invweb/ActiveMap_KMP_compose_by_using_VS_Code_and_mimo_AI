# Active Map

Kotlin Multiplatform + Compose Multiplatform is an application for tracking and cataloging locations. Supports Android, Desktop (JVM) and Web (Kotlin/JS).

## Stack

- **Kotlin 2.1.10** + ** Compose Multiplatform 1.7.3**
- ** Android**: OsmDroid (Maps), Room (Storage), Compose Material 3
- **Desktop**: Compose Desktop, Canvas (map rendering)
- **Web**: Kotlin/JS, HTML/CSS ( Leaflet.js for maps)
- **Common module**: kotlinx.coroutines, kotlinx.serialization, kotlinx.datetime

## Structure

```
ActiveMap/
├── shared/             # Common module: models, ViewModel, Repository
├── androidApp/         # Android UI
├── desktopApp/         # Desktop UI (JVM)
├── webApp/             # Web UI (Kotlin/JS)
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
./gradlew :shared:allTests
```

## Functionality

- **Map** with location markers (colors according to activity type), long press to select a point
- **List of locations** with filtering by type/status and search by name
- **Adding a location** with full form and validation (name and coordinates are required)
- **Detailed card** with viewing and editing
- **Saving data** between restarts (Room on Android, JSON on other platforms)

## Data model

| Field | Types |
|------|------|
| Name | line (required) |
| Type of activity | SPORTS, WORK, LEISURE, EDUCATION, ENTERTAINMENT |
| Coordinates | latitude, longitude (required) |
| Coverage | NONE, PARTIAL, MEDIUM, FULL |
| Lighting | NO, LOW, MEDIUM, BRIGHT |
| Cleanliness | DIRTY, BAD, AVERAGE, CLEAN, PERFECT |
| Noise | QUIET, LITTLE NOISE, MEDIUM NOISE, NOISY, VERY NOISY |
| Inventory | row |
| Rating | 1-5 |
| Status | I WAS, I WANT TO GO, NOT SUITABLE |
| Notes | line |
| Photo | list of links |

## Architecture

- **shared** — `commonMain': models (`@Serializable`), `LocationViewModel', `LocationRepository'. Platform implementations in `androidMain', `Desktops', `jsMain`
- **AndroidApp** — navigation through state (`when`), OsmDroid, Room
- **Desktops** — Canvas-based map, in-memory repository
- **webApp** — HTML/CSS interface, Leaflet.js for maps

## Requirements

- JDK 17+
- Android SDK (API 34+)
- Gradle 9.6.1 (wrapper in the project)

## License

MIT
