package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**asf**/
class SilentHouseTooMuchOilOnTile2 : ExampleSystemTestExtension() {
    override val description = "two piles of oil on a tile with amount 1000 both"
    override val corporations = "corporationJsons/corpOnSmallMapSimple.json"
    override val scenario = "scenarioJsons/tooMuchOilOnTile2.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "Silent House Too Much Oil On Tile 2"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpOnSmallMapSimple.json successfully parsed and validated.")
        assertNextLine("Initialization Info: tooMuchOilOnTile2.json is invalid.")
        assertEnd()
    }
}
