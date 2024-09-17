package general

import de.unisaarland.cs.se.selab.parsing.MapParser
import java.io.File
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapParserTest {
    @TempDir
    lateinit var tempDir: File
    private lateinit var mapFile: File
    private lateinit var mapParser: MapParser

    @BeforeEach
    fun setUp() {
        // Create a temporary map file
        mapFile = File(tempDir, "map.json")
        mapParser = MapParser(mapFile.path)
    }

    @Test
    fun `test parseMap with valid data`() {
        // Write valid JSON data to the map file
        val jsonData = """
            {
                "tiles": [
                    {
                        "id": 1,
                        "coordinates": {"x": 0, "y": 0},
                        "category": "LAND"
                    },
                    {
                        "id": 2,
                        "coordinates": {"x": 1, "y": 0},
                        "category": "SHORE",
                        "harbor": true
                    }
                ]
            }
        """.trimIndent()
        mapFile.writeText(jsonData)

        // Call parseMap and assert the result
        val result = mapParser.parseMap()
        assertTrue(result)
    }

    @Test
    fun `test parseMap with invalid JSON`() {
        // Write invalid JSON data to the map file
        val jsonData = "{ invalid json }"
        mapFile.writeText(jsonData)

        // Call parseMap and assert the result
        val result = mapParser.parseMap()
        assertFalse(result)
    }

    @Test
    fun `test parseMap with invalid tile data`() {
        // Write JSON data with invalid tile to the map file
        val jsonData = """
            {
                "tiles": [
                    {
                        "id": 1,
                        "coordinates": {"x": 0, "y": 0},
                        "category": "INVALID_CATEGORY"
                    }
                ]
            }
        """.trimIndent()
        mapFile.writeText(jsonData)

        // Call parseMap and assert the result
        val result = mapParser.parseMap()
        assertFalse(result)
    }

    @Test
    fun `test parseMap with valid and invalid tiles`() {
        // Write JSON data with both valid and invalid tiles to the map file
        val jsonData = """
            {
                "tiles": [
                    {
                        "id": 1,
                        "coordinates": {"x": 0, "y": 0},
                        "category": "LAND"
                    },
                    {
                        "id": 2,
                        "coordinates": {"x": 1, "y": 0},
                        "category": "INVALID_CATEGORY"
                    }
                ]
            }
        """.trimIndent()
        mapFile.writeText(jsonData)

        // Call parseMap and assert the result
        val result = mapParser.parseMap()
        assertFalse(result)
    }

    @Test
    fun `test parseMap with duplicate tile IDs`() {
        // Write JSON data with duplicate tile IDs to the map file
        val jsonData = """
            {
                "tiles": [
                    {
                        "id": 1,
                        "coordinates": {"x": 0, "y": 0},
                        "category": "LAND"
                    },
                    {
                        "id": 1,
                        "coordinates": {"x": 1, "y": 0},
                        "category": "SHORE"
                    }
                ]
            }
        """.trimIndent()
        mapFile.writeText(jsonData)

        // Call parseMap and assert the result
        val result = mapParser.parseMap()
        assertFalse(result)
    }
}
