package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError
import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs
/**checks the reward functionalities*/
class RewardsGivenAndFunctionCorrectly : ExampleSystemTestExtension() {
    override val description = "tests statistics after 0 ticks"
    override val corporations = "corporationJsons/rewardAndFunctionCorrectCorps.json"
    override val scenario = "scenarioJsons/rewardAndFunctionCorrectScen.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "all rewards test"
    override val maxTicks = 15
    override suspend fun run() {
        val expRadioReward = "Task: Task 1 of type COOPERATE with ship 2 is added with destination 9."
        if (skipUntilLogType(Logs.TASK) != expRadioReward) {
            throw SystemTestAssertionError("Task 1 didnt add.")
        }
        val expRadioReward2 = "Task: Task 2 of type EXPLORE with ship 5 is added with destination 35."
        if (skipUntilLogType(Logs.TASK) != expRadioReward2) {
            throw SystemTestAssertionError("Task 2 didnt add.")
        }
        val expRadioReward3 = "Reward: Task 1: Ship 1 received reward of type RADIO."
        if (skipUntilLogType(Logs.REWARD) != expRadioReward3) {
            throw SystemTestAssertionError("Reward 1 didnt add.")
        }
        val task3 = "Task: Task 3 of type COLLECT with ship 2 is added with destination 35."
        if (skipUntilLogType(Logs.TASK) != task3) {
            throw SystemTestAssertionError("Task 3 didnt add.")
        }
        val expRadioReward4 = "Reward: Task 2: Ship 5 received reward of type TELESCOPE."
        if (skipUntilLogType(Logs.REWARD) != expRadioReward4) {
            throw SystemTestAssertionError("Reward 2 didnt add.")
        }
        val expRadioReward5 = "Reward: Task 3: Ship 2 received reward of type CONTAINER."
        if (skipUntilLogType(Logs.REWARD) != expRadioReward5) {
            throw SystemTestAssertionError("Reward 3 didnt add.")
        }
        val expectedString = "Simulation Statistics: Corporation 1 collected 4000 of garbage."
        if (skipUntilLogType(Logs.SIMULATION_STATISTICS) != expectedString) {
            throw SystemTestAssertionError("Collected plastic should be 0!")
        }
        assertNextLine("Simulation Statistics: Corporation 2 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 3000.")
        skipLines(2)
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
    }
}
