package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * GarbageLandsOnShip
 */
class GarbageLandsOnShip : ExampleSystemTestExtension() {
    override val corporations: String = "corporationJsons/GarbageLandsOnShip_corporation.json"
    override val description: String = "GarbageLandsOnShip"
    override val map: String = "mapFiles/bigMap1.json"
    override val maxTicks: Int = 2
    override val name: String = "GarbageLandsOnShip"
    override val scenario: String = "scenarioJsons/GarbageLandsOnShip_scenario.json"

    override suspend fun run() {
        initSimulation()

        tick0()
        tick1()

        simEnd()
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }

    private suspend fun tick0() {
        skipLines(1)

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 101 moved with speed 10 to tile 457.")
        skipLines(5)

        // Event Phase
        assertNextLine("Event: Event 1 of type STORM happened.")
    }

    private suspend fun tick1() {
        skipLines(1)

        // Corporation Phase
        skipLines(1)
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 102 collected 100 of garbage OIL with 70.")
        skipLines(3)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 100 collected 100 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 100.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 100.")
        assertEnd()
    }
}
