package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**
 *  BigEyes: A ship gets a decent amount of telescopes.
 *
**/
class BigEyes : ExampleSystemTestExtension() {
    override val description = "BigEyes: A ship gets a decent amount of telescopes."
    override val corporations = "corporationJsons/BigEyes_corporations.json"
    override val scenario = "scenarioJsons/BigEyes_scenario.json"
    override val map = "mapFiles/bigMap1.json"
    override val name = "BigEyes"
    override val maxTicks = 5

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
        skipLines(1)
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 51 moved with speed 10 to tile 12.")

        skipLines(4)
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
