package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Test
 */
class ShipHasNoPathToVisibleGarbage : ExampleSystemTestExtension() {
    override val description = "Scouting ship can see garbage, but collecting ship" +
        " should not move towards it since it has no direct path to the garbage."
    override val corporations = "corporationJsons/shipHasNoPathToVisibleGarbage_corporations.json"
    override val scenario = "scenarioJsons/shipHasNoPathToVisibleGarbage_scenario.json"
    override val map = "mapFiles/obamna.json"
    override val name = "task could not be assigned, not enough fuel"
    override val maxTicks = 4

    override suspend fun run() {
        initSimulation()

        tick0()

        tick1()

        tick2()

        tick3()

        simEnd()
    }
    private suspend fun initSimulation() {
        skipLines(4)
    }
    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 55 moved with speed 25 to tile 8.")

        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")
    }

    private suspend fun tick1() {
        skipLines(6)
    }

    private suspend fun tick2() {
        skipLines(6)
    }

    private suspend fun tick3() {
        skipLines(6)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 100.")
        assertEnd()
    }
}
