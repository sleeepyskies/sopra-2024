package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidMap

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Testing a corporation having a harbor on a tile that is NOT a
 */
class NonUniqueTileIDs : ExampleSystemTestExtension() {
    override val description = "Current with 1000 speed"
    override val corporations = "corporationJsons/corpWithTwoShips.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/validationFiles/invalidFiles/obamnaNonUniqueTiles.json"
    override val name = "Harbor Cant Be Here"
    override val maxTicks = 0
    override suspend fun run() {
        assertNextLine("Initialization Info: obamna.json is invalid.")
        assertEnd()
    }
}
