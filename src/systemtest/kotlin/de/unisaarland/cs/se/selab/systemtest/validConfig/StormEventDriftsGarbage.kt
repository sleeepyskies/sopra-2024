package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Tests if garbage is drifted correctly (but now for another case)
 */
class StormEventDriftsGarbage : ExampleSystemTestExtension() {
    override val description = "tests if storm event is applied correctly"
    override val corporations = "corporationJsons/scoutingTheStorm.json"
    override val scenario = "scenarioJsons/garbageAndStorm.json"
    override val map = "mapFiles/map_medium_01.json"
    override val name = "test"
    override val maxTicks = 6
    override suspend fun run() {
        initSimulation()
        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        tick5()
        simEnd()
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 13.")
        skipLines(4)
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 2 moved with speed 20 to tile 15.")
        skipLines(4)
        assertNextLine("Event: Event 1 of type STORM happened.")
    }

    private suspend fun tick2() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 2 moved with speed 30 to tile 37.")
        skipLines(4)
        assertNextLine("Current Drift: Ship 2 drifted from tile 37 to tile 36.")
    }

    private suspend fun tick3() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 2 moved with speed 40 to tile 68.")
        skipLines(4)
    }

    private suspend fun tick4() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 13.")
        skipLines(4)
    }

    private suspend fun tick5() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 14.")
        skipLines(4)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 3250.")
        assertEnd()
    }
}
