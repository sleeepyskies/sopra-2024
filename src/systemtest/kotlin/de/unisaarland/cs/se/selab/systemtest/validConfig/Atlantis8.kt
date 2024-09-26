package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**gh**/
class Atlantis8 : ExampleSystemTestExtension() {
    override val description = "land tile next to shallow ocean"
    override val corporations = "corporationJsons/MultiReward_corp.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/atlantis8Map.json"
    override val name = "Atlantis8"
    override val maxTicks = 9

    override suspend fun run() {
        assertNextLine("Initialization Info: atlantis8Map.json is invalid.")
        assertEnd()
    }
}
