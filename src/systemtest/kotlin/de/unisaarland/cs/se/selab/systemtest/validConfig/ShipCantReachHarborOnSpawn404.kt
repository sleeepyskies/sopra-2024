package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/** this is a test **/
class ShipCantReachHarborOnSpawn404 : ExampleSystemTestExtension() {
    override val description = "ship spawns too far away from the harbor."
    override val corporations = "corporationJsons/shipCantReachHarborOnSpawn.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/bigMap1.json"
    override val name = "ship spawn to far away from the harbor (404)"
    override val maxTicks = 9

    override suspend fun run() {
        assertNextLine("Initialization Info: bigMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: shipCantReachHarborOnSpawn.json is invalid.")
        assertEnd()
    }
}
