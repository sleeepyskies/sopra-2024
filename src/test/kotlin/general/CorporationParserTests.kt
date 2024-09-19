package general

import de.unisaarland.cs.se.selab.parsing.CorporationParser
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CorporationParserTests {
    @TempDir
    lateinit var tempDir: File
    private lateinit var corporationFile: File
    private lateinit var corporationParser: CorporationParser

    @BeforeEach
    fun setUp() {
        // Create a temporary map file
        corporationFile = File(tempDir, "corp.json")
        corporationParser = CorporationParser(
            corporationFile.path,
            mapOf(1 to Pair(0, 0), 2 to Pair(1, 1), 3 to Pair(2, 2), 4 to Pair(3, 3), 7 to Pair(4, 4), 9 to Pair(5, 5))
        )
    }

    @Test
    fun `test corporation owns ship with garbage type that it cannot collect`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [2],
                  "homeHarbors": [7, 9],
                  "garbageTypes": ["OIL"]
                }
              ],
              "ships": [
                {
                  "id": 2,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 1000,
                  "garbageType": "PLASTIC"
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test corporation owns more ships than provided`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [1, 2],
                  "homeHarbors": [7, 9],
                  "garbageTypes": ["PLASTIC"]
                }
              ],
              "ships": [
                {
                  "id": 2,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 1000,
                  "garbageType": "PLASTIC"
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test corporation owns one collecting ship with garbage type that it can collect`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [2],
                  "homeHarbors": [7, 9],
                  "garbageTypes": ["OIL"]
                }
              ],
              "ships": [
                {
                  "id": 2,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 50000,
                  "garbageType": "OIL"
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertTrue(result)
    }

    @Test
    fun `test corporation owns one collecting ship with garbage type that it can collect and one that it cannot`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [2, 3],
                  "homeHarbors": [7, 9],
                  "garbageTypes": ["OIL"]
                }
              ],
              "ships": [
                {
                  "id": 2,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 1000,
                  "garbageType": "OIL"
                },
                {
                  "id": 3,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 1000,
                  "garbageType": "PLASTIC"
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test ship doesnt belong to any corporation`() {
        val jsonData = """
            {"corporations": [{"id": 1, "name": "Ocean Scouters Inc.", "ships": [1], "homeHarbors": [7, 9],
                  "garbageTypes": []
                },
                {
                  "id": 2,
                  "name": "Ocean Cleaners Inc.",
                  "ships": [2],
                  "homeHarbors": [9],
                  "garbageTypes": ["PLASTIC"]
                }
              ],
              "ships": [
                {
                  "id": 1,
                  "name": "Explorer",
                  "type": "SCOUTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 100,
                  "acceleration": 25,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 10,
                  "visibilityRange": 5
                },
                {
                  "id": 2,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 2,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 1000,
                  "garbageType": "PLASTIC"
                },
                {
                  "id": 3,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 4,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 1000,
                  "garbageType": "PLASTIC"
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test corporation without ships`() {
        val jsonData = """
            {"corporations": [{"id": 1, "name": "Ocean Scouters Inc.", "ships": [1], "homeHarbors": [7, 9],
                  "garbageTypes": []
                },
                {
                  "id": 2,
                  "name": "Ocean Cleaners Inc.",
                  "ships": [2],
                  "homeHarbors": [9],
                  "garbageTypes": ["PLASTIC"]
                }
              ],
              "ships": [
                {
                  "id": 1,
                  "name": "Explorer",
                  "type": "SCOUTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 100,
                  "acceleration": 25,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 10,
                  "visibilityRange": 5
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `corporations have the same ids`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [1],
                  "homeHarbors": [7, 9],
                  "garbageTypes": []
                },
                {
                  "id": 1,
                  "name": "Ocean Cleaners Inc.",
                  "ships": [2],
                  "homeHarbors": [9],
                  "garbageTypes": ["PLASTIC"]
                }
              ],
              "ships": [
                {
                  "id": 1,
                  "name": "Explorer",
                  "type": "SCOUTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 100,
                  "acceleration": 25,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 10,
                  "visibilityRange": 5
                },
                {
                  "id": 2,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 1000,
                  "garbageType": "PLASTIC"
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test corporation has ships with the same ids`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [1],
                  "homeHarbors": [7, 9],
                  "garbageTypes": ["PLASTIC"]
                }
              ],
              "ships": [
                {
                  "id": 1,
                  "name": "Explorer",
                  "type": "SCOUTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 100,
                  "acceleration": 25,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 10,
                  "visibilityRange": 5
                },
                {
                  "id": 1,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 1000,
                  "garbageType": "PLASTIC"
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test scouting ships acceleration boundaries`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [1],
                  "homeHarbors": [7, 9],
                  "garbageTypes": []
                }
              ],
              "ships": [
                {
                  "id": 1,
                  "name": "Explorer",
                  "type": "SCOUTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 100,
                  "acceleration": 101,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 10,
                  "visibilityRange": 5
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test schema validation with negative ship id`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [1],
                  "homeHarbors": [7, 9],
                  "garbageTypes": []
                }
              ],
              "ships": [
                {
                  "id": -1,
                  "name": "Explorer",
                  "type": "SCOUTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 100,
                  "acceleration": 25,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 10,
                  "visibilityRange": 5
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test corporation owns a ship which doesnt belong to it`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [2],
                  "homeHarbors": [7, 9],
                  "garbageTypes": ["PLASTIC"]
                }
              ],
              "ships": [
                {
                  "id": 2,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 2,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 1000,
                  "garbageType": "PLASTIC"
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test corporation has no harbors`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [2],
                  "homeHarbors": [],
                  "garbageTypes": ["OIL"]
                }
              ],
              "ships": [
                {
                  "id": 2,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 50000,
                  "garbageType": "OIL"
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test corporation has harbors on non defined tiles`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [2],
                  "homeHarbors": [17],
                  "garbageTypes": ["OIL"]
                }
              ],
              "ships": [
                {
                  "id": 2,
                  "name": "Collecter",
                  "type": "COLLECTING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 10,
                  "acceleration": 10,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 5,
                  "capacity": 50000,
                  "garbageType": "OIL"
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }
    /**
     * ship that doesnt belong to any corp
     * corp without ships
     * test unique ids
     * scouting ships acceleration boundaries
     * constraints for ships
     * valid values for garbage properties (max amount a tile can hold)
     *
     *
     */
}
