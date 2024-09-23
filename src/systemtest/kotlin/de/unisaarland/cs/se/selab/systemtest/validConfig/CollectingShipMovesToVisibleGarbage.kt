package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * CollectingShipMovesToVisibleGarbage
 */
class CollectingShipMovesToVisibleGarbage : ExampleSystemTestExtension() {
    override val description = "Collecting ship moves to visibleGarbage of a scouting ship."
    override val corporations = "corporationJsons/collectingShipMovesToVisibleGarbage_corporations.json"
    override val scenario = "scenarioJsons/collectingShipMovesToVisibleGarbage_scenario.json"
    override val map = "mapFiles/obamna.json"
    override val name = "CollectingShipMovesToVisibleGarbage"
    override val maxTicks = 2

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
        assertNextLine("Simulation Info: Tick 0 started.")
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 51 moved with speed 10 to tile 12.")
        assertNextLine("Ship Movement: Ship 55 moved with speed 25 to tile 12.")
        assertNextLine("Ship Movement: Ship 59 moved with speed 10 to tile 17.")

        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 59 moved with speed 10 to tile 12.")

        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Garbage Collection: Ship 51 collected 600 of garbage PLASTIC with 100.")
        assertNextLine("Garbage Collection: Ship 51 collected 400 of garbage PLASTIC with 101.")
        assertNextLine("Garbage Collection: Ship 59 collected 200 of garbage PLASTIC with 101.")

        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 1200 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 1200.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
