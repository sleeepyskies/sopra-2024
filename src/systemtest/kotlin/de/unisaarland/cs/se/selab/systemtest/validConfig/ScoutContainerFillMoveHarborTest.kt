package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Scenario: Collecting ship receives a task, fulfills it and scouting ship
 * receives a container reward, fills it and then moves to a home harbor to unload.
 */
class ScoutContainerFillMoveHarborTest : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/corpWithTwoShips.json"
    override val scenario = "scenarioJsons/scoutContainerUnloadScenario.json"
    override val map = "mapFiles/obamna.json"
    override val name = "ScoutContainerFillMoveHarborTest"
    override val maxTicks = 3

    // STRING DUPLICATES OH MY LORD
    companion object {
        const val CORP_1_MOVE = "Corporation Action: Corporation 1 is starting to move its ships."
    }

    override suspend fun run() {
        initSimulation()

        tick0()

        tick1()

        tick2()
    }

    private suspend fun initSimulation() {
        assertNextLine("Initialization Info: obamna.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpWithTwoShips.json successfully parsed and validated.")
        assertNextLine("Initialization Info: scoutContainerUnloadScenario.json successfully parsed and validated.")
        assertNextLine("Simulation Info: Simulation started.")
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase
        assertNextLine(CORP_1_MOVE)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 1.")
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting Phase
        assertNextLine("Current Drift: Ship 1 drifted from tile 1 to tile 7.")

        // Tasks Phase
        assertNextLine("Task: Task 1 of type COLLECT with ship 2 is added with destination 14.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        assertNextLine(CORP_1_MOVE)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 1.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 14.")

        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Garbage Collection: Ship 2 collected 10 of garbage OIL with 70.")

        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting Phase
        assertNextLine("Current Drift: Ship 1 drifted from tile 1 to tile 7.")
        assertNextLine("Reward: Task 1: Ship 1 received reward of type CONTAINER.")
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corporation Phase
        assertNextLine(CORP_1_MOVE)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 1.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 14.")

        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Garbage Collection: Ship 2 collected 10 of garbage OIL with 70.")

        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting Phase
        assertNextLine("Current Drift: Ship 1 drifted from tile 1 to tile 7.")
        assertNextLine("Reward: Task 1: Ship 1 received reward of type CONTAINER.")
    }
}
