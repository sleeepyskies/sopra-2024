package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

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

        skipLines(1)

        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 4.")
        skipLines(4)
        assertNextLine("Event: Event 1 of type OIL_SPILL happened.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 20 to tile 8.")
        skipLines(4)
    }

    private suspend fun tick2() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 30 to tile 21.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 4.")
        skipLines(4)
    }

    private suspend fun tick3() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 3.")
        skipLines(4)
    }

    private suspend fun tick4() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 8.")
        skipLines(4)
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
