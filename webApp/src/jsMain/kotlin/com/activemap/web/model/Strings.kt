package com.activemap.web.model

import androidx.compose.runtime.*

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    GERMAN("de", "Deutsch"),
    RUSSIAN("ru", "Русский");
}

object LocaleState {
    var currentLanguage by mutableStateOf(AppLanguage.ENGLISH)
        private set

    fun setLanguage(lang: AppLanguage) {
        currentLanguage = lang
    }
}

private val en = mapOf(
    "appName" to "ActiveMap", "map" to "Map", "list" to "List",
    "addLocation" to "Add Location", "add" to "Add", "close" to "Close",
    "back" to "Back", "edit" to "Edit", "delete" to "Delete",
    "save" to "Save", "saveChanges" to "Save Changes", "cancel" to "Cancel",
    "search" to "Search", "searchPlaceholder" to "Search locations...",
    "nameRequired" to "Name *", "nameIsRequired" to "Name is required",
    "activityType" to "Activity Type", "coordinatesRequired" to "Coordinates *",
    "coverage" to "Coverage", "lighting" to "Lighting", "inventory" to "Inventory",
    "cleanliness" to "Cleanliness", "noiseLevel" to "Noise Level",
    "rating" to "Rating", "status" to "Status", "notes" to "Notes",
    "noNotes" to "No notes", "photos" to "Photos", "noPhotos" to "No photos",
    "photosCommaSeparated" to "Photos (comma separated)",
    "type" to "Type", "coordinates" to "Coordinates", "ratings" to "Ratings",
    "latitude" to "Latitude", "longitude" to "Longitude",
    "notSpecified" to "Not specified", "allTypes" to "All", "allStatuses" to "All",
    "noLocations" to "No locations. Click + Add to add one.",
    "legend" to "Legend",
    "activitySport" to "Sport", "activityWork" to "Work", "activityRest" to "Rest",
    "activityEducation" to "Education", "activityEntertainment" to "Entertainment",
    "routeStart" to "Route Start", "routeEnd" to "Route End", "routeLine" to "Route",
    "routeStartMarker" to "Start", "routeEndMarker" to "Finish",
    "selectPoint" to "Select point", "routeMode" to "Route Mode",
    "clearRoute" to "Clear Route", "centerOnMe" to "My Location",
    "buildRoute" to "Build Route", "route" to "Route", "addNew" to "+ Add",
    "selectedPoints" to "Selected points: ", "routeInfo" to " km, "
)

private val de = mapOf(
    "appName" to "ActiveMap", "map" to "Karte", "list" to "Liste",
    "addLocation" to "Standort hinzufügen", "add" to "Hinzufügen", "close" to "Schließen",
    "back" to "Zurück", "edit" to "Bearbeiten", "delete" to "Löschen",
    "save" to "Speichern", "saveChanges" to "Änderungen speichern", "cancel" to "Abbrechen",
    "search" to "Suchen", "searchPlaceholder" to "Standorte suchen...",
    "nameRequired" to "Name *", "nameIsRequired" to "Name ist erforderlich",
    "activityType" to "Aktivitätstyp", "coordinatesRequired" to "Koordinaten *",
    "coverage" to "Abdeckung", "lighting" to "Beleuchtung", "inventory" to "Inventar",
    "cleanliness" to "Sauberkeit", "noiseLevel" to "Lärmpegel",
    "rating" to "Bewertung", "status" to "Status", "notes" to "Notizen",
    "noNotes" to "Keine Notizen", "photos" to "Fotos", "noPhotos" to "Keine Fotos",
    "photosCommaSeparated" to "Fotos (kommagetrennt)",
    "type" to "Typ", "coordinates" to "Koordinaten", "ratings" to "Bewertungen",
    "latitude" to "Breitengrad", "longitude" to "Längengrad",
    "notSpecified" to "Nicht angegeben", "allTypes" to "Alle", "allStatuses" to "Alle",
    "noLocations" to "Keine Standorte. Klicken Sie auf + Add.",
    "legend" to "Legende",
    "activitySport" to "Sport", "activityWork" to "Arbeit", "activityRest" to "Erholung",
    "activityEducation" to "Bildung", "activityEntertainment" to "Unterhaltung",
    "routeStart" to "Routenstart", "routeEnd" to "Routenende", "routeLine" to "Route",
    "routeStartMarker" to "Start", "routeEndMarker" to "Ziel",
    "selectPoint" to "Punkt auswählen", "routeMode" to "Routenmodus",
    "clearRoute" to "Route löschen", "centerOnMe" to "Mein Standort",
    "buildRoute" to "Route erstellen", "route" to "Route", "addNew" to "+ Hinzufügen",
    "selectedPoints" to "Ausgewählte Punkte: ", "routeInfo" to " km, "
)

