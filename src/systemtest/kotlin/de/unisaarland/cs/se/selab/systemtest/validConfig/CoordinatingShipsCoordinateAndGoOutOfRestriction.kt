package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError
import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs

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
        val expectedString = "Event: Event 1 of type PIRATE_ATTACK happened."
    }
    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase 1
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 15 to tile 7.")
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation 1 cooperated with corporation 2 with ship 1 to ship 2.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Corporation Phase 2
        assertNextLine("Corporation Action: Corporation 2 is starting to move its ships.")
        assertNextLine("Corporation Action: Corporation 2 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 2 is starting to cooperate with other corporations.")
        assertNextLine("Corporation 2 cooperated with corporation 1 with ship 2 to ship 1.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        skipLines(10) // No Ships move

        assertNextLine("Event: Event 1 of type RESTRICTION happened.")
    }
}
