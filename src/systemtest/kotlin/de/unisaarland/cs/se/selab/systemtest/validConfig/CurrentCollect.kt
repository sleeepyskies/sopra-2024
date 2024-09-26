package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Tests if garbage is collected correctly by a ship that moves by drifting
 */
class CurrentCollect : ExampleSystemTestExtension() {
    override val description = "CurrentCollect: tests if garbage that is drifted ontop of ship" +
        "is collected"
    override val corporations = "corporationJsons/currentCollectCorp.json"
    override val scenario = "scenarioJsons/currentCollectScen.json"
    override val map = "mapFiles/obamna.json"
    override val name = "CurrentCollect"
    override val maxTicks = 2

    override suspend fun run() {
        skipLines(4)
        tick0()
        tick1()
        skipLines(2)
        assertNextLine("Simulation Statistics: Corporation 1 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 1000.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        skipLines(5)
        assertNextLine("Current Drift: Ship 2 drifted from tile 1 to tile 7.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        skipLines(2)
        assertNextLine("Garbage Collection: Ship 2 collected 1000 of garbage OIL with 1.")
        skipLines(3)
    }
}
