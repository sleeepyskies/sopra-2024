package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * ada
 */
class ICanSeeWhatYouCannot : ExampleSystemTestExtension() {
    override val corporations: String = "corporationJsons/largeStormOil_corporation.json"
    override val description: String = "ICanSeeWhatYouCannot"
    override val map: String = "mapFiles/bigMap1.json"
    override val maxTicks: Int = 3
    override val name: String = "ICanSeeWhatYouCannot"
    override val scenario: String = "scenarioJsons/largeStormOil_scenario.json"

    override suspend fun run() {
        initSimulation()

        tick0()
        tick1()
        tick2()

        simEnd()
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }

    private suspend fun tick0() {
        skipLines(1)

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 101 moved with speed 10 to tile 263.")
        assertNextLine("Ship Movement: Ship 102 moved with speed 10 to tile 316.")
        skipLines(4)
    }

    private suspend fun tick1() {
        skipLines(1)

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 101 moved with speed 10 to tile 238.")
        assertNextLine("Ship Movement: Ship 102 moved with speed 10 to tile 291.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 102 collected 100 of garbage OIL with 69.")
        skipLines(3)
    }

    private suspend fun tick2() {
        skipLines(1)

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 101 moved with speed 10 to tile 263.")
        assertNextLine("Ship Movement: Ship 102 moved with speed 10 to tile 316.")
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
