package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**ja**/
class Error404V4 : ExampleSystemTestExtension() {
    override val description = "garbage types of corporation not collected by any ship"
    override val corporations = "corporationJsons/wrongGBTypes.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "Error404V4"
    override val maxTicks = 5

    override suspend fun run() {
        skipLines(1)
        assertNextLine("Initialization Info: wrongGBTypes.json is invalid.")
        assertEnd()
    }
}
