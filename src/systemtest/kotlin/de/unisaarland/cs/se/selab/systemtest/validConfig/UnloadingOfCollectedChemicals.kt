package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * tests the unloading of chemicals at harbor
 */
class UnloadingOfCollectedChemicals : ExampleSystemTestExtension() {
    override val description = "tests the unloading of chemicals"
    override val corporations = "corporationJsons/unloadingOfCollectedChemsCorp.json"
    override val scenario = "scenarioJsons/unloadingOfCollectedChemsScenario.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "tests the unloading of chemicals"
    override val maxTicks = 6
    override suspend fun run() {
        initSimulation()
    }
    private suspend fun initSimulation() {
        skipLines(4)
        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        tick5()
        simEnd()
    }

    private suspend fun tick0() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 8.")
        skipLines(4)
    }

    private suspend fun tick1() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 2 moved with speed 20 to tile 9.")
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 17.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 2 collected 1000 of garbage CHEMICALS with 70.")
        skipLines(3)
    }

    private suspend fun tick2() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 3 moved with speed 15 to tile 9.")
        skipLines(3)
        assertNextLine("Unload: Ship 2 unloaded 1000 of garbage CHEMICALS at harbor 9.")
        skipLines(1)
    }

    private suspend fun tick3() {
        skipLines(3)
        assertNextLine("Garbage Collection: Ship 2 collected 1000 of garbage CHEMICALS with 70.")
        skipLines(3)
    }

    private suspend fun tick4() {
        skipLines(5)
        assertNextLine("Unload: Ship 2 unloaded 1000 of garbage CHEMICALS at harbor 9.")
        skipLines(1)
    }

    private suspend fun tick5() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 17.")
        skipLines(4)
    }
    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 2000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 2000.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 1000.")
        assertEnd()
    }
}
