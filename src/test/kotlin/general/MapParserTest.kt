package general

import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.parsing.MapParser
import org.junit.jupiter.api.Assertions.*
import java.io.File
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
    @Test
    fun `test parseMap with shore tile having current`() {
        // Write JSON data with a shore tile that has current information
        val jsonData = """
        {
            "tiles": [
                {
                    "id": 1,
                    "coordinates": {"x": 0, "y": 0},
                    "category": "SHORE",
                    "current": true,
                    "direction": 180,
                    "speed": 5,
                    "intensity": 3
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
    fun `test getNavManager`() {
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

        // Call parseMap to populate the map
        val parseResult = mapParser.parseMap()
        assertTrue(parseResult)

        // Call getNavManager and assert the result
        val navManager = mapParser.getNavManager()
        assertNotNull(navManager)
        assertEquals(2, navManager.tiles.size)
    }

    @Test
    fun `test parseMap with odd y coordinate`() {
        // Write JSON data with a tile that has an odd y coordinate
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
                    "coordinates": {"x": 1, "y": 1},
                    "category": "LAND"
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
    fun `test parseMap with odd y coordinate for deep ocean`() {
        // Write JSON data with a tile that has an odd y coordinate
        val jsonData = """
        {
            "tiles": [
                {
                    "id": 1,
                    "coordinates": {"x": 0, "y": 0},
                    "category": "DEEP_OCEAN",
                    "current": false
                },
                {
                    "id": 2,
                    "coordinates": {"x": 1, "y": 1},
                    "category": "DEEP_OCEAN",
                    "current": false
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
    fun `not correct neighbouring tiles for land`() {
        // Write JSON data with a tile that has an odd y coordinate
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
                    "category": "DEEP_OCEAN",
                    "current": false
                },
                {
                    "id": 3,
                    "coordinates": {"x": 1, "y": 1},
                    "category": "DEEP_OCEAN",
                    "current": false
                },
                {
                    "id": 4,
                    "coordinates": {"x": 0, "y": 1},
                    "category": "DEEP_OCEAN",
                    "current": false
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
    fun `not correct neighbouring tiles for shallow ocean`() {
        // Write JSON data with a tile that has an odd y coordinate
        val jsonData = """
        {
            "tiles": [
                {
                    "id": 1,
                    "coordinates": {"x": 0, "y": 0},
                    "category": "SHALLOW_OCEAN",
                },
                {
                    "id": 2,
                    "coordinates": {"x": 1, "y": 0},
                    "category": "LAND"
                },
                {
                    "id": 3,
                    "coordinates": {"x": 1, "y": 1},
                    "category": "LAND"
                },
                {
                    "id": 4,
                    "coordinates": {"x": 0, "y": 1},
                    "category": "LAND"
                },
            ]
        }
    """.trimIndent()
        mapFile.writeText(jsonData)

        // Call parseMap and assert the result
        val result = mapParser.parseMap()
        assertFalse(result)
    }
    @Test
    fun `not correct neighbouring tiles for deep ocean`() {
        // Write JSON data with a tile that has an odd y coordinate
        val jsonData = """
        {
            "tiles": [
                {
                    "id": 1,
                    "coordinates": {"x": 0, "y": 0},
                    "category": "DEEP_OCEAN",
                },
                {
                    "id": 2,
                    "coordinates": {"x": 1, "y": 0},
                    "category": "LAND"
                },
                {
                    "id": 3,
                    "coordinates": {"x": 1, "y": 1},
                    "category": "SHORE"
                },
                {
                    "id": 4,
                    "coordinates": {"x": 0, "y": 1},
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
    @Test
    fun `test tiles with same ids`(){
        val jsonData = """
        {
            "tiles": [
                {
                    "id": 1,
                    "coordinates": {"x": 0, "y": 0},
                    "category": "DEEP_OCEAN",
                    "current": false
                },
                {
                    "id": 1,
                    "coordinates": {"x": 1, "y": 1},
                    "category": "DEEP_OCEAN",
                    "current": false
                }
            ]
        }
    """.trimIndent()
        mapFile.writeText(jsonData)
        val result = mapParser.parseMap()
        assertFalse(result)
    }
    @Test
    fun `test tiles with same coordinates`(){
        val jsonData = """
        {
            "tiles": [
                {
                    "id": 1,
                    "coordinates": {"x": 0, "y": 0},
                    "category": "DEEP_OCEAN",
                    "current": false
                },
                {
                    "id": 1,
                    "coordinates": {"x": 0, "y": 0},
                    "category": "DEEP_OCEAN",
                    "current": false
                }
            ]
        }
    """.trimIndent()
        mapFile.writeText(jsonData)
        val result = mapParser.parseMap()
        assertFalse(result)
    }

    @Test
    fun `test parseMap with invalid tile schema`() {
        // Write JSON data with a tile that does not match the schema
        val jsonData = """
        {
            "tiles": [
                {
                    "id": 1,
                    "coordinates": {"x": 0, "y": 0}
                    // Missing category field which is required by the schema
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
    fun `deep ocean tile with invalid direction`() {
        val jsonData = """
        {
        "tiles": [ {
      "id": 36,
      "coordinates": {"x": 5, "y": 3},
      "category": "DEEP_OCEAN",
      "current": true,
      "direction": 75,
      "speed": 10,
      "intensity": 1
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
    fun `deep ocean tile with valid direction`() {
        val jsonData = """
        {
        "tiles": [ {
      "id": 1,
      "coordinates": {"x": 5, "y": 3},
      "category": "DEEP_OCEAN",
      "current": true,
      "direction": 0,
      "speed": 10,
      "intensity": 1
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
