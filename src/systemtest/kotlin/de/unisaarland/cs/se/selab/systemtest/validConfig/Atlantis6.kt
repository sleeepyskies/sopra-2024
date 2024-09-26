package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**asd**/
class Atlantis6 : ExampleSystemTestExtension() {
    override val description = "land next to shallow ocean"
    override val corporations = "corporationJsons/MultiReward_corp.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/atlantis6Map.json"
    override val name = "Atlantis6"
    override val maxTicks = 9

    override suspend fun run() {
        assertNextLine("Initialization Info: atlantis6Map.json is invalid.")
        assertEnd()
    }
}
