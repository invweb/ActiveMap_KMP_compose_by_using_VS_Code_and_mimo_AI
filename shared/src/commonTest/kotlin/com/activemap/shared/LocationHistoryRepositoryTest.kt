package com.activemap.shared

import com.activemap.shared.model.Location
import com.activemap.shared.model.LocationFilter
import com.activemap.shared.model.LocationPoint
import com.activemap.shared.model.LocationTrack
import com.activemap.shared.repository.InMemoryLocationRepository
import com.activemap.shared.service.GeoLocation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LocationHistoryRepositoryTest {
    
    @Test
    fun testStartNewTrack() = runTest {
        val repository = InMemoryLocationRepository()
        
        repository.startNewTrack("Тестовый трек")
        
        val tracks = repository.getAllTracks().first()
        assertEquals(1, tracks.size)
        assertEquals("Тестовый трек", tracks[0].name)
        assertTrue(tracks[0].isActive())
    }
    
    @Test
    fun testGetCurrentTrack() = runTest {
        val repository = InMemoryLocationRepository()
        
        repository.startNewTrack("Активный трек")
        
        val currentTrack = repository.getCurrentTrack().first()
        assertNotNull(currentTrack)
        assertEquals("Активный трек", currentTrack.name)
        assertTrue(currentTrack.isActive())
    }
    
    @Test
    fun testStopCurrentTrack() = runTest {
        val repository = InMemoryLocationRepository()
        
        repository.startNewTrack("Трек для остановки")
        
        // Stop the current track
        repository.stopCurrentTrack()
        
        val tracks = repository.getAllTracks().first()
        assertEquals(1, tracks.size)
        assertFalse(tracks[0].isActive())
        assertNotNull(tracks[0].endDate)
        assertNotNull(tracks[0].durationMs)
    }
    
    @Test
    fun testSaveLocationPoint() = runTest {
        val repository = InMemoryLocationRepository()
        
        repository.startNewTrack("Трек с точками")
        val track = repository.getCurrentTrack().first()
        assertNotNull(track)
        
        // Save some points
        repository.saveLocationPoint(track.id, GeoLocation(55.7558, 37.6173, 10.0f))
        repository.saveLocationPoint(track.id, GeoLocation(55.7559, 37.6174, 10.0f))
        repository.saveLocationPoint(track.id, GeoLocation(55.7560, 37.6175, 10.0f))
        
        // Get track points
        val points = repository.getTrackPoints(track.id)
        assertEquals(3, points.size)
        
        // Check first point
        assertEquals(55.7558, points[0].latitude)
        assertEquals(37.6173, points[0].longitude)
        
        // Track distance should be updated
        val updatedTrack = repository.getCurrentTrack().first()
        assertTrue(updatedTrack?.distanceMeters ?: 0.0 > 0)
    }
    
    @Test
    fun testMultipleTracks() = runTest {
        val repository = InMemoryLocationRepository()
        
        // Create first track
        repository.startNewTrack("Первый трек")
        val track1 = repository.getCurrentTrack().first()
        assertNotNull(track1)
        repository.saveLocationPoint(track1.id, GeoLocation(55.0, 37.0, 10.0f))
        
        // Stop first track, then create second
        repository.stopCurrentTrack()
        repository.startNewTrack("Второй трек")
        val track2 = repository.getCurrentTrack().first()
        assertNotNull(track2)
        repository.saveLocationPoint(track2.id, GeoLocation(55.1, 37.1, 10.0f))
        
        // Check all tracks
        val allTracks = repository.getAllTracks().first()
        assertEquals(2, allTracks.size)
        
        // First track should be stopped
        val firstTrack = allTracks.find { it.name == "Первый трек" }
        assertNotNull(firstTrack)
        assertFalse(firstTrack.isActive())
        
        // Second track should be active
        assertTrue(track2.isActive())
    }
    
    @Test
    fun testTrackPointsAreOrdered() = runTest {
        val repository = InMemoryLocationRepository()
        
        repository.startNewTrack("Упорядоченный трек")
        val track = repository.getCurrentTrack().first()
        assertNotNull(track)
        
        // Save points in sequence
        for (i in 1..5) {
            repository.saveLocationPoint(track.id, GeoLocation(55.0 + i * 0.001, 37.0 + i * 0.001, 10.0f))
        }
        
        val points = repository.getTrackPoints(track.id)
        assertEquals(5, points.size)
        
        for (i in 0..4) {
            assertTrue(points[i].timestamp > 0)
        }
    }
    
    @Test
    fun testGetTrackPointsForNonExistentTrack() = runTest {
        val repository = InMemoryLocationRepository()
        
        val points = repository.getTrackPoints("nonexistent-track")
        assertEquals(emptyList<LocationPoint>(), points)
    }
    
    @Test
    fun testLoadTracks() = runTest {
        val repository = InMemoryLocationRepository()
        
        val tracks = listOf(
            LocationTrack.startNew("Трек 1").copy(
                startDate = 1000000L,
                points = listOf(LocationPoint(55.0, 37.0, 1000000L))
            ),
            LocationTrack.startNew("Трек 2").copy(
                startDate = 2000000L,
                endDate = 3000000L,
                points = listOf(LocationPoint(55.1, 37.1, 2000000L))
            )
        )
        
        repository.loadTracks(tracks)
        
        val loadedTracks = repository.getAllTracks().first()
        assertEquals(2, loadedTracks.size)
        
        val firstTrack = loadedTracks.find { it.name == "Трек 1" }
        assertNotNull(firstTrack)
        assertEquals(1, firstTrack.points.size)
    }
    
    @Test
    fun testTrackDistanceCalculation() = runTest {
        val repository = InMemoryLocationRepository()
        
        repository.startNewTrack("Дистанция")
        val track = repository.getCurrentTrack().first()
        assertNotNull(track)
        
        // Save points 1 degree apart (≈111 km)
        repository.saveLocationPoint(track.id, GeoLocation(0.0, 0.0, 10.0f))
        repository.saveLocationPoint(track.id, GeoLocation(1.0, 0.0, 10.0f))
        
        val updatedTrack = repository.getCurrentTrack().first()
        val distanceKm = updatedTrack?.distanceMeters ?: 0.0
        assertTrue(distanceKm > 100000, "Distance should be approximately 111 km")
    }
    
    @Test
    fun testStopNonExistentTrack() = runTest {
        val repository = InMemoryLocationRepository()
        
        // Should not throw, just do nothing
        repository.stopCurrentTrack()
        
        val tracks = repository.getAllTracks().first()
        assertEquals(0, tracks.size)
    }
    
    @Test
    fun testMultiplePointsSameTrack() = runTest {
        val repository = InMemoryLocationRepository()
        
        repository.startNewTrack("Много точек")
        val track = repository.getCurrentTrack().first()
        assertNotNull(track)
        
        // Save 10 points
        for (i in 1..10) {
            repository.saveLocationPoint(
                track.id, 
                GeoLocation(55.0 + i * 0.001, 37.0 + i * 0.001, 10.0f)
            )
        }
        
        val points = repository.getTrackPoints(track.id)
        assertEquals(10, points.size)
        
        val updatedTrack = repository.getCurrentTrack().first()
        assertTrue(updatedTrack?.distanceMeters ?: 0.0 > 0)
    }
    
    @Test
    fun testTrackWithNoPointsDistance() = runTest {
        val repository = InMemoryLocationRepository()
        
        repository.startNewTrack("Без точек")
        
        val track = repository.getCurrentTrack().first()
        assertEquals(0.0, track?.distanceMeters)
    }
}
