package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Tests if garbage is drifted correctly (but now for another case)
 */
class GarbageDriftsCorrectly2 : ExampleSystemTestExtension() {
    override val description = "tests if garbage is drifted correctly"
    override val corporations = "corporationJsons/empty_corps.json"
    override val scenario = "scenarioJsons/fourGarbageForDrifting.json"
    override val map = "mapFiles/obamnaStrongerCurrent.json"
    override val name = "test"
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
        skipLines(4)
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase 1
        skipLines(5)
        assertNextLine("Current Drift: OIL 75 with amount 150 drifted from tile 6 to tile 8.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        skipLines(5) // No Ships move
        assertNextLine("Current Drift: OIL 76 with amount 150 drifted from tile 6 to tile 8.")
    }

    private suspend fun tick2() {
        skipLines(6)
        assertNextLine("Current Drift: OIL 77 with amount 150 drifted from tile 6 to tile 8.")
    }

    private suspend fun tick3() {
        skipLines(6)
        assertNextLine("Current Drift: OIL 72 with amount 50 drifted from tile 6 to tile 8.")
        assertNextLine("Current Drift: PLASTIC 78 with amount 100 drifted from tile 6 to tile 8.")
    }

    private suspend fun tick4() {
        skipLines(6)
        assertNextLine("Current Drift: PLASTIC 73 with amount 100 drifted from tile 6 to tile 8.")
        assertNextLine("Current Drift: OIL 79 with amount 50 drifted from tile 6 to tile 7.")
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 2200.")
        assertEnd()
    }
}
