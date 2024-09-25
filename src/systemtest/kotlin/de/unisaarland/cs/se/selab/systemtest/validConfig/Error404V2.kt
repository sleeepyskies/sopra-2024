package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/** this is a test **/
class Error404V2 : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/corpWithNonExistingShip.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "NoWayToLeaveRestriction: task should be assigned even if ship capacity is full"
    override val maxTicks = 5

    override suspend fun run() {
        skipLines(1)
        assertNextLine("Initialization Info: corpWithNonExistingShip.json is invalid.")
    }
}
