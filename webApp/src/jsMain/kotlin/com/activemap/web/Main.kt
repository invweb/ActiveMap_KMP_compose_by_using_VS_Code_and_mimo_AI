package com.activemap.web

import com.activemap.web.model.*
import com.activemap.web.data.LocalStorageLocationRepository
import kotlinx.browser.document
import kotlinx.browser.window

private val repo = LocalStorageLocationRepository()
private var currentLang = AppLanguage.ENGLISH
private var screen = "map"
private var detailId: String? = null
private var searchQuery = ""
private var leafletMap: dynamic = null
private val markers = mutableListOf<dynamic>()
private var savedZoom: Int = 6
private var savedCenter: dynamic = null

private fun t(key: String): String {
    val map = when (currentLang) {
        AppLanguage.ENGLISH -> enStrings
        AppLanguage.GERMAN -> deStrings
        AppLanguage.RUSSIAN -> ruStrings
    }
    return map[key] ?: key
}

private val enStrings = mapOf(
    "appName" to "ActiveMap", "map" to "Map", "list" to "List",
    "addNew" to "+ Add", "route" to "Route", "legend" to "Legend",
    "noLocations" to "No locations. Click on the map to add one.",
    "activitySport" to "Sport", "activityWork" to "Work", "activityRest" to "Rest",
    "activityEducation" to "Education", "activityEntertainment" to "Entertainment",
    "addLocation" to "Add Location", "close" to "Close", "cancel" to "Cancel",
    "save" to "Save", "delete" to "Delete", "edit" to "Edit", "back" to "Back",
    "nameRequired" to "Name *", "activityType" to "Activity Type",
    "coordinatesRequired" to "Coordinates *", "latitude" to "Latitude",
    "longitude" to "Longitude", "searchPlaceholder" to "Search locations...",
    "type" to "Type", "status" to "Status", "rating" to "Rating",
    "details" to "Details", "saved" to "Location saved!",
    "deleteConfirm" to "Delete this location?"
)
private val deStrings = mapOf(
    "appName" to "ActiveMap", "map" to "Karte", "list" to "Liste",
    "addNew" to "+ Hinzufügen", "route" to "Route", "legend" to "Legende",
    "noLocations" to "Keine Standorte. Klicken Sie auf die Karte.",
    "activitySport" to "Sport", "activityWork" to "Arbeit", "activityRest" to "Erholung",
    "activityEducation" to "Bildung", "activityEntertainment" to "Unterhaltung",
    "addLocation" to "Standort hinzufügen", "close" to "Schließen", "cancel" to "Abbrechen",
    "save" to "Speichern", "delete" to "Löschen", "edit" to "Bearbeiten", "back" to "Zurück",
    "nameRequired" to "Name *", "activityType" to "Aktivitätstyp",
    "coordinatesRequired" to "Koordinaten *", "latitude" to "Breitengrad",
    "longitude" to "Längengrad", "searchPlaceholder" to "Standorte suchen...",
    "type" to "Typ", "status" to "Status", "rating" to "Bewertung",
    "details" to "Details", "saved" to "Standort gespeichert!",
    "deleteConfirm" to "Standort löschen?"
)
private val ruStrings = mapOf(
    "appName" to "ActiveMap", "map" to "Карта", "list" to "Список",
    "addNew" to "+ Добавить", "route" to "Маршрут", "legend" to "Легенда",
    "noLocations" to "Нет локаций. Нажмите на карту чтобы добавить.",
    "activitySport" to "Спорт", "activityWork" to "Работа", "activityRest" to "Отдых",
    "activityEducation" to "Образование", "activityEntertainment" to "Развлечения",
    "addLocation" to "Добавить локацию", "close" to "Закрыть", "cancel" to "Отмена",
    "save" to "Сохранить", "delete" to "Удалить", "edit" to "Редактировать", "back" to "Назад",
    "nameRequired" to "Название *", "activityType" to "Тип деятельности",
    "coordinatesRequired" to "Координаты *", "latitude" to "Широта",
    "longitude" to "Долгота", "searchPlaceholder" to "Поиск локаций...",
    "type" to "Тип", "status" to "Статус", "rating" to "Рейтинг",
    "details" to "Детали", "saved" to "Локация сохранена!",
    "deleteConfirm" to "Удалить эту локацию?"
)

