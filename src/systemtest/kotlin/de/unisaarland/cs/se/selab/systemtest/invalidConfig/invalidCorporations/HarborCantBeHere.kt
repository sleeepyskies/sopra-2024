package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Testing a corporation having a harbor on a tile that is NOT a
 */
class HarborCantBeHere : ExampleSystemTestExtension() {
    override val description = "Current with 1000 speed"
    override val corporations = "corporationJsons/harborCantBeHere_corporations.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/obamna.json"
    override val name = "Harbor Cant Be Here"
    override val maxTicks = 0
    override suspend fun run() {
        assertNextLine("Initialization Info: obamna.json successfully parsed and validated.")
        assertNextLine("Initialization Info: harborCantBeHere_corporations.json is invalid.")
        assertEnd()
    }
}
