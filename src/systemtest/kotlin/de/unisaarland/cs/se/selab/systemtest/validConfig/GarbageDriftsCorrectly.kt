package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs

/**
 * Tests if garbage is drifted correctly
 */
class GarbageDriftsCorrectly : ExampleSystemTestExtension() {
    override val description = "tests if garbage is drifted correctly"
    override val corporations = "corporationJsons/empty_corps.json"
    override val scenario = "scenarioJsons/threeGarbageForDrifting.json"
    override val map = "mapFiles/obamnaStrongerCurrent.json"
    override val name = "test"
    override val maxTicks = 4
    override suspend fun run() {
        skipUntilLogType(Logs.TOTAL_AMOUNT_OF_GARBAGE)
    }
}
