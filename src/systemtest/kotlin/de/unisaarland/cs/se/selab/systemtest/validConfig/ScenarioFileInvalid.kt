package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Scenario: Corporation file is invalid.
 */
class ScenarioFileInvalid : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/corpWithTwoShips.json"
    override val scenario = "scenarioJsons/inv_scenario_taskType_doesnt_match_reward.json"
    override val map = "mapFiles/obamna.json"
    override val name = "ScenarioFileInvalid"
    override val maxTicks = 0

    override suspend fun run() {
        initSimulation()
    }
    private suspend fun initSimulation() {
        assertNextLine("Initialization Info: obamna.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpWithTwoShips.json successfully parsed and validated.")
        assertNextLine("Initialization Info: inv_scenario_taskType_doesnt_match_reward.json is invalid.")
        assertEnd()
    }
}
