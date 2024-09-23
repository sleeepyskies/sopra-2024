package general

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.parsing.SimulationParser
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.PrintWriter

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimulationParserTests {
    private val mapJson = """
            {
              "tiles": [
                {"id": 1,"coordinates": {"x": 0,"y": 0},"category": "SHORE","harbor": false},
                {"id": 2,"coordinates": {"x": 0,"y": 1},"category": "SHORE","harbor": false},
                {"id": 3,"coordinates": {"x": 0,"y": 2},"category": "SHORE","harbor": false},
                {"id": 4,"coordinates": {"x": 0,"y": 3},"category": "SHORE","harbor": false},
                {"id": 5,"coordinates": {"x": 0,"y": 4},"category": "SHORE","harbor": false},
                {
                  "id": 6,
                  "coordinates": {
                    "x": 0,
                    "y": 5
                  },
                  "category": "SHORE",
                  "harbor": false
                },
                {
                  "id": 7,
                  "coordinates": {
                    "x": 1,
                    "y": 5
                  },
                  "category": "SHORE",
                  "harbor": true
                },
                {
                  "id": 8,
                  "coordinates": {
                    "x": 2,
                    "y": 5
                  },
                  "category": "SHORE",
                  "harbor": false
                },
                {
                  "id": 9,
                  "coordinates": {
                    "x": 3,
                    "y": 5
                  },
                  "category": "SHORE",
                  "harbor": true
                },
                {
                  "id": 10,
                  "coordinates": {
                    "x": 4,
                    "y": 1
                  },
                  "category": "SHORE",
                  "harbor": false
                },
                {
                  "id": 11,
                  "coordinates": {
                    "x": 1,
                    "y": 0
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 12,
                  "coordinates": {
                    "x": 1,
                    "y": 1
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 13,
                  "coordinates": {
                    "x": 1,
                    "y": 2
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 14,
                  "coordinates": {
                    "x": 1,
                    "y": 3
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 15,
                  "coordinates": {
                    "x": 1,
                    "y": 4
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 16,
                  "coordinates": {
                    "x": 2,
                    "y": 4
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 17,
                  "coordinates": {
                    "x": 3,
                    "y": 4
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 18,
                  "coordinates": {
                    "x": 4,
                    "y": 4
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 19,
                  "coordinates": {
                    "x": 5,
                    "y": 4
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 20,
                  "coordinates": {
                    "x": 4,
                    "y": 5
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 21,
                  "coordinates": {
                    "x": 5,
                    "y": 5
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 22,
                  "coordinates": {
                    "x": 3,
                    "y": 0
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 23,
                  "coordinates": {
                    "x": 3,
                    "y": 1
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 24,
                  "coordinates": {
                    "x": 3,
                    "y": 2
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 25,
                  "coordinates": {
                    "x": 4,
                    "y": 2
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 26,
                  "coordinates": {
                    "x": 5,
                    "y": 1
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 27,
                  "coordinates": {
                    "x": 4,
                    "y": 0
                  },
                  "category": "SHALLOW_OCEAN"
                },
                {
                  "id": 28,
                  "coordinates": {
                    "x": 2,
                    "y": 0
                  },
                  "category": "DEEP_OCEAN",
                  "current": false
                },
                {
                  "id": 29,
                  "coordinates": {
                    "x": 5,
                    "y": 0
                  },
                  "category": "DEEP_OCEAN",
                  "current": false
                },
                {
                  "id": 30,
                  "coordinates": {
                    "x": 2,
                    "y": 1
                  },
                  "category": "DEEP_OCEAN",
                  "current": false
                },
                {
                  "id": 31,
                  "coordinates": {
                    "x": 2,
                    "y": 2
                  },
                  "category": "DEEP_OCEAN",
                  "current": false
                },
                {
                  "id": 32,
                  "coordinates": {
                    "x": 2,
                    "y": 3
                  },
                  "category": "DEEP_OCEAN",
                  "current": false
                },
                {
                  "id": 33,
                  "coordinates": {
                    "x": 3,
                    "y": 3
                  },
                  "category": "DEEP_OCEAN",
                  "current": false
                },
                {
                  "id": 34,
                  "coordinates": {
                    "x": 4,
                    "y": 3
                  },
                  "category": "DEEP_OCEAN",
                  "current": false
                },
                {
                  "id": 35,
                  "coordinates": {
                    "x": 5,
                    "y": 3
                  },
                  "category": "DEEP_OCEAN",
                  "current": false
                },
                {
                  "id": 36,
                  "coordinates": {
                    "x": 5,
                    "y": 2
                  },
                  "category": "DEEP_OCEAN",
                  "current": false
                }
              ]
            }
    """.trimIndent()
    private val corporationJson = """
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
    private val corporationJson2 = """
            {
              "corporations": [
                {
                  "id": 1,
                  "name": "Ocean Scouters Inc.",
                  "ships": [1],
                  "homeHarbors": [9],
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

    @BeforeEach
    fun init() {
        Logger.setOutput(PrintWriter(System.out, true))
        corporationFile = File(tempDir, "corp.json")
        mapFile = File(tempDir, "map.json")
        scenarioFile = File(tempDir, "scenario.json")
        simulationParser = SimulationParser(mapFile.path, corporationFile.path, scenarioFile.path, maxTick)
    }

    @TempDir
    lateinit var tempDir: File
    private lateinit var corporationFile: File
    private lateinit var mapFile: File
    private lateinit var scenarioFile: File
    private lateinit var simulationParser: SimulationParser
    private var maxTick = 5

    @Test
    fun `test valid files`() {
        val scenarioJson = """
            {
                "events": [{"type": "STORM","id": 1,"tick": 3,"location": 1,"radius": 0,"speed": 10,"direction": 180}],
                "garbage": [{id: 1, location: 35, type: "PLASTIC", amount: 1000}],
                "tasks": [{id: 1, type: "FIND", tick: 3, shipID: 1, targetTile: 29, rewardID: 1, rewardShipID: 1}],
                "rewards": [{id: 1, type: "TRACKER"}]
            }
        """.trimIndent()
        mapFile.writeText(mapJson)
        corporationFile.writeText(corporationJson)
        scenarioFile.writeText(scenarioJson)
        val result = simulationParser.createSimulator()
        assertNotNull(result)
    }

    @Test
    fun `test invalid files`() {
        val scenarioJson = """
            {
                "events": [{"type": "STORM","id": 1,"tick": 3,"location": 1,"radius": 0,"speed": 10,"direction": 180}],
                "garbage": [{id: 1, location: 5, type: "PLASTIC", amount: 1000}],
                "tasks": [{id: 1, type: "FIND", tick: 3, shipID: 1, targetTile: 29, rewardID: 1, rewardShipID: 1}],
                "rewards": [{id: 1, type: "RADIO"}]
            }
        """.trimIndent()
        mapFile.writeText(mapJson)
        corporationFile.writeText(corporationJson)
        scenarioFile.writeText(scenarioJson)
        val result = simulationParser.createSimulator()
        assertNull(result)
    }

    @Test
    fun `test garbage amount incorrect`() {
        val scenarioJson = """
            {
                "events": [{"type": "STORM","id": 1,"tick": 3,"location": 1,"radius": 0,"speed": 10,"direction": 180}],
                "garbage": [{id: 1, location: 35, type: "OIL", amount: 1000}],
                "tasks": [{id: 1, type: "FIND", tick: 3, shipID: 1, targetTile: 29, rewardID: 1, rewardShipID: 1}],
                "rewards": [{id: 1, type: "RADIO"}]
            }
        """.trimIndent()
        mapFile.writeText(mapJson)
        corporationFile.writeText(corporationJson)
        scenarioFile.writeText(scenarioJson)
        val result = simulationParser.createSimulator()
        assertNull(result)
    }

    @Test
    fun `test garbage on LAND`() {
        val scenarioJson = """
            {
                "events": [{"type": "STORM","id": 1,"tick": 3,"location": 1,"radius": 0,"speed": 10,"direction": 180}],
                "garbage": [{id: 1, location: 2, type: "OIL", amount: 1000}],
                "tasks": [{id: 1, type: "FIND", tick: 3, shipID: 1, targetTile: 29, rewardID: 1, rewardShipID: 1}],
                "rewards": [{id: 1, type: "RADIO"}]
            }
        """.trimIndent()
        mapFile.writeText(mapJson)
        corporationFile.writeText(corporationJson)
        scenarioFile.writeText(scenarioJson)
        val result = simulationParser.createSimulator()
        assertNull(result)
    }

    @Test
    fun `test chemical garbage on deepocean`() {
        val scenarioJson = """
            {
                "events": [{"type": "STORM","id": 1,"tick": 3,"location": 1,"radius": 0,"speed": 10,"direction": 180}],
                "garbage": [{id: 1, location: 29, type: "CHEMICALS", amount: 1000}],
                "tasks": [{id: 1, type: "FIND", tick: 3, shipID: 1, targetTile: 29, rewardID: 1, rewardShipID: 1}],
                "rewards": [{id: 1, type: "RADIO"}]
            }
        """.trimIndent()
        mapFile.writeText(mapJson)
        corporationFile.writeText(corporationJson)
        scenarioFile.writeText(scenarioJson)
        val result = simulationParser.createSimulator()
        assertNull(result)
    }

    @Test
    fun `test harbors dont belong to any corp`() {
        val scenarioJson = """
            {
                "events": [{"type": "STORM","id": 1,"tick": 3,"location": 1,"radius": 0,"speed": 10,"direction": 180}],
                "garbage": [{id: 1, location: 35, type: "PLASTIC", amount: 1000}],
                "tasks": [{id: 1, type: "FIND", tick: 3, shipID: 1, targetTile: 29, rewardID: 1, rewardShipID: 1}],
                "rewards": [{id: 1, type: "TRACKER"}]
            }
        """.trimIndent()
        mapFile.writeText(mapJson)
        corporationFile.writeText(corporationJson2)
        scenarioFile.writeText(scenarioJson)
        val result = simulationParser.createSimulator()
        assertNull(result)
    }

    @Test
    fun `test coordinate task target is not a harbor`() {
        val scenarioJson = """
            {
                "events": [{"type": "STORM","id": 1,"tick": 3,"location": 1,"radius": 0,"speed": 10,"direction": 180}],
                "garbage": [{id: 1, location: 35, type: "PLASTIC", amount: 1000}],
                "tasks": [{id: 1, type: "COOPERATE", tick: 3, shipID: 1, targetTile: 29, rewardID: 1, rewardShipID: 1}],
                "rewards": [{id: 1, type: "RADIO"}]
            }
        """.trimIndent()
        mapFile.writeText(mapJson)
        corporationFile.writeText(corporationJson)
        scenarioFile.writeText(scenarioJson)
        val result = simulationParser.createSimulator()
        assertNull(result)
    }
}
