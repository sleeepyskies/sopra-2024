package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Tests if garbage is collected correctly by a ship that moves by drifting
 */
class CurrentCollect1 : ExampleSystemTestExtension() {
    override val description = "CurrentCollect1: tests if garbage that is drifted ontop of ship" +
        "is collected"
    override val corporations = "corporationJsons/currentCollectCorp1.json"
    override val scenario = "scenarioJsons/currentCollectScen1.json"
    override val map = "mapFiles/obamna.json"
    override val name = "CurrentCollect1"
    override val maxTicks = 3

    override suspend fun run() {
        skipLines(4)
        tick0()
        tick1()
        tick2()
        skipLines(2)
        assertNextLine("Simulation Statistics: Corporation 1 collected 100 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 100.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 900.")
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        skipLines(5)
        assertNextLine("Current Drift: OIL 2 with amount 50 drifted from tile 1 to tile 7.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        skipLines(2)
        assertNextLine("Garbage Collection: Ship 2 collected 50 of garbage OIL with 2.")
        skipLines(3)
        assertNextLine("Current Drift: OIL 3 with amount 50 drifted from tile 1 to tile 7.")
    }
    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        skipLines(2)
        assertNextLine("Garbage Collection: Ship 2 collected 50 of garbage OIL with 3.")
        skipLines(4)
    }
}