private val markerColors = mapOf(
    ActivityType.SPORT to "#f44336", ActivityType.WORK to "#2196f3",
    ActivityType.REST to "#4caf50", ActivityType.EDUCATION to "#ffeb3b",
    ActivityType.ENTERTAINMENT to "#9c27b0"
)

fun main() {
    window.asDynamic().app = js("{}")
    val app = window.asDynamic().app
    app.switchLang = { code: String -> switchLang(code) }
    app.saveLocation = { saveLocation() }
    app.selectLoc = { id: String -> selectLocation(id) }
    app.deleteLoc = { id: String -> deleteLocation(id) }
    app.showMap = { screen = "map"; detailId = null; render() }
    app.showList = { screen = "list"; detailId = null; render() }
    app.showAdd = { screen = "add"; detailId = null; render() }
    app.goBack = { detailId = null; render() }
    app.doSearch = { q: String -> searchQuery = q; render() }
    render()
}

private fun render() {
    val root = document.getElementById("root") ?: return
    val locations = repo.getLocations()
    val langs = listOf(AppLanguage.ENGLISH, AppLanguage.GERMAN, AppLanguage.RUSSIAN)

    val content = when {
        screen == "add" -> renderAddForm()
        detailId != null -> renderDetail(locations.find { it.id == detailId })
        screen == "list" -> renderList(locations)
        else -> "<div id=\"map\"></div>"
    }

    root.innerHTML = """
    <div style="font-family:Arial,sans-serif;margin:0;padding:0;background:white;height:100vh;display:flex;flex-direction:column">
        <div style="background:#1976d2;color:white;padding:10px 12px;display:flex;align-items:center;flex-wrap:wrap;gap:8px;flex-shrink:0">
            <b style="font-size:20px">${t("appName")}</b>
            <button onclick="app.showMap()" style="padding:6px 12px;background:${if(screen=="map"&&detailId==null)"white" else "#1565c0"};color:${if(screen=="map"&&detailId==null)"#1976d2" else "white"};border:1px solid #1565c0;border-radius:4px;cursor:pointer;font-size:14px">${t("map")}</button>
            <button onclick="app.showList()" style="padding:6px 12px;background:${if(screen=="list")"white" else "#1565c0"};color:${if(screen=="list")"#1976d2" else "white"};border:1px solid #1565c0;border-radius:4px;cursor:pointer;font-size:14px">${t("list")}</button>
            <select onchange="app.switchLang(this.value)" style="padding:6px;border-radius:4px;border:1px solid #ccc;cursor:pointer;font-size:14px">
                ${langs.joinToString("") { l -> "<option value='${l.code}' ${if(l==currentLang) "selected" else ""}>${l.displayName}</option>" }}
            </select>
        </div>
        <div style="flex:1;overflow:auto">$content</div>
    </div>
    """.trimIndent()

    if (screen == "map" && detailId == null) {
        if (leafletMap != null) {
            savedZoom = leafletMap.getZoom() as Int
            savedCenter = leafletMap.getCenter()
        }
        initLeafletMap(locations)
        if (leafletMap != null && savedCenter != null) {
            leafletMap.setView(savedCenter, savedZoom)
        }
    }
}

