# Active Map - Структура проекта

## Основные файлы

### Корень проекта
- `build.gradle.kts` - Корневой build файл с общими плагинами
- `settings.gradle.kts` - Настройки Gradle и модулей
- `gradle.properties` - Свойства Gradle
- `build.sh` - Скрипт для сборки и запуска

### Shared модуль (shared/)
```
shared/
├── build.gradle.kts                    # Build файл модуля
├── src/
│   ├── commonMain/kotlin/com/activemap/shared/
│   │   ├── model/
│   │   │   └── Location.kt            # Модель данных локации
│   │   ├── repository/
│   │   │   ├── LocationRepository.kt  # Интерфейс репозитория
│   │   │   └── InMemoryLocationRepository.kt  # In-memory реализация
│   │   └── viewmodel/
│   │       └── LocationViewModel.kt   # ViewModel для бизнес-логики
│   └── commonTest/kotlin/com/activemap/shared/
│       └── LocationRepositoryTest.kt  # Тесты репозитория
```

### Android модуль (androidApp/)
```
androidApp/
├── build.gradle.kts                    # Build файл Android модуля
├── src/androidMain/
│   ├── AndroidManifest.xml
│   ├── kotlin/com/activemap/android/
│   │   ├── MainActivity.kt            # Главная Activity
│   │   └── ui/
│   │       ├── ActiveMapApp.kt        # Главный Composable
│   │       └── components/
│   │           ├── LocationList.kt     # Список локаций
│   │           ├── LocationDetail.kt   # Детальная карточка
│   │           ├── AddLocationForm.kt  # Форма добавления
│   │           ├── EditLocationForm.kt # Форма редактирования
│   │           └── MapView.kt         # Карта (OsmDroid)
│   └── res/
│       └── values/
│           └── themes.xml            # Темы приложения
```

### Desktop модуль (desktopApp/)
```
desktopApp/
├── build.gradle.kts                    # Build файл Desktop модуля
├── src/desktopMain/kotlin/com/activemap/desktop/
│   ├── Main.kt                         # Точка входа Desktop
│   └── ui/
│       ├── ActiveMapDesktopApp.kt      # Главный Composable
│       └── components/
│           ├── LocationListDesktop.kt  # Список локаций
│           ├── LocationDetailDesktop.kt # Детальная карточка
│           ├── AddLocationFormDesktop.kt # Форма добавления
│           ├── EditLocationFormDesktop.kt # Форма редактирования
│           └── MapViewDesktop.kt       # Карта (Canvas)
```

### Web модуль (webApp/)
```
webApp/
├── build.gradle.kts                    # Build файл Web модуля
├── src/jsMain/
│   ├── kotlin/com/activemap/web/
│   │   ├── Main.kt                     # Точка входа Web
│   │   └── ui/
│   │       ├── ActiveMapWebApp.kt      # Главный Composable
│   │       └── components/
│   │           ├── LocationListWeb.kt  # Список локаций
│   │           ├── LocationDetailWeb.kt # Детальная карточка
│   │           ├── AddLocationFormWeb.kt # Форма добавления
│   │           ├── EditLocationFormWeb.kt # Форма редактирования
│   │           └── MapViewWeb.kt       # Карта (HTML/CSS)
│   └── resources/
│       └── index.html                 # HTML файл для Web
```

## Ключевые компоненты

### Модель данных (Location.kt)
- `id` - Уникальный идентификатор
- `name` - Название (обязательно)
- `activityType` - Тип活动ности (СПОРТ, РАБОТА, ОТДЫХ, ОБРАЗОВАНИЕ, РАЗВЛЕЧЕНИЯ)
- `latitude`, `longitude` - Координаты (обязательно)
- `coverage` - Покрытие
- `lighting` - Освещение
- `inventory` - Инвентарь
- `cleanliness` - Чистота
- `noiseLevel` - Уровень шума
- `rating` - Рейтинг (1-5)
- `status` - Статус (БЫЛ, ХОЧУ СХОДИТЬ, НЕ ПОДХОДИТ)
- `notes` - Заметки
- `photos` - Список фото URL

### Repository паттерн
- `LocationRepository` - Интерфейс для работы с данными
- `InMemoryLocationRepository` - In-memory реализация для MVP

### ViewModel
- `LocationViewModel` - Управление состоянием приложения
- Фильтрация и поиск
- CRUD операции с локациями

## Цвета маркеров по типам活动ности
- **Красный** (#f44336) - СПОРТ
- **Синий** (#2196f3) - РАБОТА
- **Зеленый** (#4caf50) - ОТДЫХ
- **Желтый** (#ffeb3b) - ОБРАЗОВАНИЕ
- **Фиолетовый** (#9c27b0) - РАЗВЛЕЧЕНИЯ

## Запуск

```bash
# Android
./gradlew :androidApp:installDebug

# Desktop
./gradlew :desktopApp:run

# Web
./gradlew :webApp:jsBrowserDevelopmentRun

# Тесты
./gradlew :shared:allTests
```
