package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * applying pirate attack and checking if ship was removed
 */
class CoordinatingShipsCoordinateAndGoOutOfRestriction : ExampleSystemTestExtension() {
    override val description = "testing to see of ships coordinate coorectly, and go out of the restriction correctly" +
        "with correct accelerations"
    override val corporations = "corporationJsons/corpWithTwoCoordinatingShips.json"
    override val scenario = "scenarioJsons/restrictionAndOilSpillPlusCooperatingTask.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "test"
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

        // Corporation Phase 1
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 15 to tile 7.")
        skipLines(2)
        assertNextLine("Cooperation: Corporation 1 cooperated with corporation 2 with ship 1 to ship 2.")
        skipLines(2)

        // Corporation Phase 2
        skipLines(1)
        assertNextLine("Corporation Action: Corporation 2 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 2 is starting to cooperate with other corporations.")
        assertNextLine("Cooperation: Corporation 2 cooperated with corporation 1 with ship 2 to ship 1.")
        skipLines(2)
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 15 to tile 5.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 5.")
        skipLines(4)
        assertNextLine("Event: Event 1 of type RESTRICTION happened.")
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 30 to tile 6.")
        skipLines(1)
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        skipLines(3)
        assertNextLine("Ship Movement: Ship 2 moved with speed 20 to tile 6.")
        skipLines(4)
        assertNextLine("Task: Task 1 of type COORDINATE with ship 1 is added with destination 9.")
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 45 to tile 9.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 2 moved with speed 30 to tile 3.")
        skipLines(4)
        assertNextLine("Reward: Task 1: Ship 1 received reward of type RADIO.")
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
