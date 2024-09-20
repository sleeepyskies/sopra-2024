package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/** Ship gets a task, then a restriction happens on the tile the ship is on,
 * ship moves out of the restriction, and continues towards the task.
 */
class NoWayToLeaveRestriction : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/noWayToLeaveRestrictionSHIPS.json"
    override val scenario = "scenarioJsons/noWayOutOfRestriction.json"
    override val map = "mapFiles/obamna.json"
    override val name = "task should be assigned even if ship capacity is full"
    override val maxTicks = 5
    private val corporationStartedToMove = "Corporation Action: Corporation 1 is starting to move its ships."
    private val corporationStartedCollecting = "Corporation Action: Corporation 1 is starting to collect garbage."
    private val corporationStartedCooperating = "Corporation Action: Corporation" +
        " 1 is starting to cooperate with other corporations."
    private val corporationStartedRefueling = "Corporation Action: Corporation 1 is starting to refuel."
    private val corporationFinishedActions = "Corporation Action: Corporation 1 finished its actions."
    override suspend fun run() {
        initSimulation()
        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        simEnd()
    }
    private suspend fun initSimulation() {
        /*
        assertNextLine("Initialization Info: obamna.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpWithTwoShips.json successfully parsed and validated.")
        assertNextLine("Initialization Info: scoutContainerUnloadScenario.json successfully parsed and validated.")
        assertNextLine("Simulation Info: Simulation started.")
        */
        skipLines(4)
    }
    suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase
        assertNextLine(corporationStartedToMove)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 14.")
        skipLines(4)
        // assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        // assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        // assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        // assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting Phase
        // assertNextLine("Current Drift: Ship 1 drifted from tile 1 to tile 7.")

        // Events Phase
        assertNextLine("Event: Event 1 of type RESTRICTION happened.")

        // Tasks Phase
        // assertNextLine("Task: Task 1 of type FIND with ship 1 is added with destination 3.")
    }
    suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        assertNextLine(corporationStartedToMove)
        assertNextLine(corporationStartedCollecting)
        assertNextLine(corporationStartedCooperating)
        assertNextLine(corporationStartedRefueling)
        assertNextLine(corporationFinishedActions)
    }
    suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corporation Phase
        assertNextLine(corporationStartedToMove)
        assertNextLine(corporationStartedCollecting)
        assertNextLine(corporationStartedCooperating)
        assertNextLine(corporationStartedRefueling)
        assertNextLine(corporationFinishedActions)
    }
    suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")

        // Corporation Phase
        assertNextLine(corporationStartedToMove)
        assertNextLine(corporationStartedCollecting)
        assertNextLine(corporationStartedCooperating)
        assertNextLine(corporationStartedRefueling)
        assertNextLine(corporationFinishedActions)
    }
    suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        assertNextLine(corporationStartedToMove)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 14.")
        assertNextLine(corporationStartedCollecting)
        assertNextLine(corporationStartedCooperating)
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine(corporationFinishedActions)
    }
    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine(corporationStartedToMove)
        assertNextLine("Simulation Statistics: Corporation 1 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 1000.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
