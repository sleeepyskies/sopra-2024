package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**test*/
class TaskCouldNotBeAssignedNotEnoughFuel : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/corpWithOneScoutingShip.json"
    override val scenario = "scenarioJsons/shipShouldGetRewardNextTick.json"
    override val map = "mapFiles/obamna.json"
    override val name = "task could not be assigned, not enough fuel"
    override val maxTicks = 3

    override suspend fun run() {
        initSimulation()
        tick0()
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
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 2.")
        assertNextLine("Corporation Action: Corporation 1 is starting to collect garbage.")
        assertNextLine("Corporation Action: Corporation 1 is starting to cooperate with other corporations.")
        assertNextLine("Corporation Action: Corporation 1 is starting to refuel.")
        assertNextLine("Corporation Action: Corporation 1 finished its actions.")
        // Ship Drifting Phase
        // assertNextLine("Current Drift: Ship 1 drifted from tile 1 to tile 7.")
        // Tasks Phase
        assertNextLine("Task: Task 1 of type FIND with ship 1 is added with destination 3.")
    }
}
