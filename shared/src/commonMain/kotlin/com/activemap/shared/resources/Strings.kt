package com.activemap.shared.resources

import com.activemap.shared.generated.resources.Res
import com.activemap.shared.generated.resources.*
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
object Strings {
    @Composable
    fun appName(): String = stringResource(Res.string.app_name)

    @Composable
    fun map(): String = stringResource(Res.string.map)

    @Composable
    fun list(): String = stringResource(Res.string.list)

    @Composable
    fun addLocation(): String = stringResource(Res.string.add_location)

    @Composable
    fun add(): String = stringResource(Res.string.add)

    @Composable
    fun close(): String = stringResource(Res.string.close)

    @Composable
    fun back(): String = stringResource(Res.string.back)

    @Composable
    fun edit(): String = stringResource(Res.string.edit)

    @Composable
    fun delete(): String = stringResource(Res.string.delete)

    @Composable
    fun save(): String = stringResource(Res.string.save)

    @Composable
    fun saveChanges(): String = stringResource(Res.string.save_changes)

    @Composable
    fun cancel(): String = stringResource(Res.string.cancel)

    @Composable
    fun search(): String = stringResource(Res.string.search)

    @Composable
    fun searchPlaceholder(): String = stringResource(Res.string.search_placeholder)

    @Composable
    fun allTypes(): String = stringResource(Res.string.all_types)

    @Composable
    fun allStatuses(): String = stringResource(Res.string.all_statuses)

    @Composable
    fun type(): String = stringResource(Res.string.type)

    @Composable
    fun status(): String = stringResource(Res.string.status)

    @Composable
    fun rating(): String = stringResource(Res.string.rating)

    @Composable
    fun coordinates(): String = stringResource(Res.string.coordinates)

    @Composable
    fun latitude(): String = stringResource(Res.string.latitude)

    @Composable
    fun longitude(): String = stringResource(Res.string.longitude)

    @Composable
    fun coverage(): String = stringResource(Res.string.coverage)

    @Composable
    fun lighting(): String = stringResource(Res.string.lighting)

    @Composable
    fun inventory(): String = stringResource(Res.string.inventory)

    @Composable
    fun cleanliness(): String = stringResource(Res.string.cleanliness)

    @Composable
    fun noiseLevel(): String = stringResource(Res.string.noise_level)

    @Composable
    fun notes(): String = stringResource(Res.string.notes)

    @Composable
    fun photos(): String = stringResource(Res.string.photos)

    @Composable
    fun photosCommaSeparated(): String = stringResource(Res.string.photos_comma_separated)

    @Composable
    fun ratings(): String = stringResource(Res.string.ratings)

    @Composable
    fun nameRequired(): String = stringResource(Res.string.name_required)

    @Composable
    fun nameIsRequired(): String = stringResource(Res.string.name_is_required)

    @Composable
    fun coordinatesRequired(): String = stringResource(Res.string.coordinates_required)

    @Composable
    fun notSpecified(): String = stringResource(Res.string.not_specified)

    @Composable
    fun noNotes(): String = stringResource(Res.string.no_notes)

    @Composable
    fun noPhotos(): String = stringResource(Res.string.no_photos)

    @Composable
    fun routeMode(): String = stringResource(Res.string.route_mode)

    @Composable
    fun routeStart(): String = stringResource(Res.string.route_start)

    @Composable
    fun routeEnd(): String = stringResource(Res.string.route_end)

    @Composable
    fun routeLine(): String = stringResource(Res.string.route_line)

    @Composable
    fun routeCalculating(): String = stringResource(Res.string.route_calculating)

    @Composable
    fun clearRoute(): String = stringResource(Res.string.clear_route)

    @Composable
    fun selectRoutePoints(): String = stringResource(Res.string.select_route_points)

    @Composable
    fun selectPoint(): String = stringResource(Res.string.select_point)

    @Composable
    fun language(): String = stringResource(Res.string.language)

    @Composable
    fun russian(): String = stringResource(Res.string.russian)

    @Composable
    fun english(): String = stringResource(Res.string.english)

    @Composable
    fun german(): String = stringResource(Res.string.german)

    @Composable
    fun ukrainian(): String = stringResource(Res.string.ukrainian)

    @Composable
    fun settings(): String = stringResource(Res.string.settings)

    @Composable
    fun about(): String = stringResource(Res.string.about)

    @Composable
    fun version(): String = stringResource(Res.string.version)

    @Composable
    fun routeError(): String = stringResource(Res.string.route_error)

    @Composable
    fun centerOnMe(): String = stringResource(Res.string.center_on_me)

    @Composable
    fun routeInfo(distanceKm: Double, durationText: String): String {
        return stringResource(Res.string.route_info, distanceKm, durationText)
    }

    @Composable
    fun noLocations(): String = stringResource(Res.string.no_locations)

    @Composable
    fun legend(): String = stringResource(Res.string.legend)

    @Composable
    fun activitySport(): String = stringResource(Res.string.activity_sport)

    @Composable
    fun activityWork(): String = stringResource(Res.string.activity_work)

    @Composable
    fun activityRest(): String = stringResource(Res.string.activity_rest)

    @Composable
    fun activityEducation(): String = stringResource(Res.string.activity_education)

    @Composable
    fun activityEntertainment(): String = stringResource(Res.string.activity_entertainment)

    @Composable
    fun routeStartMarker(): String = stringResource(Res.string.route_start_marker)

    @Composable
    fun routeEndMarker(): String = stringResource(Res.string.route_end_marker)

    @Composable
    fun nameIsRequiredError(): String = stringResource(Res.string.name_is_required_error)

    @Composable
    fun routeNotFound(): String = stringResource(Res.string.route_not_found)

    @Composable
    fun activityType(): String = stringResource(Res.string.activity_type)

    @Composable
    fun export(): String = stringResource(Res.string.export)

    @Composable
    fun importData(): String = stringResource(Res.string.import_data)

    @Composable
    fun importedCount(count: Int): String = stringResource(Res.string.imported_count, count)

    @Composable
    fun exportError(): String = stringResource(Res.string.export_error)

    @Composable
    fun importError(): String = stringResource(Res.string.import_error)

    @Composable
    fun buildRoute(): String = stringResource(Res.string.build_route)

    @Composable
    fun selectedPoints(count: Int): String = stringResource(Res.string.selected_points, count)

    @Composable
    fun selectAtLeast2(): String = stringResource(Res.string.select_at_least_2)
}
