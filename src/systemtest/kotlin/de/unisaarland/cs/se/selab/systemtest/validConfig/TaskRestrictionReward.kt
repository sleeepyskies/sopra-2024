package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/** Ship gets a task, then a restriction happens on the tile the ship is on,
 * ship moves out of the restriction, and continues towards the task.
 */

class TaskRestrictionReward : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/scoutingShip1Corp1.json"
    override val scenario = "scenarioJsons/TaskRestrictionReward.json"
    override val map = "mapFiles/obamna.json"
    override val name = "Checks if a task and restriction work correctly"
    override val maxTicks = 5

    override suspend fun run() {
        initSimulation()
        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        simEnd()
    }
    private suspend fun initSimulation() {
        /*
        assertNextLine("Initialization Info: obamna.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpWithTwoShips.json successfully parsed and validated.")
        assertNextLine("Initialization Info: scoutContainerUnloadScenario.json successfully parsed and validated.")
        assertNextLine("Simulation Info: Simulation started.")
        */
        skipLines(4)
    }
    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase
        skipLines(1)
        // assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 7.")
        skipLines(4)
        // assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        // assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        // assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        // assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting Phase
        // assertNextLine("Current Drift: Ship 1 drifted from tile 1 to tile 7.")

        // Tasks Phase
        assertNextLine("Task: Task 1 of type FIND with ship 1 is added with destination 3.")
    }
    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        skipLines(1)
        // assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 2.")
        skipLines(4)
        // assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        // assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        // assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        // assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting Phase
        // assertNextLine("Current Drift: Ship 1 drifted from tile 7 to tile 13.")

        // Events phase
        assertNextLine("Event: Event 1 of type RESTRICTION happened.")
    }
    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corporation Phase
        skipLines(1)
        // assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 1.")
        skipLines(4)
        // assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        // assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        // assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        // assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting Phase
        assertNextLine("Current Drift: Ship 1 drifted from tile 1 to tile 7.")
    }
    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")

        // Corporation Phase
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 8.")
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting Phase
    }
    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")

        // Corporation Phase
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 3.")
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Task Phase
        assertNextLine("Reward: Task 1: Ship 1 received reward of type TRACKING.")
    }
    private suspend fun simEnd() {
        skipLines(2)
        // assertNextLine("Simulation Info: Simulation ended.")
        // assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
