package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidScenario

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/** tests multiple task assignments for ships and corp harbors*/
class MultiTaskInvalid : ExampleSystemTestExtension() {
    override val description = "tests multiple task assignments for ships and corp harbors"
    override val corporations = "corporationJsons/rewardAndFunctionCorrectCorps.json"
    override val scenario = "scenarioJsons/multiTaskInvalid.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = " MultiTaskInvalid "
    override val maxTicks = 0

    override suspend fun run() {
        initSimulation()
    }
    private suspend fun initSimulation() {
        assertNextLine("Initialization Info: smallMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: rewardAndFunctionCorrectCorps.json successfully parsed and validated.")
        assertNextLine("Initialization Info: multiTaskInvalid.json is invalid.")
        assertEnd()
    }
}
