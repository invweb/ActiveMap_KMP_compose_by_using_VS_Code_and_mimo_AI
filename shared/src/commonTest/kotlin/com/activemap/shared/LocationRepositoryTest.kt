package com.activemap.shared

import com.activemap.shared.model.*
import com.activemap.shared.repository.InMemoryLocationRepository
import com.activemap.shared.service.OfflineRouteService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LocationRepositoryTest {
    
    @Test
    fun testAddLocation() = runTest {
        val repository = InMemoryLocationRepository()
        
        val location = Location(
            id = "1",
            name = "Тестовая локация",
            activityType = ActivityType.SPORT,
            latitude = 55.7558,
            longitude = 37.6173
        )
        
        repository.addLocation(location)
        
        val locations = repository.getAllLocations().first()
        assertEquals(1, locations.size)
        assertEquals("Тестовая локация", locations[0].name)
    }
    
    @Test
    fun testFilterByActivityType() = runTest {
        val repository = InMemoryLocationRepository()
        
        val sportLocation = Location(
            id = "1",
            name = "Спортивная площадка",
            activityType = ActivityType.SPORT,
            latitude = 55.7558,
            longitude = 37.6173
        )
        
        val workLocation = Location(
            id = "2",
            name = "Офис",
            activityType = ActivityType.WORK,
            latitude = 55.7559,
            longitude = 37.6174
        )
        
        repository.addLocation(sportLocation)
        repository.addLocation(workLocation)
        
        val filter = LocationFilter(activityType = ActivityType.SPORT)
        val filteredLocations = repository.getFilteredLocations(filter).first()
        
        assertEquals(1, filteredLocations.size)
        assertEquals("Спортивная площадка", filteredLocations[0].name)
    }
    
    @Test
    fun testSearchByName() = runTest {
        val repository = InMemoryLocationRepository()
        
        val location1 = Location(
            id = "1",
            name = "Парк Горького",
            activityType = ActivityType.REST,
            latitude = 55.7558,
            longitude = 37.6173
        )
        
        val location2 = Location(
            id = "2",
            name = "Офис",
            activityType = ActivityType.WORK,
            latitude = 55.7559,
            longitude = 37.6174
        )
        
        repository.addLocation(location1)
        repository.addLocation(location2)
        
        val filter = LocationFilter(searchQuery = "Парк")
        val filteredLocations = repository.getFilteredLocations(filter).first()
        
        assertEquals(1, filteredLocations.size)
        assertEquals("Парк Горького", filteredLocations[0].name)
    }
    
    @Test
    fun testDeleteLocation() = runTest {
        val repository = InMemoryLocationRepository()
        
        val location = Location(
            id = "1",
            name = "Тестовая локация",
            activityType = ActivityType.SPORT,
            latitude = 55.7558,
            longitude = 37.6173
        )
        
        repository.addLocation(location)
        assertEquals(1, repository.getAllLocations().first().size)
        
        repository.deleteLocation("1")
        assertEquals(0, repository.getAllLocations().first().size)
    }
    
    @Test
    fun testUpdateLocation() = runTest {
        val repository = InMemoryLocationRepository()
        
        val location = Location(
            id = "1",
            name = "Тестовая локация",
            activityType = ActivityType.SPORT,
            latitude = 55.7558,
            longitude = 37.6173
        )
        
        repository.addLocation(location)
        
        val updatedLocation = location.copy(name = "Обновленная локация")
        repository.updateLocation(updatedLocation)
        
        val retrievedLocation = repository.getLocationById("1")
        assertNotNull(retrievedLocation)
        assertEquals("Обновленная локация", retrievedLocation.name)
    }
    
    @Test
    fun testFilterByStatus() = runTest {
        val repository = InMemoryLocationRepository()
        
        val wasThere = Location(
            id = "1",
            name = "Посещённая",
            activityType = ActivityType.SPORT,
            latitude = 55.7558,
            longitude = 37.6173,
            status = VisitStatus.WAS_THERE
        )
        
        val wantToVisit = Location(
            id = "2",
            name = "Хочу посетить",
            activityType = ActivityType.SPORT,
            latitude = 55.7559,
            longitude = 37.6174,
            status = VisitStatus.WANT_TO_VISIT
        )
        
        repository.addLocation(wasThere)
        repository.addLocation(wantToVisit)
        
        val filter = LocationFilter(status = VisitStatus.WAS_THERE)
        val filtered = repository.getFilteredLocations(filter).first()
        
        assertEquals(1, filtered.size)
        assertEquals("Посещённая", filtered[0].name)
    }
    
    @Test
    fun testFilterByMultipleCriteria() = runTest {
        val repository = InMemoryLocationRepository()
        
        repository.addLocation(Location(
            id = "1",
            name = "Спортзал",
            activityType = ActivityType.SPORT,
            latitude = 55.7558,
            longitude = 37.6173,
            status = VisitStatus.WAS_THERE
        ))
        repository.addLocation(Location(
            id = "2",
            name = "Парк",
            activityType = ActivityType.REST,
            latitude = 55.7559,
            longitude = 37.6174,
            status = VisitStatus.WAS_THERE
        ))
        repository.addLocation(Location(
            id = "3",
            name = "Офис",
            activityType = ActivityType.WORK,
            latitude = 55.7560,
            longitude = 37.6175,
            status = VisitStatus.WANT_TO_VISIT
        ))
        
        val filter = LocationFilter(
            activityType = ActivityType.SPORT,
            status = VisitStatus.WAS_THERE
        )
        val filtered = repository.getFilteredLocations(filter).first()
        
        assertEquals(1, filtered.size)
        assertEquals("Спортзал", filtered[0].name)
    }
    
    @Test
    fun testGetLocationById() = runTest {
        val repository = InMemoryLocationRepository()
        
        val location = Location(
            id = "test-id",
            name = "Тест",
            activityType = ActivityType.WORK,
            latitude = 55.7558,
            longitude = 37.6173
        )
        
        repository.addLocation(location)
        
        val found = repository.getLocationById("test-id")
        assertNotNull(found)
        assertEquals("Тест", found.name)
        
        val notFound = repository.getLocationById("nonexistent")
        assertEquals(null, notFound)
    }
}