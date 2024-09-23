package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * applying pirate attack and checking if ship was removed
 */
class OnlyCorrectNumberOfShipsAssigned : ExampleSystemTestExtension() {
    override val description = "tests if only the correct number of ships is assigned" +
        "to a garbage "
    override val corporations = "corporationJsons/corporationsWithThreeCollectingShip.json"
    override val scenario = "scenarioJsons/garbageWhichNeedsAtLeast3Ships.json"
    override val map = "mapFiles/obamna.json"
    override val name = "OnlyCorrectNumberOfShipsAssigned"
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

        // Corporation Phase 1
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 7.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 7.")
        assertNextLine("Ship Movement: Ship 4 moved with speed 25 to tile 11.")
        skipLines(4)
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 11.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 11.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 1 collected 1000 of garbage CHEMICALS with 1.")
        assertNextLine("Garbage Collection: Ship 2 collected 1000 of garbage CHEMICALS with 1.")
        skipLines(3)
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 7.")
        assertNextLine("Ship Movement: Ship 4 moved with speed 25 to tile 1.")
        skipLines(4)
        assertNextLine("Current Drift: Ship 4 drifted from tile 1 to tile 7.")
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 2.")
        assertNextLine("Ship Movement: Ship 4 moved with speed 50 to tile 5.")
        skipLines(4)
    }
    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 2000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 2000.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
