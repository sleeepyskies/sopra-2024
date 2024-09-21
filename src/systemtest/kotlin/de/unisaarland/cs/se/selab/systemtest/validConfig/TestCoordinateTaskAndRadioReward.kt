package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Test aa
 */
class TestCoordinateTaskAndRadioReward : ExampleSystemTestExtension() {
    override val corporations = "corporationJsons/testCoordinateTaskAndRadioReward_corporations.json"
    override val scenario = "scenarioJsons/testCoordinateTaskAndRadioReward_scenario.json"
    override val description =
        "A ship is tasked to coordinate with a harbor, does so and grants another ship a radio."
    override val map = "mapFiles/obamna.json"
    override val maxTicks: Int = 5
    override val name: String = "TestCoordinateTaskAndRadioReward"

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
        skipLines(4)
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corp 1 phase
        skipLines(1)
        // assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")

        assertNextLine("Ship Movement: Ship 100 moved with speed 20 to tile 12.")
        assertNextLine("Ship Movement: Ship 101 moved with speed 20 to tile 12.")

        skipLines(4)
        /* assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")
        */

        // Corp 2 phase
        skipLines(5)
        /*
        assertNextLine("Corporation Action: Corporation 2 is starting to move its ships.")
        assertNextLine("Corporation Action: Corporation 2 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 2 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 2 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 2 finished its actions.")
        */

        // Task Phase
        assertNextLine("Task: Task 1 of type COOPERATE with ship 100 is added with destination 5.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corp 1 phase
        skipLines(1)
        // assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")

        assertNextLine("Ship Movement: Ship 100 moved with speed 20 to tile 3.")

        skipLines(4)
        /*
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")
         */

        // Corp 2 phase
        skipLines(5)
        /*
        assertNextLine("Corporation Action: Corporation 2 is starting to move its ships.")
        assertNextLine("Corporation Action: Corporation 2 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 2 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 2 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 2 finished its actions.")
         */
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corp 1 phase
        skipLines(1)
        // assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")

        assertNextLine("Ship Movement: Ship 100 moved with speed 40 to tile 5.")

        skipLines(4)
        /*
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")
         */

        // Corp 2 phase
        skipLines(5)
        /*
        assertNextLine("Corporation Action: Corporation 2 is starting to move its ships.")
        assertNextLine("Corporation Action: Corporation 2 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 2 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 2 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 2 finished its actions.")
         */
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")

        // Corp 1 phase
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Corp 2 phase
        assertNextLine("Corporation Action: Corporation 2 is starting to move its ships.")
        assertNextLine("Corporation Action: Corporation 2 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 2 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 2 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 2 finished its actions.")

        // Task Phase
        assertNextLine("Reward: Task 1: Ship 101 received reward of type RADIO.")
    }

    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")

        // Corp 1 phase
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 100 moved with speed 20 to tile 3.")
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Corp 2 phase
        assertNextLine("Corporation Action: Corporation 2 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 200 moved with speed 20 to tile 12.")
        assertNextLine("Corporation Action: Corporation 2 is starting to collect garbage.")
        assertNextLine("Garbage Collection: Ship 200 collected 100 of garbage PLASTIC with 50.")
        assertNextLine("Corporation Action: Corporation 2 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 2 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 2 finished its actions.")
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 100 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 100.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
