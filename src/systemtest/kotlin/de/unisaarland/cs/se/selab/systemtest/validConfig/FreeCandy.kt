package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/** xxx **/
class FreeCandy : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/twoCorpsOneReward.json"
    override val scenario = "scenarioJsons/twoCorpsOneRewardScen.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "two corps 1 reward"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: twoCorpsOneReward.json successfully parsed and validated.")
        assertNextLine("Initialization Info: twoCorpsOneRewardScen.json is invalid.")
        assertEnd()
    }
}
