package com.activemap.shared

import com.activemap.shared.model.*
import com.activemap.shared.repository.InMemoryLocationRepository
import com.activemap.shared.service.DataExporter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DataExporterTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testExportEmptyRepository() = runTest {
        val repository = InMemoryLocationRepository()
        val exporter = DataExporter(repository)

        val result = exporter.exportToJson()

        assertEquals("[]", result.trim())
    }

    @Test
    fun testExportAndImportRoundtrip() = runTest {
        val repository = InMemoryLocationRepository()
        val exporter = DataExporter(repository)

        val location = Location(
            id = "export-1",
            name = "Экспорт тест",
            activityType = ActivityType.EDUCATION,
            latitude = 55.75,
            longitude = 37.62,
            rating = 4,
            status = VisitStatus.WAS_THERE,
            coverage = CoverageLevel.FULL,
            lighting = LightingLevel.BRIGHT,
            cleanliness = CleanlinessLevel.PERFECT,
            noiseLevel = NoiseLevel.QUIET,
            notes = "Тестовые заметки"
        )
        repository.addLocation(location)

        val exported = exporter.exportToJson()
        assertTrue(exported.contains("export-1"))
        assertTrue(exported.contains("Экспорт тест"))

        val repository2 = InMemoryLocationRepository()
        val exporter2 = DataExporter(repository2)
        val count = exporter2.importFromJson(exported)

        assertEquals(1, count)
        val imported = repository2.getAllLocations().first()
        assertEquals(1, imported.size)
        assertEquals("export-1", imported[0].id)
        assertEquals("Экспорт тест", imported[0].name)
        assertEquals(ActivityType.EDUCATION, imported[0].activityType)
        assertEquals(4, imported[0].rating)
    }

    @Test
    fun testImportMultipleLocations() = runTest {
        val repository = InMemoryLocationRepository()
        val exporter = DataExporter(repository)

        val locations = listOf(
            Location(id = "a", name = "A", activityType = ActivityType.SPORT, latitude = 1.0, longitude = 2.0),
            Location(id = "b", name = "B", activityType = ActivityType.WORK, latitude = 3.0, longitude = 4.0),
            Location(id = "c", name = "C", activityType = ActivityType.REST, latitude = 5.0, longitude = 6.0)
        )
        for (loc in locations) repository.addLocation(loc)

        val exported = exporter.exportToJson()
        val parsed = json.decodeFromString<List<Location>>(exported)
        assertEquals(3, parsed.size)
    }

    @Test
    fun testImportInvalidJsonThrows() = runTest {
        val repository = InMemoryLocationRepository()
        val exporter = DataExporter(repository)

        assertFailsWith<IllegalArgumentException> {
            exporter.importFromJson("not valid json")
        }
    }

    @Test
    fun testImportEmptyArray() = runTest {
        val repository = InMemoryLocationRepository()
        val exporter = DataExporter(repository)

        val count = exporter.importFromJson("[]")
        assertEquals(0, count)
        assertEquals(0, repository.getAllLocations().first().size)
    }

    @Test
    fun testGetExportFileNameContainsActivemap() {
        val exporter = DataExporter(InMemoryLocationRepository())
        val fileName = exporter.getExportFileName()
        assertTrue(fileName.startsWith("activemap_export_"))
        assertTrue(fileName.endsWith(".json"))
    }
}
