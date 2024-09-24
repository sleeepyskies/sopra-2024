package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/** tests if we receive reward regardless of task completion */
class FreeGiftTest : ExampleSystemTestExtension() {
    override val corporations = "corporationJsons/rewardAndFunctionCorrectCorps.json"
    override val description = "tests if we receive reward regardless of task completion"
    override val map = "mapFiles/smallMap1.json"
    override val name = "FreeGiftTest"
    override val scenario = "scenarioJsons/freeGiftScen.json"
    override val maxTicks = 10

    override suspend fun run() {
        skipLines(13)
        assertNextLine("Task: Task 1 of type COOPERATE with ship 2 is added with destination 9.")
        assertNextLine("Task: Task 2 of type FIND with ship 5 is added with destination 35.")
        skipLines(18)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 9.")

        initSimulation()
    }
    private suspend fun initSimulation() {
        assertNextLine("Initialization Info: smallMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: rewardAndFunctionCorrectCorps.json successfully parsed and validated.")
        assertNextLine("Initialization Info: freeGiftScen.json successfully parsed and validated.")
        assertEnd()
    }
}
