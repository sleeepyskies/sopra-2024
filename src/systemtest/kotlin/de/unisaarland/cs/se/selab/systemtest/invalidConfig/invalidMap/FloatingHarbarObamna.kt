package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidMap

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Simulates the FloatingHarbor test.
 */
class FloatingHarbarObamna : ExampleSystemTestExtension() {
    override val name: String = "FloatingHarbarObamna"
    override val description: String = "Non shore tile with harbor attribute."

    override val corporations: String = "corporationJsons/corporations.json"
    override val map: String = "mapFiles/validationFiles/invalidFiles/obamna_floating_harbor.json"
    override val scenario: String = "scenarioJsons/empty_scen.json"
    override val maxTicks: Int = 20

    override suspend fun run() {
        assertNextLine("Initialization Info: obamna_floating_harbor.json is invalid.")
        assertEnd()
    }
}
