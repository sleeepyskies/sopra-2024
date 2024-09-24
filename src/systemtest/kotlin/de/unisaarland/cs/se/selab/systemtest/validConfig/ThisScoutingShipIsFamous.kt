package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * ThisScoutingShipIsFamous, one scouting ship and 2 collecting ships.
 */
class ThisScoutingShipIsFamous : ExampleSystemTestExtension() {
    override val corporations: String = "corporationJsons/ThisScoutingShipIsFamous_corporation.json"
    override val description: String = "ThisScoutingShipIsFamous, one scouting ship and 2 collecting ships."
    override val map: String = "mapFiles/map_medium_01.json"
    override val maxTicks: Int = 4
    override val name: String = "ThisScoutingShipIsFamous"
    override val scenario: String = "scenarioJsons/ThisScoutingShipIsFamous_scenario.json"

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
        skipLines(1)

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 100 moved with speed 10 to tile 79.")
        assertNextLine("Ship Movement: Ship 101 moved with speed 10 to tile 78.")
        assertNextLine("Ship Movement: Ship 102 moved with speed 10 to tile 58.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 101 collected 10 of garbage OIL with 70.")
        assertNextLine("Garbage Collection: Ship 102 collected 10 of garbage OIL with 71.")
        skipLines(3)
    }

    private suspend fun tick1() {
        skipLines(1)

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 100 moved with speed 10 to tile 68.")
        skipLines(1)
        skipLines(3)
    }

    private suspend fun tick2() {
        skipLines(1)

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 100 moved with speed 10 to tile 58.")
        skipLines(1)
        skipLines(3)
    }

    private suspend fun tick3() {
        skipLines(1)

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 100 moved with speed 10 to tile 47.")
        assertNextLine("Ship Movement: Ship 101 moved with speed 10 to tile 67.")
        skipLines(1)
        skipLines(3)

        // Ship Drift
        assertNextLine("Current Drift: Ship 100 drifted from tile 47 to tile 37.")
        assertNextLine("Current Drift: Ship 101 drifted from tile 67 to tile 68.")
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 100 collected 20 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 20.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 10.")
        assertEnd()
    }
}
