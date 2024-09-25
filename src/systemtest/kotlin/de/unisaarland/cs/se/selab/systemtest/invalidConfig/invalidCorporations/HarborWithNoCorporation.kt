package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Harbor that has no corporation.
 */
class HarborWithNoCorporation : ExampleSystemTestExtension() {
    override val description = "Harbor that has no corporation."
    override val corporations = "corporationJsons/HarborWithNoCorporation_corporations.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/obamna.json"
    override val name = "HarborWithNoCorporation"
    override val maxTicks = 0

    override suspend fun run() {
        assertNextLine("Initialization Info: obamna.json successfully parsed and validated.")
        assertNextLine("Initialization Info: HarborWithNoCorporation_corporations.json is invalid.")
    }
}
