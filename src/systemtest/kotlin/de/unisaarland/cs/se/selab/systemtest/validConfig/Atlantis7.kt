package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**asd**/
class Atlantis7 : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/MultiReward_corp.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/atlantis7Map.json"
    override val name = "Atlantis7"
    override val maxTicks = 9

    override suspend fun run() {
        assertNextLine("Initialization Info: atlantis7Map.json is invalid.")
        assertEnd()
    }
}
