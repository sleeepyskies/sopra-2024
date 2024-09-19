package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidMap

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Testing a map with negative coordinates.
 */
class WrongNeighbors : ExampleSystemTestExtension() {
    override val description = "Simulation with wrong tiles next to each other."
    override val corporations = "corporationJsons/negativeCorporation.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/validationFiles/invalidFiles/obamnaNegative.json"

    // override val map = "mapFiles/obamna.json"
    override val name = "WrongNeighbors"
    override val maxTicks = 0

    override suspend fun run() {
        assertNextLine("Initialization Info: obamnaNegative.json is invalid.")
        assertEnd()
    }
}
