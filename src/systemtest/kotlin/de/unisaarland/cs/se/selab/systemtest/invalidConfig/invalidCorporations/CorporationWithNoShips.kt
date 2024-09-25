package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Corporation that has no ships.
 */
class CorporationWithNoShips : ExampleSystemTestExtension() {
    override val description = "Corporation that has no ships."
    override val corporations = "corporationJsons/CorporationWithNoShips_corporations.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/obamna.json"
    override val name = "CorporationWithNoShips"
    override val maxTicks = 0

    override suspend fun run() {
        assertNextLine("Initialization Info: obamna.json successfully parsed and validated.")
        assertNextLine("Initialization Info: CorporationWithNoShips_corporations.json is invalid.")
    }
}
