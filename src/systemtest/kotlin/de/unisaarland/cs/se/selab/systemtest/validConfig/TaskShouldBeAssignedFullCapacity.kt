package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**
 * Scenario: Task could not be assigned, capacity full
 */
class TaskShouldBeAssignedFullCapacity : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/corpWithOneOilCollectShip.json"
    override val scenario = "scenarioJsons/garbagePlasticAndTask.json"
    override val map = "mapFiles/obamna.json"
    override val name = "task should be assigned even if ship capacity is full"
    override val maxTicks = 2

    override suspend fun run() {
        initSimulation()
        tick0()
        tick1()
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
    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Garbage Collection: Ship 1 collected 1000 of garbage PLASTIC with 1.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting Phase
        // assertNextLine("Current Drift: Ship 1 drifted from tile 1 to tile 7.")

        // Tasks Phase
        // assertNextLine("Task: Task 1 of type FIND with ship 1 is added with destination 3.")
    }
    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 12.")
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")

        // Ship Drifting Phase
        // assertNextLine("Current Drift: Ship 1 drifted from tile 7 to tile 13.")

        // Tasks Phase
        assertNextLine("Task: Task 1 of type COOPERATE with ship 1 is added with destination 5.")
    }
    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 1000.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
