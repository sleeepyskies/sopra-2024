package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidScenario

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * ThisShipAin'tHere.
 */
class ThisShipAintHere : ExampleSystemTestExtension() {
    override val name = "ThisShipAin'tHere"
    override val description = "Pirate attack on non existent ship."

    override val map = "mapFiles/bigMap1.json"
    override val corporations = "corporationJsons/testOilSpillIds_corporation.json"
    override val scenario = "scenarioJsons/ThisShipAintHere_scenario.json"
    override val maxTicks = 0

    override suspend fun run() {
        initSimulation()
    }
    private suspend fun initSimulation() {
        assertNextLine("Initialization Info: bigMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: testOilSpillIds_corporation.json successfully parsed and validated.")
        assertNextLine("Initialization Info: ThisShipAintHere_scenario.json is invalid.")
        assertEnd()
    }
}
