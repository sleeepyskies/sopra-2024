package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**
 * Collecting ship and scouting ship dont go to garbage outside of visibility range
 **/
class CollecingShipDoesntGoToNonVisibleGarbage : ExampleSystemTestExtension() {
    override val description = "Collecting ship and scouting ship dont go to garbage outside of visibility range"
    override val corporations = "corporationJsons/collectingShipDontGoVisibleGarbage.json"
    override val scenario = "scenarioJsons/collectingShipDontGoVisibleGarbageScen.json"
    override val map = "mapFiles/cyclopsMap.json"
    override val name = "CollecingShipDoesntGoToNonVisibleGarbage"
    override val maxTicks = 3

    override suspend fun run() {
        skipLines(4)

        tick0()
        tick1()
        tick2()

        simEnd()
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 0.")
        skipLines(4)

        // Task Phase
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 2 moved with speed 20 to tile 2.")
        skipLines(4)
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 6.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 30 to tile 5.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 1 collected 1000 of garbage OIL with 1.")
        skipLines(3)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 1000.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
