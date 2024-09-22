package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError
import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs

/**
 * example system test
 */
class OilSpillHappensCorrectly : ExampleSystemTestExtension() {
    override val description = "applying oil spill"
    override val corporations = "corporationJsons/scoutingShipForOilSpillCollect.json"
    override val scenario = "scenarioJsons/justOilSpill.json"
    override val map = "mapFiles/obamna.json"
    override val name = "OilSpillCorrect"
    override val maxTicks = 5
    override suspend fun run() {
        /*initSimulation()
        tick0()
        tick1()
        tick2()
        simEnd()*/
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase 1
        skipLines(5)
        assertNextLine("Current Drift: OIL 75 with amount 50 drifted from tile 1 to tile 1.")
        assertNextLine("Current Drift: OIL 76 with amount 150 drifted from tile 6 to tile 8.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        skipLines(5) // No Ships move
        assertNextLine("Current Drift: OIL 77 with amount 50 drifted from tile 1 to tile 1.")
    }

    private suspend fun tick2() {
        skipLines(6)
    }
    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 2300.")
        assertEnd()
    }
}
