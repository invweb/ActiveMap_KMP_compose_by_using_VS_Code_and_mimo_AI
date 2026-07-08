package com.activemap.shared

import com.activemap.shared.model.*
import com.activemap.shared.repository.InMemoryLocationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
}
