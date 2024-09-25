package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**test**/
class AtlantisTest1 : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/atlantis1Corp.json"
    override val scenario = "scenarioJsons/noWayOutOfRestriction.json"
    override val map = "mapFiles/atlantis1Map.json"
    override val name = "AtlantisTest1"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: atlantis1Map.json is invalid.")
    }
}
