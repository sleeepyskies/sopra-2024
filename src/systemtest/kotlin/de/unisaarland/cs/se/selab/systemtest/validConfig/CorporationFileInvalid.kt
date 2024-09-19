package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Scenario: Corporation file is invalid.
 */
class CorporationFileInvalid : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/inv_Corporations_wrong_garb_types.json"
    override val scenario = "scenarioJsons/scoutContainerUnloadScenario.json"
    override val map = "mapFiles/obamna.json"
    override val name = "CorporationFileInvalid"
    override val maxTicks = 0

    override suspend fun run() {
        initSimulation()
    }
    private suspend fun initSimulation() {
        assertNextLine("Initialization Info: obamna.json successfully parsed and validated.")
        assertNextLine("Initialization Info: inv_Corporations_wrong_garb_types.json is invalid.")
        assertEnd()
    }
}
