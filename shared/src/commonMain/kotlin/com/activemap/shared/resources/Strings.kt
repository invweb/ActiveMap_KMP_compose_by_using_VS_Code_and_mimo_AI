package com.activemap.shared.resources

import activemap.shared.generated.resources.Res
import activemap.shared.generated.resources.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.currentLocale

object Strings {
    @Composable
    private fun currentLanguage(): String = currentLocale().language

    @Composable
    fun appName(): String = Res.string.app_name.getString()

    @Composable
    fun map(): String = Res.string.map.getString()

    @Composable
    fun list(): String = Res.string.list.getString()

    @Composable
    fun addLocation(): String = Res.string.add_location.getString()

    @Composable
    fun add(): String = Res.string.add.getString()

    @Composable
    fun close(): String = Res.string.close.getString()

    @Composable
    fun back(): String = Res.string.back.getString()

    @Composable
    fun edit(): String = Res.string.edit.getString()

    @Composable
    fun delete(): String = Res.string.delete.getString()

    @Composable
    fun save(): String = Res.string.save.getString()

    @Composable
    fun saveChanges(): String = Res.string.save_changes.getString()

    @Composable
    fun cancel(): String = Res.string.cancel.getString()

    @Composable
    fun search(): String = Res.string.search.getString()

    @Composable
    fun searchPlaceholder(): String = Res.string.search_placeholder.getString()

    @Composable
    fun allTypes(): String = Res.string.all_types.getString()

    @Composable
    fun allStatuses(): String = Res.string.all_statuses.getString()

    @Composable
    fun type(): String = Res.string.type.getString()

    @Composable
    fun status(): String = Res.string.status.getString()

    @Composable
    fun rating(): String = Res.string.rating.getString()

    @Composable
    fun coordinates(): String = Res.string.coordinates.getString()

    @Composable
    fun latitude(): String = Res.string.latitude.getString()

    @Composable
    fun longitude(): String = Res.string.longitude.getString()

    @Composable
    fun coverage(): String = Res.string.coverage.getString()

    @Composable
    fun lighting(): String = Res.string.lighting.getString()

    @Composable
    fun inventory(): String = Res.string.inventory.getString()

    @Composable
    fun cleanliness(): String = Res.string.cleanliness.getString()

    @Composable
    fun noiseLevel(): String = Res.string.noise_level.getString()

    @Composable
    fun notes(): String = Res.string.notes.getString()

    @Composable
    fun photos(): String = Res.string.photos.getString()

    @Composable
    fun photosCommaSeparated(): String = Res.string.photos_comma_separated.getString()

    @Composable
    fun ratings(): String = Res.string.ratings.getString()

    @Composable
    fun nameRequired(): String = Res.string.name_required.getString()

    @Composable
    fun nameIsRequired(): String = Res.string.name_is_required.getString()

    @Composable
    fun coordinatesRequired(): String = Res.string.coordinates_required.getString()

    @Composable
    fun notSpecified(): String = Res.string.not_specified.getString()

    @Composable
    fun noNotes(): String = Res.string.no_notes.getString()

    @Composable
    fun noPhotos(): String = Res.string.no_photos.getString()

    @Composable
    fun routeMode(): String = Res.string.route_mode.getString()

    @Composable
    fun routeStart(): String = Res.string.route_start.getString()

    @Composable
    fun routeEnd(): String = Res.string.route_end.getString()

    @Composable
    fun routeLine(): String = Res.string.route_line.getString()

    @Composable
    fun routeCalculating(): String = Res.string.route_calculating.getString()

    @Composable
    fun clearRoute(): String = Res.string.clear_route.getString()

    @Composable
    fun selectRoutePoints(): String = Res.string.select_route_points.getString()

    @Composable
    fun selectPoint(): String = Res.string.select_point.getString()

    @Composable
    fun language(): String = Res.string.language.getString()

    @Composable
    fun russian(): String = Res.string.russian.getString()

    @Composable
    fun english(): String = Res.string.english.getString()

    @Composable
    fun german(): String = Res.string.german.getString()

    @Composable
    fun ukrainian(): String = Res.string.ukrainian.getString()

    @Composable
    fun settings(): String = Res.string.settings.getString()

    @Composable
    fun about(): String = Res.string.about.getString()

    @Composable
    fun version(): String = Res.string.version.getString()

    @Composable
    fun routeError(): String = Res.string.route_error.getString()

    @Composable
    fun centerOnMe(): String = Res.string.center_on_me.getString()

    @Composable
    fun routeInfo(distanceKm: Double, durationText: String): String {
        return Res.string.route_info.getString(distanceKm, durationText)
    }

    @Composable
    fun noLocations(): String = Res.string.no_locations.getString()

    @Composable
    fun legend(): String = Res.string.legend.getString()

    @Composable
    fun activitySport(): String = Res.string.activity_sport.getString()

    @Composable
    fun activityWork(): String = Res.string.activity_work.getString()

    @Composable
    fun activityRest(): String = Res.string.activity_rest.getString()

    @Composable
    fun activityEducation(): String = Res.string.activity_education.getString()

    @Composable
    fun activityEntertainment(): String = Res.string.activity_entertainment.getString()

    @Composable
    fun routeStartMarker(): String = Res.string.route_start_marker.getString()

    @Composable
    fun routeEndMarker(): String = Res.string.route_end_marker.getString()

    @Composable
    fun nameIsRequiredError(): String = Res.string.name_is_required_error.getString()

    @Composable
    fun routeNotFound(): String = Res.string.route_not_found.getString()
}
