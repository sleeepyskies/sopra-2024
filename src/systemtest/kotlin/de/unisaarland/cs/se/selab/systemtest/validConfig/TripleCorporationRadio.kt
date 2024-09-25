package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * A ship coordinates with another corporation, receives
 * a radio and then ends up coordinating with another
 * corporation, which makes one of their ships move.
 */
class TripleCorporationRadio : ExampleSystemTestExtension() {
    override val description = "A ship coordinates with another corporation, receives a" +
        " radio and then ends up coordinating with another corporation, which makes one of their ships move."
    override val corporations = "corporationJsons/TripleCorporationRadio_corporations.json"
    override val scenario = "scenarioJsons/TripleCorporationRadio_scenario.json"
    override val map = "mapFiles/bigMap1.json"
    override val name = "TripleCorporationRadio"
    override val maxTicks = 7

    override suspend fun run() {
        initSimulation()

        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        tick5()
        tick6()

        simEnd()
    }
    private suspend fun initSimulation() {
        skipLines(4)
    }
    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation 1
        skipLines(5)

        // Corporation 2
        skipLines(1)
        assertNextLine("Ship Movement: Ship 21 moved with speed 10 to tile 288.")
        assertNextLine("Ship Movement: Ship 23 moved with speed 10 to tile 346.")
        skipLines(4)

        // Corporation 3
        skipLines(5)

        // Task Phase
        assertNextLine("Task: Task 0 of type COOPERATE with ship 21 is added with destination 314.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation 1
        skipLines(5)

        // Corporation 2
        skipLines(1)
        assertNextLine("Ship Movement: Ship 21 moved with speed 10 to tile 289.")
        assertNextLine("Ship Movement: Ship 23 moved with speed 10 to tile 320.")
        skipLines(4)

        // Corporation 3
        skipLines(5)
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corporation 1
        skipLines(5)

        // Corporation 2
        skipLines(1)
        assertNextLine("Ship Movement: Ship 21 moved with speed 10 to tile 314.")
        assertNextLine("Ship Movement: Ship 23 moved with speed 10 to tile 295.")
        skipLines(4)

        // Corporation 3
        skipLines(5)
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")

        // Corporation 1
        skipLines(5)

        // Corporation 2
        skipLines(1)
        assertNextLine("Ship Movement: Ship 23 moved with speed 10 to tile 269.")
        skipLines(4)

        // Corporation 3
        skipLines(5)

        // Task Phase
        assertNextLine("Reward: Task 0: Ship 21 received reward of type RADIO.")
    }

    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")

        // Corporation 1
        skipLines(5)

        // Corporation 2
        skipLines(1)
        assertNextLine("Ship Movement: Ship 21 moved with speed 10 to tile 289.")
        assertNextLine("Ship Movement: Ship 23 moved with speed 10 to tile 244.")
        skipLines(2)
        assertNextLine("Cooperation: Corporation 2 cooperated with corporation 1 with ship 21 to ship 11.")
        skipLines(2)

        // Corporation 3
        skipLines(5)
    }

    private suspend fun tick5() {
        assertNextLine("Simulation Info: Tick 5 started.")

        // Corporation 1
        skipLines(5)

        // Corporation 2
        skipLines(1)
        assertNextLine("Ship Movement: Ship 21 moved with speed 10 to tile 263.")
        assertNextLine("Ship Movement: Ship 23 moved with speed 10 to tile 243.")
        skipLines(4)

        // Corporation 3
        skipLines(5)
    }

    private suspend fun tick6() {
        assertNextLine("Simulation Info: Tick 6 started.")

        // Corporation 1
        skipLines(5)

        // Corporation 2
        skipLines(1)
        assertNextLine("Ship Movement: Ship 21 moved with speed 10 to tile 238.")
        assertNextLine("Ship Movement: Ship 22 moved with speed 10 to tile 241.")
        assertNextLine("Ship Movement: Ship 23 moved with speed 10 to tile 242.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 22 collected 1000 of garbage OIL with 100.")
        skipLines(3)

        // Corporation 3
        skipLines(5)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 3 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 1000.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
