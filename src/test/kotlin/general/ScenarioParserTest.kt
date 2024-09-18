package general

import de.unisaarland.cs.se.selab.assets.*
import de.unisaarland.cs.se.selab.parsing.ScenarioParser
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ScenarioParserTest {
    @TempDir
    lateinit var tempDir: File
    private lateinit var scenarioFile: File
    private lateinit var scenarioParser: ScenarioParser

    @BeforeEach
    fun setUp() {
        // Create a temporary map file
        scenarioFile = File(tempDir, "scenario.json")
        scenarioParser = ScenarioParser(scenarioFile.path, mapOf(1 to Pair(0, 0), 2 to Pair(1, 1)))
    }

    @Test
    fun `test parseEvents`() {
        val jsonData = """
            {
    "events": [
        {
            "type": "STORM",
            "id": 1,
            "tick": 1,
            "location": 1,
            "radius": 5,
            "speed": 10,
            "direction": 180
        }
    ],
    "garbage": [],
    "tasks": [],
    "rewards": []
}
        """.trimIndent()
        scenarioFile.writeText(jsonData)
        val result = scenarioParser.parseScenario()
        assertTrue(result)
        assertTrue(scenarioParser.events.isNotEmpty())
        assertTrue(
            scenarioParser.events[1] == mutableListOf(
                StormEvent(
                    1, 1, Pair(0, 0),
                    5, Direction.WEST, 10
                )
            )
        )
    }

    @Test
    fun `test parseEvents with multiple events`() {
        val jsonData = """
            {
    "events": [
        {
            "type": "STORM",
            "id": 1,
            "tick": 1,
            "location": 1,
            "radius": 5,
            "speed": 10,
            "direction": 180
        },
        {
            "type": "STORM",
            "id": 2,
            "tick": 1,
            "location": 2,
            "radius": 5,
            "speed": 10,
            "direction": 180
        }
    ],
    "garbage": [],
    "tasks": [],
    "rewards": []
}
        """.trimIndent()
        scenarioFile.writeText(jsonData)
        val result = scenarioParser.parseScenario()
        assertTrue(result)
        assertTrue(scenarioParser.events.isNotEmpty())
        assertTrue(
            scenarioParser.events[1] == mutableListOf(
                StormEvent(
                    1, 1, Pair(0, 0),
                    5, Direction.WEST, 10
                ),
                StormEvent(
                    2, 1, Pair(1, 1),
                    5, Direction.WEST, 10
                )
            )
        )
    }

    @Test
    fun `test parseEvents with multiple events on different ticks`() {
        val jsonData = """
            {
                "events": [
                    {
                        "type": "STORM",
                        "id": 1,
                        "tick": 1,
                        "location": 1,
                        "radius": 5,
                        "speed": 10,
                        "direction": 180
                    },
                    {
                        "type": "STORM",
                        "id": 2,
                        "tick": 2,
                        "location": 2,
                        "radius": 5,
                        "speed": 10,
                        "direction": 180
                    }
                ],
                "garbage": [],
                "tasks": [],
                "rewards": []
            }
        """.trimIndent()
        scenarioFile.writeText(jsonData)
        val result = scenarioParser.parseScenario()
        assertTrue(result)
        assertTrue(scenarioParser.events.isNotEmpty())
        assertTrue(
            scenarioParser.events[1] == mutableListOf(
                StormEvent(
                    1, 1, Pair(0, 0),
                    5, Direction.WEST, 10
                )
            )
        )
        assertTrue(
            scenarioParser.events[2] == mutableListOf(
                StormEvent(
                    2, 2, Pair(1, 1),
                    5, Direction.WEST, 10
                )
            )
        )
    }

    @Test
    fun `test parseGarbage`() {
        val jsonData = """
            {
                "events": [],
                "garbage": [
                    {
                        "id": 1,
                        "type": "PLASTIC",
                        "location": 1,
                        "amount": 1
                    }
                ],
                "tasks": [],
                "rewards": []
            }
        """.trimIndent()
        scenarioFile.writeText(jsonData)
        val result = scenarioParser.parseScenario()
        assertTrue(result)
        assertTrue(scenarioParser.garbage.isNotEmpty())
        assertTrue(
            scenarioParser.garbage[0] == Garbage(
                1, 1, GarbageType.PLASTIC, 1, Pair(0, 0), 0, mutableListOf<Int>()
            )
        )
    }

    @Test
    fun `test parseTasks`() {
        val jsonData = """
            {
                "events": [],
                "garbage": [],
                "tasks": [
                    {
                        "id": 5,
                        "type": "EXPLORE",
                        "tick": 1,
                        "shipID": 1,
                        "targetTile": 1,
                        "rewardID": 1,
                        "rewardShipID": 1
                    }
                ],
                "rewards": []
            }
        """.trimIndent()
        scenarioFile.writeText(jsonData)
        val result = scenarioParser.parseScenario()
        assertTrue(result)
    }
}
