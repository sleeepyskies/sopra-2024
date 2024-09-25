package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs

/** checks when refueling is not possible due to currents */
class CurrentsFreakRefueling : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/currentsFuckRefuelingCorp.json"
    override val scenario = "scenarioJsons/currentsFuckRefuelingScenario.json"

    override val map = "mapFiles/bigMap1.json"
    override val name = "CurrentsFreakRefueling"
    override val maxTicks = 17

    override suspend fun run() {
        skipUntilLogType(Logs.TICK16)
        assertNextLine("Corporation Action: Corporation 2 is starting to move its ships.")
        assertNextLine("Corporation Action: Corporation 2 is starting to collect garbage.")
        skipLines(4)
        simEnd()
    }

    private suspend fun simEnd() {
        skipLines(2)
        // assertNextLine("Simulation Info: Simulation ended.")
        // assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
