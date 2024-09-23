package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * Test
 */
class ShipCannotCollectGarbageWithContainer : ExampleSystemTestExtension() {
    override val description = "A scouting ship with container cannot " +
        "collect a garbage on a tile due to garbage type of corp"
    override val corporations = "corporationJsons/shipCannotCollectGarbageWithContainer_corporations.json"
    override val scenario = "scenarioJsons/shipCannotCollectGarbageWithContainer_scenario.json"
    override val map = "mapFiles/obamna.json"
    override val name = "ShipCannotCollectGarbageWithContainer"
    override val maxTicks = 3

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
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 55 moved with speed 13 to tile 6.")
        skipLines(4)

        // Ship Drifting
        assertNextLine("Current Drift: Ship 55 drifted from tile 6 to tile 8.")

        // Task Phase
        assertNextLine("Task: Task 1 of type COLLECT with ship 51 is added with destination 14.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 51 moved with speed 10 to tile 14.")
        assertNextLine("Ship Movement: Ship 55 moved with speed 26 to tile 1.")

        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")

        assertNextLine("Garbage Collection: Ship 51 collected 100 of garbage PLASTIC with 20.")

        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting
        assertNextLine("Current Drift: Ship 55 drifted from tile 1 to tile 7.")

        // Event Phase
        assertNextLine("Event: Event 30 of type OIL_SPILL happened.")

        // Task Phase
        assertNextLine("Reward: Task 1: Ship 55 received reward of type CONTAINER.")
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 55 moved with speed 39 to tile 3.")
        skipLines(4)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 100 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 100.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 10.")
        assertEnd()
    }
}
