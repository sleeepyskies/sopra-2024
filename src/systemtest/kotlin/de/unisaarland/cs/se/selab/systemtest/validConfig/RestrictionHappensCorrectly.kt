package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError
import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs
/**
 * restriction event system test
 */
class RestrictionHappensCorrectly : ExampleSystemTestExtension() {
    override val description = "applying oil spill"
    override val corporations = "corporationJsons/empty_corps.json"
    override val scenario = "scenarioJsons/justRestriction.json"
    override val map = "mapFiles/obamna.json"
    override val name = "OilSpillCorrect"
    override val maxTicks = 2
    override suspend fun run() {
        val exp = "Event: Event 1 of type RESTRICTION happened."
        if (skipUntilLogType(Logs.EVENT) != exp) {
            throw SystemTestAssertionError("Event 1 of type RESTRICTION should happen!")
        }
        val expectedString = "Simulation Statistics: Total amount of garbage still in the ocean: 0."
        if (skipUntilLogType(Logs.TOTAL_AMOUNT_OF_GARBAGE) != expectedString) {
            throw SystemTestAssertionError("Uncollected oil should be 0!")
        }
        assertEnd()
    }
}
