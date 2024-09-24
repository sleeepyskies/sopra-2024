package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Tests corporation with 3 garbage types, but no ships for all types.
 */
class ThisCorporationIsMessedUPbruh : ExampleSystemTestExtension() {
    override val corporations: String = "corporationJsons/ThisCorporationIsMessedUPbruh_corporation.json"
    override val description: String = "Tests corporation with 3 garbage types, but no ships for all types."
    override val map: String = "mapFiles/bigMap1.json"
    override val maxTicks: Int = 0
    override val name: String = "ThisCorporationIsMessedUPbruh"
    override val scenario: String = "scenarioJsons/empty_scen.json"

    override suspend fun run() {
        skipLines(1)
        assertNextLine(
            "Initialization Info: ThisCorporationIsMessedUPbruh_corporation.json " +
                "is invalid."
        )
    }
}
