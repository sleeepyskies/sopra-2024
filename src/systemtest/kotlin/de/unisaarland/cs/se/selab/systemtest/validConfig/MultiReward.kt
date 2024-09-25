package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**tesst**/
class MultiReward : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/MultiReward.json"
    override val scenario = "scenarioJsons/MultiReward.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "MultiReward"
    override val maxTicks = 9

    override suspend fun run() {
        skipLines(1)
    }
}
