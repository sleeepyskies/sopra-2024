package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError
import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs

/** tests if we receive reward regardless of task completion */
class FreeGiftTest : ExampleSystemTestExtension() {
    override val corporations = "corporationJsons/rewardAndFunctionCorrectCorps.json"
    override val description = "tests if we receive reward regardless of task completion"
    override val map = "mapFiles/smallMap1.json"
    override val name = "FreeGiftTest"
    override val scenario = "scenarioJsons/freeGiftScen.json"
    override val maxTicks = 10

    override suspend fun run() {
        skipLines(17)
        assertNextLine("Task: Task 1 of type COOPERATE with ship 2 is added with destination 9.")
        assertNextLine("Task: Task 2 of type FIND with ship 5 is added with destination 35.")
        skipLines(18)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 9.")
        skipLines(57)
        assertNextLine("Corporation Action: Corporation 2 finished its actions.")
        var expectedString = "Simulation Info: Tick 6 started."
        if (skipUntilLogType(Logs.TICK) != expectedString) {
            throw SystemTestAssertionError("shouldnt receive reward")
        }
        expectedString = "Simulation Statistics: Corporation 1 collected 0 of garbage."
        if (skipUntilLogType(Logs.SIMULATION_STATISTICS) != expectedString) {
            throw SystemTestAssertionError("Collected plastic should be 0!")
        }
        assertNextLine("Simulation Statistics: Corporation 2 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        skipLines(2)
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
    }
}
