package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * WhySoMuchSpace
 */
class WhySoMuchSpace : ExampleSystemTestExtension() {
    override val name = "WhySoMuchSpace"
    override val description = "inventory hacks"

    override val map = "mapFiles/bigMap1.json"
    override val corporations = "corporationJsons/WhySoMuchSpace_corporations.json"
    override val scenario = "scenarioJsons/WhySoMuchSpace_scenario.json"
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

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 187.")
        assertNextLine("Ship Movement: Ship 55 moved with speed 10 to tile 187.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 1000 of garbage PLASTIC with 0.")
        skipLines(3)
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 188.")
        assertNextLine("Ship Movement: Ship 55 moved with speed 10 to tile 188.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 1000 of garbage PLASTIC with 1.")
        skipLines(3)
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 189.")
        assertNextLine("Ship Movement: Ship 55 moved with speed 10 to tile 189.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 1000 of garbage PLASTIC with 2.")
        skipLines(3)
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 190.")
        assertNextLine("Ship Movement: Ship 55 moved with speed 10 to tile 190.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 1000 of garbage PLASTIC with 3.")
        skipLines(3)
    }

    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 164.")
        assertNextLine("Ship Movement: Ship 55 moved with speed 10 to tile 164.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 1000 of garbage PLASTIC with 6.")
        skipLines(3)
    }

    private suspend fun tick5() {
        assertNextLine("Simulation Info: Tick 5 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 189.")
        assertNextLine("Ship Movement: Ship 55 moved with speed 10 to tile 163.")
        skipLines(4)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 5000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 5000.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 4000.")
        assertEnd()
    }
}
