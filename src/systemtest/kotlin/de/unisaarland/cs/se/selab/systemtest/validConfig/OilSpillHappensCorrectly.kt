package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError
import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs

/**
 * example system test
 */
class OilSpillHappensCorrectly : ExampleSystemTestExtension() {
    override val description = "applying oil spill"
    override val corporations = "corporationJsons/empty_corps.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/obamna.json"
    override val name = "OilSpillCorrect"
    override val maxTicks = 1
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
