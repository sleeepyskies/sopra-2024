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
class CorporationParserTests2 {
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
    fun `test corporations with the given json`() {
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
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertTrue(result)
    }

    @Test
    fun `test corporations with coordinating ship`() {
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
                  "name": "Coordinator",
                  "type": "COORDINATING",
                  "corporation": 1,
                  "location": 7,
                  "maxVelocity": 50,
                  "acceleration": 15,
                  "fuelCapacity": 3000,
                  "fuelConsumption": 7,
                  "visibilityRange": 1
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
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertTrue(result)
    }

    @Test
    fun `test ships have same ids`() {
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
    fun `test corporations must have unique names`() {
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
                  "id": 2,
                  "name": "Ocean Scouters Inc.",
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
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test corporations must have names`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "",
                  "ships": [1],
                  "homeHarbors": [7, 9],
                  "garbageTypes": []
                },
                {
                  "id": 2,
                  "name": "Ocean Scouters Inc.",
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
                }
              ]
            }
        """.trimIndent()
        corporationFile.writeText(jsonData)
        val result = corporationParser.parseAllCorporations()
        assertFalse(result)
    }

    @Test
    fun `test corporations with valid self made json`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "EcoCorp",
                  "ships": [1],
                  "homeHarbors": [3, 4],
                  "garbageTypes": ["PLASTIC"]
                }
              ],
              "ships": [
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
        assertTrue(result)
    }

    @Test
    fun `test corporations with no collecting ship`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "EcoCorp",
                  "ships": [1],
                  "homeHarbors": [3, 4],
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
    fun `test corporations with not enough collecting ships for all garbage types`() {
        val jsonData = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [1, 2],
                  "homeHarbors": [7, 9],
                  "garbageTypes": ["OIL", "PLASTIC"]
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
}
