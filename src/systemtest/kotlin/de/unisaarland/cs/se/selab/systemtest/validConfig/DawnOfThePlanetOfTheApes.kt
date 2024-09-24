package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError
import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs

class DawnOfThePlanetOfTheApes : ExampleSystemTestExtension() {
    override val description = "Tests if information gain by the corporation is handled " +
            "correctly"
    override val corporations = "corporationJsons/ape_corp.json"
    override val scenario = "scenarioJsons/ape_scen.json"
    override val map = "mapFiles/obamna_ohne_drifts.json"
    override val name = "Tests information gain "
    override val maxTicks = 4

    override suspend fun run() {
        val expectedString = "Simulation Statistics: Corporation 1 collected 0 of garbage."
        if (skipUntilLogType(Logs.SIMULATION_STATISTICS) != expectedString) {
            throw SystemTestAssertionError("Collected plastic should be 0!")
        }
        assertNextLine("Simulation Statistics: Corporation 2 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 3 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        skipLines(2)
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 2000.")
    }
}