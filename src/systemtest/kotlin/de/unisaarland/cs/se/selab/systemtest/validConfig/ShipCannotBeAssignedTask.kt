package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Sys test.
 */
class ShipCannotBeAssignedTask : ExampleSystemTestExtension() {
    override val description = "A ship has a task scheduled, but cannot be assigned the task since it needs to refuel."
    override val corporations = "corporationJsons/shipCannotBeAssignedTask_corporation.json"
    override val scenario = "scenarioJsons/shipCannotBeAssignedTask_scenario.json"
    override val map = "mapFiles/obamna.json"
    override val name = "ShipCannotBeAssignedTask"
    override val maxTicks = 8 // was 9 before
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
        // tick8()
        simEnd()
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }

    private suspend fun tick0() {
        // ship has 3600 fuel
        // Tick start
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 25 to tile 3.") // 3400 fuel, tile 3
        skipLines(4)
    }

    private suspend fun tick1() {
        // Tick start
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 50 to tile 16.") // 3000 fuel, tile 16
        skipLines(4)
    }

    private suspend fun tick2() {
        // Tick start
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 75 to tile 5.") // 2400 fuel, tile 5
        skipLines(4)
    }

    private suspend fun tick3() {
        // Tick start
        assertNextLine("Simulation Info: Tick 3 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 100 to tile 16.") // 1800 fuel, tile 16
        skipLines(4)
    }

    private suspend fun tick4() {
        // Tick start
        assertNextLine("Simulation Info: Tick 4 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 100 to tile 5.") // 1200 fuel, tile 5
        skipLines(4)
    }

    private suspend fun tick5() {
        // Tick start
        assertNextLine("Simulation Info: Tick 5 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 100 to tile 16.") // 600 fuel, tile 16
        skipLines(4)
        // assertNextLine("Refueling: Ship 1 refueled at harbor 5.")

        // made it so that refuel logic is based on how much a ship can move, not how much it would move
    }

    private suspend fun tick6() {
        // ship has 0
        // Tick start
        assertNextLine("Simulation Info: Tick 6 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 100 to tile 5.") // 0 fuel, tile 5
        skipLines(4)
        assertNextLine("Task: Task 1 of type FIND with ship 1 is added with destination 3.")
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

    /*
private suspend fun tick8() {
    // ship has 0
    // Tick start
    assertNextLine("Simulation Info: Tick 8 started.")

    // Corporation Phase
    skipLines(1)
    assertNextLine("Ship Movement: Ship 1 moved with speed 25 to tile 3.")
    skipLines(4)
}

*/
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
