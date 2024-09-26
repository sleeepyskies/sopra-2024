package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**asd**/
class Atlantis9 : ExampleSystemTestExtension() {
    override val description = "land tile next to deep ocean"
    override val corporations = "corporationJsons/MultiReward_corp.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/Atlantis9Map.json"
    override val name = "Atlantis9"
    override val maxTicks = 9

    override suspend fun run() {
        assertNextLine("Initialization Info: Atlantis9Map.json is invalid.")
        assertEnd()
    }
}
