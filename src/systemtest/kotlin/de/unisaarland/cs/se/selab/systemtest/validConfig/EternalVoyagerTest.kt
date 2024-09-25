package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**testing indirect info sharing*/
class EternalVoyagerTest : ExampleSystemTestExtension() {
    override val description = "testing whether ship runs out of fuel"
    override val corporations = "corporationJsons/corpWithOneFasterScoutingShip.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/bigMap1.json"
    override val name = "marathonRunner"
    override val maxTicks = 18
    private val shipMove123 = "Ship Movement: Ship 1 moved with speed 20 to tile 123."
    private val shipDrift149 = "Current Drift: Ship 1 drifted from tile 149 to tile 174."
    private val shipDrift = "Current Drift: Ship 1 drifted from tile 123 to tile 174."

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
        tick8()
        tick9()
        tick10()
        tick11()
        tick12()
        tick13()
        tick14()
        tick15()
        tick16()
        tick17()
        simEnd()
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 149.")
        skipLines(9)
        assertNextLine("Current Drift: Ship 1 drifted from tile 149 to tile 174.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick5() {
        assertNextLine("Simulation Info: Tick 5 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick6() {
        assertNextLine("Simulation Info: Tick 6 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick7() {
        assertNextLine("Simulation Info: Tick 7 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick8() {
        assertNextLine("Simulation Info: Tick 8 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick9() {
        assertNextLine("Simulation Info: Tick 9 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick10() {
        assertNextLine("Simulation Info: Tick 10 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick11() {
        assertNextLine("Simulation Info: Tick 11 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick12() {
        assertNextLine("Simulation Info: Tick 12 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick13() {
        assertNextLine("Simulation Info: Tick 13 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick14() {
        assertNextLine("Simulation Info: Tick 14 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine(shipMove123)
        skipLines(9)
        assertNextLine(shipDrift)
    }
    private suspend fun tick15() {
        assertNextLine("Simulation Info: Tick 15 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 20 to tile 149.")
        skipLines(9)
        assertNextLine(shipDrift149)
    }
    private suspend fun tick16() {
        assertNextLine("Simulation Info: Tick 16 started.")

        // Corporation Phase
        skipLines(1)
        skipLines(9)
    }
    private suspend fun tick17() {
        assertNextLine("Simulation Info: Tick 17 started.")

        // Corporation Phase
        skipLines(1)
        skipLines(9)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
