package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Sys test.
 */
class ShipCannotBeAssignedTask : ExampleSystemTestExtension() {
    override val description = "A ship has a task scheduled, but cannot be assigned the task since it needs to refuel.s"
    override val corporations = "corporationJsons/shipCannotBeAssignedTask_corporation.json"
    override val scenario = "scenarioJsons/shipCannotBeAssignedTask_scenario.json"
    override val map = "mapFiles/obamna.json"
    override val name = "ShipCannotBeAssignedTask"
    override val maxTicks = 8
    override suspend fun run() {
        initSimulation()
        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        tick5()
        tick6()
        tick7()
        simEnd()
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }

    private suspend fun tick0() {
        // ship has 3000 fuel
        // Tick start
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 25 to tile 3.")
        skipLines(4)
    }

    private suspend fun tick1() {
        // ship has 2800 fuel
        // Tick start
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 50 to tile 16.")
        skipLines(4)
    }

    private suspend fun tick2() {
        // ship has 2400 fuel
        // Tick start
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 75 to tile 5.")
        skipLines(4)
    }

    private suspend fun tick3() {
        // ship has 1800
        // Tick start
        assertNextLine("Simulation Info: Tick 3 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 100 to tile 16.")
        skipLines(4)
    }

    private suspend fun tick4() {
        // ship has 1200
        // Tick start
        assertNextLine("Simulation Info: Tick 4 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 100 to tile 5.")
        skipLines(4)
    }

    private suspend fun tick5() {
        // ship has 600
        // Tick start
        assertNextLine("Simulation Info: Tick 5 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 100 to tile 16.")
        skipLines(4)
    }

    private suspend fun tick6() {
        // ship has 0
        // Tick start
        assertNextLine("Simulation Info: Tick 6 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 100 to tile 5.")
        skipLines(4)
    }

    private suspend fun tick7() {
        // ship has 0
        // Tick start
        assertNextLine("Simulation Info: Tick 7 started.")

        // Corporation Phase
        skipLines(3)
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Refueling: Ship 1 refueled at harbor 5.")
        skipLines(1)
    }
    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
