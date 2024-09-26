package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidMap

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * OnlyLandTileMap
 */
class OnlyLandTileMap : ExampleSystemTestExtension() {
    override val description = "Map with only a LAND tile, we assert the map is invalid."
    override val corporations = "corporationJsons/OnlyOneTile_corporation.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/validationFiles/invalidFiles/OnlyLandTile.json"

    override val name = "OnlyLandTileMap"
    override val maxTicks = 0

    override suspend fun run() {
        assertNextLine("Initialization Info: OnlyLandTile.json is invalid.")
        assertEnd()
    }
}
