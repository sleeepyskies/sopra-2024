package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/** this is a test **/
class AtlantisTest2 : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/atlantis1Corp.json"
    override val scenario = "scenarioJsons/noWayOutOfRestriction.json"
    override val map = "mapFiles/atlantis2map.json"
    override val name = "AtlantisTest2"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: atlantis2map.json is invalid.")
        assertEnd()
    }
}
