package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidMap

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * OnlyShallowTileMap
 */
class OnlyShallowTileMap : ExampleSystemTestExtension() {
    override val description = "Map with only a SHALLOW_OCEAN tile, we assert the map is valid."
    override val corporations = "corporationJsons/OnlyOneTile_corporation.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/validationFiles/invalidFiles/OnlyShallowOcean.json"

    override val name = "OnlyShallowTileMap"
    override val maxTicks = 0

    override suspend fun run() {
        assertNextLine("Initialization Info: OnlyShallowOcean.json is invalid.")
        assertEnd()
    }
}
