package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * OnlyLandTileCorporation
 */
class OnlyLandTileCorporation : ExampleSystemTestExtension() {
    override val description = "Map with only a DEEP_OCEAN tile, we assert the map is valid."
    override val corporations = "corporationJsons/OnlyOneTile_corporation.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/validationFiles/invalidFiles/OnlyLandTile.json"

    override val name = "OnlyLandTileCorporation"
    override val maxTicks = 0

    override suspend fun run() {
        assertNextLine("Initialization Info: OnlyLandTile.json successfully parsed and validated.")
        assertNextLine("Initialization Info: OnlyOneTile_corporation.json is invalid.")
        assertEnd()
    }
}
