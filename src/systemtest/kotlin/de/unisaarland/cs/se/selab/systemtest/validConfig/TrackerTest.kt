package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * AA
 */
class TrackerTest : ExampleSystemTestExtension() {
    override val name = "TrackerTest"
    override val description = "A corporation sends a scouting ship to the" +
        " location that an OIL_SPILL event happened at."
    override val map = "mapFiles/bigMap1.json"
    override val corporations = "corporationJsons/corpKnowsAboutSpills_corporation.json"
    override val scenario = "scenarioJsons/corpKnowsAboutSpills_scenario.json"
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

        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 15 to tile 188.")
        skipLines(4)

        // Task Phase
        assertNextLine("Task: Task 0 of type FIND with ship 50 is added with destination 111.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 30 to tile 111.")
        skipLines(4)

        // Task Phase
        assertNextLine("Reward: Task 0: Ship 50 received reward of type TRACKER.")
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        skipLines(1)
        assertNextLine("Corporation Action: Corporation 1 attached tracker to garbage 69 with ship 50.")
        skipLines(4)

        // Event Phase
        assertNextLine("Event: Event 500 of type STORM happened.")

        // Task Phase
        assertNextLine("Task: Task 1 of type FIND with ship 50 is added with destination 480.")
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")

        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 136.")
        assertNextLine("Ship Movement: Ship 51 moved with speed 15 to tile 113.")
        skipLines(4)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
