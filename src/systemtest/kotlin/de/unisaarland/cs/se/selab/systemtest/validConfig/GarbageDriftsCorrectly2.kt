package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Tests if garbage is drifted correctly (but now for another case)
 */
class GarbageDriftsCorrectly2 : ExampleSystemTestExtension() {
    override val description = "tests if ship is drifted correctly"
    override val corporations = "corporationJsons/corpWithOneCollectingShip.json"
    override val scenario = "scenarioJsons/oneGarbageForShipDrifting.json"
    override val map = "mapFiles/obamaButWeaker.json"
    override val name = "test"
    override val maxTicks = 7
    override suspend fun run() {
        initSimulation()
        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        tick5()
        tick6()
        simEnd()
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase 1
        skipLines(10)
        assertNextLine("Current Drift: Ship 1 drifted from tile 6 to tile 8.")
        assertNextLine("Current Drift: Ship 2 drifted from tile 6 to tile 8.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        skipLines(2)
        assertNextLine("Garbage Collection: Ship 2 collected 1000 of garbage OIL with 71.")

        skipLines(8) // No Ships move
        assertNextLine("Current Drift: Ship 3 drifted from tile 6 to tile 8.")
    }

    private suspend fun tick2() {
        skipLines(3)
        assertNextLine("Garbage Collection: Ship 1 collected 1000 of garbage PLASTIC with 70.")
        skipLines(5)
        assertNextLine("Garbage Collection: Ship 3 collected 1000 of garbage PLASTIC with 70.")
        skipLines(3)
    }

    private suspend fun tick3() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 3.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 3.")
        skipLines(4)
    }

    private suspend fun tick4() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 4.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 4.")
        skipLines(4)
    }

    private suspend fun tick5() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 5.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 5.")
        skipLines(4)
    }

    private suspend fun tick6() {
        skipLines(5)
        assertNextLine("Unload: Ship 1 unloaded 1000 of garbage PLASTIC at harbor 5.")
        skipLines(5)
        assertNextLine("Unload: Ship 3 unloaded 1000 of garbage PLASTIC at harbor 5.")
        skipLines(1)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 2000 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 2000.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 1000.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
