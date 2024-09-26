package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**lol**/
class SilentHouseEventOnNonExistingTile : ExampleSystemTestExtension() {
    override val description = "Silent house event on non-existing tile"
    override val corporations = "corporationJsons/MultiReward_corp.json"
    override val scenario = "scenarioJsons/SilentHouseEventOnNonExistingTile_scen.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "SilentHouseEventOnNonExistingTile"
    override val maxTicks = 9

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: MultiReward_corp.json successfully parsed and validated.")
        assertNextLine("Initialization Info: SilentHouseEventOnNonExistingTile_scen.json is invalid.")
        assertEnd()
    }
}
