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
    override val maxTicks = 2
    override suspend fun run() {
        val expectedString = "Simulation Statistics: Total amount of garbage still in the ocean: 7000."
        if (skipUntilLogType(Logs.TOTAL_AMOUNT_OF_GARBAGE) != expectedString) {
            throw SystemTestAssertionError("Uncollected oil should be 7000!")
        }
        assertEnd()
    }
}
