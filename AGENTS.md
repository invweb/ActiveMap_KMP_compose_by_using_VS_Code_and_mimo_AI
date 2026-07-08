# ActiveMap - Agent Guide

## Project Overview

Kotlin Multiplatform + Compose Multiplatform app for tracking locations. Targets Android, Desktop (JVM), and Web (Kotlin/JS).

## Build & Run

```bash
# Android (install to device/emulator)
./gradlew :androidApp:installDebug

# Desktop (run JVM app)
./gradlew :desktopApp:run

# Web (Kotlin/JS dev server)
./gradlew :webApp:jsBrowserDevelopmentRun

# Tests (shared module only)
./gradlew :shared:allTests

# Clean
./gradlew clean
```

## Architecture

- **shared/** — Common module with models, repository, and ViewModel (commonMain)
- **androidApp/** — Android UI (OsmDroid maps, Room, AndroidX)
- **desktopApp/** — Desktop UI (Compose Desktop, Canvas for maps)
- **webApp/** — Web UI (Kotlin/JS, HTML/CSS for maps)

Entry points:
- Android: `androidApp/src/androidMain/kotlin/com/activemap/android/MainActivity.kt`
- Desktop: `desktopApp/src/desktopMain/kotlin/com/activemap/desktop/Main.kt`
- Web: `webApp/src/jsMain/kotlin/com/activemap/web/Main.kt`

## Key Conventions

- **Language**: UI strings and comments are in Russian. Enums use English names with Russian comments.
- **Gradle**: JVM target is 17. Uses `gradlew` (wrapper present).
- **Experimental flags** in `gradle.properties`:
  - `org.jetbrains.compose.experimental.macos.enabled=true`
  - `org.jetbrains.compose.experimental.jscanvas.enabled=true`
- **Dependencies**: Uses kotlinx (coroutines, serialization, datetime), Compose Multiplatform 1.7.3, Kotlin 2.1.10.
- **Serialization**: All model classes are `@Serializable` (kotlinx.serialization).
- **Testing**: Tests are in `shared/src/commonTest/`. Run with `:shared:allTests`.

## Common Pitfalls

- Desktop maps use Canvas rendering (not OsmDroid). Don't import Android map libraries in desktop source sets.
- Web uses HTML/CSS for maps (not a mapping library). Map implementation is platform-specific.
- The `shared` module is an Android library (`com.android.library`). Platform-specific code goes in `androidMain`, `desktopMain`, `jsMain`.
- Room is only used in Android. Other platforms use in-memory repository for now.

## Module Dependencies

All platform modules depend on `:shared`. Do not introduce circular dependencies.
