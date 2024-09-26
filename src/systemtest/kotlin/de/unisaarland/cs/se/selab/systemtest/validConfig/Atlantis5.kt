package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**testing atlantis mutant*/
class Atlantis5 : ExampleSystemTestExtension() {
    override val description = "tile ditribution is invalid"
    override val corporations = "corporationJsons/MultiReward_corp.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/atlantis5Map.json"
    override val name = "Atlantis5"
    override val maxTicks = 9

    override suspend fun run() {
        assertNextLine("Initialization Info: atlantis5Map.json is invalid.")
        assertEnd()
    }
}
