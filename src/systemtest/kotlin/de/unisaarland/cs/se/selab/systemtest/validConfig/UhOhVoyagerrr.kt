package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * UhOhVoyagerrr
 */
class UhOhVoyagerrr : ExampleSystemTestExtension() {
    override val name = "UhOhVoyagerrr"
    override val description = "Ship wants but cannot have :("

    override val map = "mapFiles/veryLongMap.json"
    override val corporations = "corporationJsons/UhOhVoyager_corporations.json"
    override val scenario = "scenarioJsons/UhOhVoyager_scenario.json"
    override val maxTicks = 9

    override suspend fun run() {
        skipLines(4)

        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        tick5()
        tick6()
        tick7()
        tick8()

        simEnd()
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 1.") // 2910 fuel left
        skipLines(4)
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 20 to tile 3.") // 2730 fuel left
        skipLines(4)
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 30 to tile 6.") // 2460 fuel left
        skipLines(4)
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 40 to tile 10.") // 2100 fuel left
        skipLines(4)
    }

    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 50 to tile 15.") // 1650 fuel left
        skipLines(4)
    }

    private suspend fun tick5() {
        assertNextLine("Simulation Info: Tick 5 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 50 to tile 10.") // 1200 fuel left
        skipLines(4)
    }

    private suspend fun tick6() {
        assertNextLine("Simulation Info: Tick 6 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 50 to tile 5.") // 750 fuel left
        skipLines(4)
    }

    private suspend fun tick7() {
        assertNextLine("Simulation Info: Tick 7 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 50 to tile 0.") // 300 fuel left
        skipLines(4)
    }

    private suspend fun tick8() {
        assertNextLine("Simulation Info: Tick 8 started.")
        skipLines(4)
        assertNextLine("Refueling: Ship 50 refueled at harbor 0.")
        skipLines(1)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 1000.")
        assertEnd()
    }
}
