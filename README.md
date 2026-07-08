# Active Map

Kotlin Multiplatform + Compose Multiplatform приложение для отслеживания и каталогизации локаций. Поддерживает Android, Desktop (JVM) и Web (Kotlin/JS).

## Стек

- **Kotlin 2.1.10** + **Compose Multiplatform 1.7.3**
- **Android**: OsmDroid (карты), Room (хранение), Compose Material 3
- **Desktop**: Compose Desktop, Canvas (рендеринг карт)
- **Web**: Kotlin/JS, HTML/CSS ( Leaflet.js для карт)
- **Общий модуль**: kotlinx.coroutines, kotlinx.serialization, kotlinx.datetime

## Структура

```
ActiveMap/
├── shared/             # Общий модуль: модели, ViewModel, репозиторий
├── androidApp/         # Android UI
├── desktopApp/         # Desktop UI (JVM)
├── webApp/             # Web UI (Kotlin/JS)
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Запуск

```bash
# Требуется JDK 17+ (brew install openjdk@17)

# Android (эмулятор или устройство)
./gradlew :androidApp:installDebug

# Desktop
./gradlew :desktopApp:run

# Web (dev-сервер)
./gradlew :webApp:jsBrowserDevelopmentRun

# Тесты
./gradlew :shared:allTests
```

## Функциональность

- **Карта** с маркерами локаций (цвета по типу активности), long press для выбора точки
- **Список локаций** с фильтрацией по типу/статусу и поиском по названию
- **Добавление локации** с полной формой и валидацией (название и координаты обязательны)
- **Детальная карточка** с просмотром и редактированием
- **Сохранение данных** между перезапусками (Room на Android, JSON на других платформах)

## Модель данных

| Поле | Типы |
|------|------|
| Название | строка (обязательно) |
| Тип активности | СПОРТ, РАБОТА, ОТДЫХ, ОБРАЗОВАНИЕ, РАЗВЛЕЧЕНИЯ |
| Координаты | широта, долгота (обязательно) |
| Покрытие | НЕТ, ЧАСТИЧНОЕ, СРЕДНЕЕ, ПОЛНОЕ |
| Освещение | НЕТ, СЛАБОЕ, СРЕДНЕЕ, ЯРКОЕ |
| Чистота | ГРЯЗНО, ПЛОХО, СРЕДНЕЕ, ЧИСТО, ИДЕАЛЬНО |
| Шум | ТИХО, НЕМНОГО ШУМА, СРЕДНИЙ ШУМ, ШУМНО, ОЧЕНЬ ШУМНО |
| Инвентарь | строка |
| Рейтинг | 1-5 |
| Статус | БЫЛ, ХОЧУ СХОДИТЬ, НЕ ПОДХОДИТ |
| Заметки | строка |
| Фото | список ссылок |

## Архитектура

- **shared** — `commonMain`: модели (`@Serializable`), `LocationViewModel`, `LocationRepository`. Платформенные реализации в `androidMain`, `desktopMain`, `jsMain`
- **androidApp** — навигация через состояние (`when`), OsmDroid, Room
- **desktopApp** — Canvas-based карта, in-memory репозиторий
- **webApp** — HTML/CSS интерфейс, Leaflet.js для карт

## Требования

- JDK 17+
- Android SDK (API 34+)
- Gradle 9.6.1 (wrapper в проекте)

## Лицензия

MIT
