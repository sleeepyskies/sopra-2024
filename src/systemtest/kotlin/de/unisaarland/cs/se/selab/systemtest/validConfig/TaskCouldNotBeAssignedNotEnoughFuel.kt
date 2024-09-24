package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**test*/
class TaskCouldNotBeAssignedNotEnoughFuel : ExampleSystemTestExtension() {
    override val description = "checks if a task cannot be assigned due to physical limitations"
    override val corporations = "corporationJsons/corpWithOneScoutingShipNotEnoughFuel.json"
    override val scenario = "scenarioJsons/shoulShouldGetRewardNextTickNoFuel.json"
    override val map = "mapFiles/bigMap1.json"
    override val name = "TaskCouldNotBeAssignedNoPath"
    override val maxTicks = 3

    override suspend fun run() {
        initSimulation()
        tick0()
        tick1()
        tick2()
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
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 525.")
        skipLines(4)
        assertNextLine("Current Drift: Ship 1 drifted from tile 525 to tile 550.")
        assertNextLine("Task: Task 1 of type FIND with ship 1 is added with destination 21.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 20 to tile 500.")
        skipLines(4)
        assertNextLine("Current Drift: Ship 1 drifted from tile 500 to tile 551.")
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 30 to tile 475.")
        skipLines(4)
    }

    private suspend fun simEnd() {
        skipLines(2)
        // assertNextLine("Simulation Info: Simulation ended.")
        // assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 500.")
        assertEnd()
    }
}
