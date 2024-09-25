package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**sdf**/
class SilentHouseShipSpawsOnLand : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/corpWithShipOnLand.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/smallMapWithLandTile.json"
    override val name = "NoWayToLeaveRestriction: task should be assigned even if ship capacity is full"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMapWithLandTile.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpWithShipOnLand.json is invalid.")
        assertEnd()
    }
}
