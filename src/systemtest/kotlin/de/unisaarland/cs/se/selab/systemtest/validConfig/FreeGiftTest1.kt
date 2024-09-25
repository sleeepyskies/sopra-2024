package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/** tests if we receive reward regardless of task completion */
class FreeGiftTest1 : ExampleSystemTestExtension() {
    override val corporations = "corporationJsons/collectingShipButNoGarbageAtFindTask.json"
    override val description = "tests if we receive reward regardless of task completion2"
    override val map = "mapFiles/smallMap1.json"
    override val name = "FreeGiftTest"
    override val scenario = "scenarioJsons/findGarbageTask.json"
    override val maxTicks = 8

    override suspend fun run() {
        initSimulation()
        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        tick5()
        tick6()
        tick7()
        simEnd()
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }
    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        skipLines(5)
        assertNextLine("Task: Task 1 of type FIND with ship 1 is added with destination 35.")
    }

    private suspend fun tick1() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 7.")
        skipLines(4)
    }

    private suspend fun tick2() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 8.")
        skipLines(4)
    }

    private suspend fun tick3() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 9.")
        skipLines(4)
    }

    private suspend fun tick4() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 17.")
        skipLines(4)
    }

    private suspend fun tick5() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 18.")
        skipLines(4)
    }

    private suspend fun tick6() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 35.")
        skipLines(4)
    }

    private suspend fun tick7() {
        skipLines(6)
    }
    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
