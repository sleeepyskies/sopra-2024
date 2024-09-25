package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**testing indirect info sharing*/
class EternalVoyagerTest : ExampleSystemTestExtension() {
    override val description = "testing whether ship runs out of fuel"
    override val corporations = "corporationJsons/corpWithOneFasterScoutingShip.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/obamna.json"
    override val name = "marathonRunner"
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
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 15 to tile 2.")
        skipLines(4)
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 30 to tile 5.")
        skipLines(4)
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 45 to tile 1.")
        skipLines(4)
        assertNextLine("Current Drift: Ship 1 drifted from tile 1 to tile 7.")
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")
        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 60 to tile 5.")
        skipLines(4)
    }

    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")
        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 75 to tile 16.")
        skipLines(4)
    }

    private suspend fun tick5() {
        assertNextLine("Simulation Info: Tick 5 started.")
        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 90 to tile 5.")
        skipLines(4)
    }

    private suspend fun tick6() {
        assertNextLine("Simulation Info: Tick 6 started.")
        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 100 to tile 16.")
        skipLines(4)
    }

    private suspend fun tick7() {
        assertNextLine("Simulation Info: Tick 7 started.")
        skipLines(5)
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