private fun initLeafletMap(locations: List<Location>) {
    val L = js("L")
    val mapEl = document.getElementById("map") ?: return

    if (leafletMap != null) {
        leafletMap.remove()
        leafletMap = null
    }
    markers.clear()

    leafletMap = L.map(mapEl).setView(L.latLng(48.5, 31.2), 6)

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", js(
        "{ attribution: '© OpenStreetMap contributors', maxZoom: 19 }"
    )).addTo(leafletMap)

    val allLocations = repo.getLocations()
    allLocations.forEach { loc ->
        val color = markerColors[loc.activityType] ?: "#999"
        val iconHtml = "<div style='background:$color;width:24px;height:24px;border-radius:50%;border:2px solid black;transform:translate(-50%,-50%);cursor:pointer'></div>"
        val iconOpts = js("Object()")
        iconOpts.className = ""
        iconOpts.html = iconHtml
        iconOpts.iconSize = arrayOf(24, 24)
        iconOpts.iconAnchor = arrayOf(12, 12)
        val icon = L.divIcon(iconOpts)
        val markerOpts = js("Object()")
        markerOpts.icon = icon
        val marker = L.marker(L.latLng(loc.latitude, loc.longitude), markerOpts).addTo(leafletMap)
        val popup = "<b>${loc.name}</b><br>${loc.activityType.name}<br>${t("rating")}: ${loc.rating}/5<br><button onclick=\"app.selectLoc('${loc.id}')\" style=\"margin-top:4px;padding:4px 8px;background:#2196f3;color:white;border:none;border-radius:4px;cursor:pointer\">${t("details")}</button>"
        marker.bindPopup(popup)
        markers.add(marker)
    }

    leafletMap.on("click", { event: dynamic ->
        val lat = event.latlng.lat as Double
        val lng = event.latlng.lng as Double
        showAddAtLocation(lat, lng)
    })

    if (allLocations.isNotEmpty()) {
        if (allLocations.size == 1) {
            leafletMap.setView(L.latLng(allLocations[0].latitude, allLocations[0].longitude), 12)
        } else {
            val bounds = L.latLngBounds(js("[]"))
            allLocations.forEach { loc ->
                bounds.extend(L.latLng(loc.latitude, loc.longitude))
            }
            leafletMap.fitBounds(bounds, js("({ padding: [50, 50], maxZoom: 14 })"))
        }
    }
}

private fun showAddAtLocation(lat: Double, lng: Double) {
    screen = "add"
    detailId = null
    render()
    val latEl = document.getElementById("add-lat")
    val lngEl = document.getElementById("add-lng")
    if (latEl != null) latEl.asDynamic().value = lat.toString()
    if (lngEl != null) lngEl.asDynamic().value = lng.toString()
}

private fun renderAddForm(): String {
    return """
    <div style="max-width:500px;margin:0 auto;padding:16px">
        <h2 style="margin:0 0 16px">${t("addLocation")}</h2>
        <label style="display:block;margin-bottom:4px"><b>${t("nameRequired")}</b></label>
        <input id="add-name" style="width:100%;padding:12px;border:1px solid #ccc;border-radius:4px;font-size:16px;margin-bottom:16px;box-sizing:border-box">
        <label style="display:block;margin-bottom:4px"><b>${t("activityType")}</b></label>
        <select id="add-type" style="width:100%;padding:12px;border:1px solid #ccc;border-radius:4px;font-size:16px;margin-bottom:16px;box-sizing:border-box;font-size:16px">
            <option value="SPORT">${t("activitySport")}</option>
            <option value="WORK">${t("activityWork")}</option>
            <option value="REST">${t("activityRest")}</option>
            <option value="EDUCATION">${t("activityEducation")}</option>
            <option value="ENTERTAINMENT">${t("activityEntertainment")}</option>
        </select>
        <label style="display:block;margin-bottom:4px"><b>${t("latitude")}</b></label>
        <input id="add-lat" type="number" step="any" style="width:100%;padding:12px;border:1px solid #ccc;border-radius:4px;font-size:16px;margin-bottom:16px;box-sizing:border-box">
        <label style="display:block;margin-bottom:4px"><b>${t("longitude")}</b></label>
        <input id="add-lng" type="number" step="any" style="width:100%;padding:12px;border:1px solid #ccc;border-radius:4px;font-size:16px;margin-bottom:16px;box-sizing:border-box">
        <div style="display:flex;gap:8px">
            <button onclick="app.saveLocation()" style="flex:1;padding:12px;background:#4caf50;color:white;border:1px solid #388e3c;border-radius:4px;font-size:16px;cursor:pointer">${t("save")}</button>
            <button onclick="app.showMap()" style="flex:1;padding:12px;background:#f5f5f5;color:#333;border:1px solid #ccc;border-radius:4px;font-size:16px;cursor:pointer">${t("cancel")}</button>
        </div>
    </div>
    """.trimIndent()
}

