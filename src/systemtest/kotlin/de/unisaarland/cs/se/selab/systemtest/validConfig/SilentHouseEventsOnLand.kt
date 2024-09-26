package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**sdfg**/
class SilentHouseEventsOnLand : ExampleSystemTestExtension() {
    override val description = "events happening on land tile"
    override val corporations = "corporationJsons/corpOnSmallMapSimple.json"
    override val scenario = "scenarioJsons/eventsOnLand.json"
    override val map = "mapFiles/smallMapWithLandTile.json"
    override val name = "SilentHouseEventsOnLand"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMapWithLandTile.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpOnSmallMapSimple.json successfully parsed and validated.")
        assertNextLine("Initialization Info: eventsOnLand.json is invalid.")
        assertEnd()
    }
}
