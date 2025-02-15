package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**gh**/
class SilentHouseTasksOnLand : ExampleSystemTestExtension() {
    override val description = "Tasks are on land tile"
    override val corporations = "corporationJsons/corpOnSmallMapSimple.json"
    override val scenario = "scenarioJsons/tasksOnLand.json"
    override val map = "mapFiles/smallMapWithLandTile.json"
    override val name = "SilentHouseTasksOnLand"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMapWithLandTile.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpOnSmallMapSimple.json successfully parsed and validated.")
        assertNextLine("Initialization Info: tasksOnLand.json is invalid.")
        assertEnd()
    }
}
