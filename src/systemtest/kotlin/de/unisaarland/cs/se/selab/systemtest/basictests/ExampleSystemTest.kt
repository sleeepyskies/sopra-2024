package de.unisaarland.cs.se.selab.systemtest.basictests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError
import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs

/**
 * example system test
 */
class ExampleSystemTest : ExampleSystemTestExtension() {
    override val description = "tests statistics after 0 ticks"
    override val corporations = "corporationJsons/corporations.json"
    override val scenario = "scenarioJsons/scenario.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "ExampleTest"
    override val maxTicks = 0
    override suspend fun run() {
        val expectedString = "Simulation Statistics: Corporation 1 collected 0 of garbage."
        if (skipUntilLogType(Logs.SIMULATION_STATISTICS) != expectedString) {
            throw SystemTestAssertionError("Collected plastic should be 0!")
        }
        assertNextLine("Simulation Statistics: Corporation 2 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        skipLines(2)
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 1000.")
    }
}
