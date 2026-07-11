package com.activemap.shared

import com.activemap.shared.model.*
import com.activemap.shared.repository.InMemoryLocationRepository
import com.activemap.shared.service.GeoLocation
import com.activemap.shared.service.LocationService
import com.activemap.shared.viewmodel.LocationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LocationViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: InMemoryLocationRepository
    private lateinit var locationService: FakeLocationService
    private lateinit var viewModel: LocationViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = InMemoryLocationRepository()
        locationService = FakeLocationService()
        viewModel = LocationViewModel(repository, locationService, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        viewModel.close()
        Dispatchers.resetMain()
    }

    private fun makeLocation(
        id: String = "1",
        name: String = "Тест",
        activityType: ActivityType = ActivityType.SPORT,
        latitude: Double = 55.75,
        longitude: Double = 37.62,
        rating: Int = 3
    ) = Location(
        id = id, name = name, activityType = activityType,
        latitude = latitude, longitude = longitude, rating = rating
    )

    @Test
    fun testSaveLocationBlankNameSetsError() {
        var completed = false
        viewModel.saveLocation(makeLocation(name = "")) { completed = true }

        assertEquals("Название обязательно", viewModel.error.value)
        assertTrue(!completed)
    }

    @Test
    fun testSaveLocationInvalidLatitudeSetsError() {
        viewModel.saveLocation(makeLocation(latitude = 91.0)) {}
        assertEquals("Широта должна быть от -90 до 90", viewModel.error.value)
    }

    @Test
    fun testSaveLocationInvalidLongitudeSetsError() {
        viewModel.saveLocation(makeLocation(longitude = 181.0)) {}
        assertEquals("Долгота должна быть от -180 до 180", viewModel.error.value)
    }

    @Test
    fun testSaveLocationRatingTooLowSetsError() {
        viewModel.saveLocation(makeLocation(rating = 0)) {}
        assertEquals("Рейтинг должен быть от 1 до 5", viewModel.error.value)
    }

    @Test
    fun testSaveLocationRatingTooHighSetsError() {
        viewModel.saveLocation(makeLocation(rating = 6)) {}
        assertEquals("Рейтинг должен быть от 1 до 5", viewModel.error.value)
    }

    @Test
    fun testSaveValidLocationSuccess() {
        var completed = false
        viewModel.saveLocation(makeLocation()) { completed = true }

        assertEquals(null, viewModel.error.value)
        assertEquals("Локация добавлена", viewModel.operationSuccess.value)
        assertTrue(completed)
    }

    @Test
    fun testClearError() {
        viewModel.saveLocation(makeLocation(name = "")) {}
        assertNotNull(viewModel.error.value)

        viewModel.clearError()
        assertNull(viewModel.error.value)
    }

    @Test
    fun testClearSuccess() {
        var completed = false
        viewModel.saveLocation(makeLocation()) { completed = true }
        assertNotNull(viewModel.operationSuccess.value)

        viewModel.clearSuccess()
        assertNull(viewModel.operationSuccess.value)
    }

    @Test
    fun testSelectLocation() {
        val location = makeLocation()
        viewModel.selectLocation(location)
        assertEquals(location, viewModel.selectedLocation.value)

        viewModel.selectLocation(null)
        assertNull(viewModel.selectedLocation.value)
    }

    @Test
    fun testDeleteLocation() {
        viewModel.saveLocation(makeLocation(id = "del-1")) {}
        assertEquals("Локация добавлена", viewModel.operationSuccess.value)

        viewModel.deleteLocation("del-1")
        assertEquals("Локация удалена", viewModel.operationSuccess.value)
        assertNull(viewModel.selectedLocation.value)
    }

    @Test
    fun testToggleRouteMode() {
        assertFalse(viewModel.isRouteMode.value)

        viewModel.toggleRouteMode()
        assertTrue(viewModel.isRouteMode.value)

        viewModel.toggleRouteMode()
        assertFalse(viewModel.isRouteMode.value)
    }

    @Test
    fun testSetRoutePointStartAndEnd() {
        viewModel.toggleRouteMode()
        viewModel.setRoutePoint(55.0, 37.0)

        assertEquals(55.0 to 37.0, viewModel.routeStart.value)
        assertNull(viewModel.routeEnd.value)

        viewModel.setRoutePoint(56.0, 38.0)
        assertEquals(56.0 to 38.0, viewModel.routeEnd.value)
    }

    @Test
    fun testClearRoute() {
        viewModel.toggleRouteMode()
        viewModel.setRoutePoint(55.0, 37.0)
        viewModel.setRoutePoint(56.0, 38.0)

        viewModel.clearRoute()
        assertNull(viewModel.routeStart.value)
        assertNull(viewModel.routeEnd.value)
        assertNull(viewModel.currentRoute.value)
    }

    @Test
    fun testCenterOnMeSuccess() {
        locationService.currentLocation = GeoLocation(55.75, 37.62, 10f)

        viewModel.centerOnMe()

        assertNotNull(viewModel.currentLocation.value)
        assertEquals(55.75, viewModel.currentLocation.value!!.latitude)
        assertEquals(false, viewModel.isLocationLoading.value)
    }

    @Test
    fun testCenterOnMeNullLocationSetsError() {
        locationService.currentLocation = null

        viewModel.centerOnMe()

        assertEquals("Не удалось определить местоположение", viewModel.error.value)
        assertEquals(false, viewModel.isLocationLoading.value)
    }

    @Test
    fun testStartAddingLocation() {
        viewModel.startAddingLocation()
        assertTrue(viewModel.isAddingLocation.value)
        assertNull(viewModel.pickedLatLng.value)
    }

    @Test
    fun testStartAddingLocationAtLatLng() {
        viewModel.startAddingLocationAt(55.0, 37.0)
        assertTrue(viewModel.isAddingLocation.value)
        assertEquals(55.0 to 37.0, viewModel.pickedLatLng.value)
    }

    @Test
    fun testCancelAddingLocation() {
        viewModel.startAddingLocationAt(55.0, 37.0)
        viewModel.cancelAddingLocation()

        assertEquals(false, viewModel.isAddingLocation.value)
        assertNull(viewModel.pickedLatLng.value)
    }
}

private fun assertFalse(value: Boolean) {
    assertEquals(false, value)
}

class FakeLocationService : LocationService {
    var currentLocation: GeoLocation? = null
    var shouldThrow: Boolean = false

    override suspend fun getCurrentLocation(): GeoLocation? {
        if (shouldThrow) throw RuntimeException("Location error")
        return currentLocation
    }

    override fun getLocationUpdates(intervalMs: Long): Flow<GeoLocation> {
        throw NotImplementedError()
    }

    override fun hasPermission(): Boolean = true

    override suspend fun requestPermission(): Boolean = true
}