private val ru = mapOf(
    "appName" to "ActiveMap", "map" to "Карта", "list" to "Список",
    "addLocation" to "Добавить локацию", "add" to "Добавить", "close" to "Закрыть",
    "back" to "Назад", "edit" to "Редактировать", "delete" to "Удалить",
    "save" to "Сохранить", "saveChanges" to "Сохранить изменения", "cancel" to "Отмена",
    "search" to "Поиск", "searchPlaceholder" to "Поиск локаций...",
    "nameRequired" to "Название *", "nameIsRequired" to "Название обязательно",
    "activityType" to "Тип деятельности", "coordinatesRequired" to "Координаты *",
    "coverage" to "Покрытие", "lighting" to "Освещение", "inventory" to "Инвентарь",
    "cleanliness" to "Чистота", "noiseLevel" to "Уровень шума",
    "rating" to "Рейтинг", "status" to "Статус", "notes" to "Заметки",
    "noNotes" to "Нет заметок", "photos" to "Фото", "noPhotos" to "Нет фото",
    "photosCommaSeparated" to "Фото (через запятую)",
    "type" to "Тип", "coordinates" to "Координаты", "ratings" to "Оценки",
    "latitude" to "Широта", "longitude" to "Долгота",
    "notSpecified" to "Не указано", "allTypes" to "Все", "allStatuses" to "Все",
    "noLocations" to "Нет локаций. Нажмите + Добавить.",
    "legend" to "Легенда",
    "activitySport" to "Спорт", "activityWork" to "Работа", "activityRest" to "Отдых",
    "activityEducation" to "Образование", "activityEntertainment" to "Развлечения",
    "routeStart" to "Начало маршрута", "routeEnd" to "Конец маршрута", "routeLine" to "Маршрут",
    "routeStartMarker" to "Старт", "routeEndMarker" to "Финиш",
    "selectPoint" to "Выберите точку", "routeMode" to "Режим маршрута",
    "clearRoute" to "Очистить маршрут", "centerOnMe" to "Моё положение",
    "buildRoute" to "Построить маршрут", "route" to "Маршрут", "addNew" to "+ Добавить",
    "selectedPoints" to "Выбрано точек: ", "routeInfo" to " км, "
)

object Strings {
    private fun t(key: String): String {
        val map = when (LocaleState.currentLanguage) {
            AppLanguage.ENGLISH -> en
            AppLanguage.GERMAN -> de
            AppLanguage.RUSSIAN -> ru
        }
        return map[key] ?: key
    }

    fun appName() = t("appName")
    fun map() = t("map")
    fun list() = t("list")
    fun addLocation() = t("addLocation")
    fun add() = t("add")
    fun close() = t("close")
    fun back() = t("back")
    fun edit() = t("edit")
    fun delete() = t("delete")
    fun save() = t("save")
    fun saveChanges() = t("saveChanges")
    fun cancel() = t("cancel")
    fun search() = t("search")
    fun searchPlaceholder() = t("searchPlaceholder")
    fun nameRequired() = t("nameRequired")
    fun nameIsRequired() = t("nameIsRequired")
    fun activityType() = t("activityType")
    fun coordinatesRequired() = t("coordinatesRequired")
    fun coverage() = t("coverage")
    fun lighting() = t("lighting")
    fun inventory() = t("inventory")
    fun cleanliness() = t("cleanliness")
    fun noiseLevel() = t("noiseLevel")
    fun rating() = t("rating")
    fun status() = t("status")
    fun notes() = t("notes")
    fun noNotes() = t("noNotes")
    fun photos() = t("photos")
    fun noPhotos() = t("noPhotos")
    fun photosCommaSeparated() = t("photosCommaSeparated")
    fun type() = t("type")
    fun coordinates() = t("coordinates")
    fun ratings() = t("ratings")
    fun latitude() = t("latitude")
    fun longitude() = t("longitude")
    fun notSpecified() = t("notSpecified")
    fun allTypes() = t("allTypes")
    fun allStatuses() = t("allStatuses")
    fun noLocations() = t("noLocations")
    fun legend() = t("legend")
    fun activitySport() = t("activitySport")
    fun activityWork() = t("activityWork")
    fun activityRest() = t("activityRest")
    fun activityEducation() = t("activityEducation")
    fun activityEntertainment() = t("activityEntertainment")
    fun routeStart() = t("routeStart")
    fun routeEnd() = t("routeEnd")
    fun routeLine() = t("routeLine")
    fun routeStartMarker() = t("routeStartMarker")
    fun routeEndMarker() = t("routeEndMarker")
    fun selectPoint() = t("selectPoint")
    fun routeMode() = t("routeMode")
    fun clearRoute() = t("clearRoute")
    fun centerOnMe() = t("centerOnMe")
    fun buildRoute() = t("buildRoute")
    fun route() = t("route")
    fun addNew() = t("addNew")
    fun selectedPoints(n: Int) = "${t("selectedPoints")}$n"
    fun routeInfo(distKm: Double, duration: String) = "$distKm${t("routeInfo")}$duration"
}
