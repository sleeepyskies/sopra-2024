package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**gh**/
class SilentHouseCoordinateNotOnHarbor : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/corpOnSmallMapSimple.json"
    override val scenario = "scenarioJsons/coordinateNotOnHarbor.json"
    override val map = "mapFiles/smallMapWithLandTile.json"
    override val name = "asdf"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMapWithLandTile.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpOnSmallMapSimple.json successfully parsed and validated.")
        assertNextLine("Initialization Info: coordinateNotOnHarbor.json is invalid.")
        assertEnd()
    }
}