private fun renderList(locations: List<Location>): String {
    val items = locations.joinToString("") { loc ->
        """<div onclick="app.selectLoc('${loc.id}')" style="padding:16px;margin-bottom:8px;background:white;border:1px solid #e0e0e0;border-radius:8px;cursor:pointer;max-width:600px;margin-left:auto;margin-right:auto">
            <b style="font-size:18px">${loc.name}</b><br>
            <span style="color:#666">${t("type")}: ${loc.activityType.name}</span><br>
            <span style="color:#666">${t("status")}: ${loc.status.name}</span> &middot;
            <span style="color:#666">${t("rating")}: ${loc.rating}/5</span> &middot;
            <span style="color:#666">${loc.latitude.asDynamic().toFixed(4)}, ${loc.longitude.asDynamic().toFixed(4)}</span>
        </div>"""
    }
    return items.ifEmpty { "<p style='color:#666;font-size:18px;text-align:center;padding:40px'>${t("noLocations")}</p>" }
}

private fun renderDetail(loc: Location?): String {
    if (loc == null) { screen = "map"; detailId = null; return "<div id=\"map\"></div>" }
    return """
    <div style="max-width:500px;margin:0 auto;padding:16px">
        <button onclick="app.goBack()" style="margin-bottom:16px;padding:8px 16px;background:#f5f5f5;color:#333;border:1px solid #ccc;border-radius:4px;cursor:pointer">${t("back")}</button>
        <h2 style="margin:0 0 16px">${loc.name}</h2>
        <div style="background:#f5f5f5;border-radius:8px;padding:16px;margin-bottom:16px">
            <p style="margin:4px 0"><b>${t("type")}:</b> <span style="display:inline-block;width:12px;height:12px;border-radius:50%;background:${markerColors[loc.activityType] ?: "#999"};vertical-align:middle"></span> ${loc.activityType.name}</p>
            <p style="margin:4px 0"><b>${t("status")}:</b> ${loc.status.name}</p>
            <p style="margin:4px 0"><b>${t("rating")}:</b> ${"★".repeat(loc.rating)}${"☆".repeat(5 - loc.rating)} (${loc.rating}/5)</p>
            <p style="margin:4px 0"><b>${t("latitude")}:</b> ${loc.latitude.asDynamic().toFixed(6)}</p>
            <p style="margin:4px 0"><b>${t("longitude")}:</b> ${loc.longitude.asDynamic().toFixed(6)}</p>
            ${if(loc.notes.isNotEmpty()) "<p style='margin:4px 0'><b>${t("notes")}:</b> ${loc.notes}</p>" else ""}
            ${if(loc.inventory.isNotEmpty()) "<p style='margin:4px 0'><b>${t("inventory")}</b>: ${loc.inventory}</p>" else ""}
        </div>
        <div style="display:flex;gap:8px">
            <button onclick="app.deleteLoc('${loc.id}')" style="padding:10px 20px;background:#f44336;color:white;border:1px solid #d32f2f;border-radius:4px;cursor:pointer;font-size:14px">${t("delete")}</button>
        </div>
    </div>
    """.trimIndent()
}

private fun selectLocation(id: String) {
    detailId = id
    screen = "detail"
    render()
}

private fun saveLocation() {
    val name = (document.getElementById("add-name")?.asDynamic()?.value as? String)?.trim() ?: ""
    val type = (document.getElementById("add-type")?.asDynamic()?.value as? String) ?: "SPORT"
    val lat = (document.getElementById("add-lat")?.asDynamic()?.value as? String)?.toDoubleOrNull()
    val lng = (document.getElementById("add-lng")?.asDynamic()?.value as? String)?.toDoubleOrNull()
    if (name.isBlank() || lat == null || lng == null) return
    val location = Location(
        id = js("Date.now()").toString(),
        name = name,
        activityType = ActivityType.valueOf(type),
        latitude = lat, longitude = lng
    )
    repo.addLocation(location)
    screen = "map"
    render()
}

private fun switchLang(code: String) {
    currentLang = AppLanguage.entries.find { it.code == code } ?: AppLanguage.ENGLISH
    render()
}

private fun deleteLocation(id: String) {
    repo.deleteLocation(id)
    screen = "map"
    detailId = null
    render()
}
