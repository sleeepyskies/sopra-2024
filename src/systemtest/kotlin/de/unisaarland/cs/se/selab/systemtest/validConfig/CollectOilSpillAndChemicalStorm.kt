package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError
import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs
/**Oil spill happens and chem drifts to deep ocean, shouldnt be collected*/
class CollectOilSpillAndChemicalStorm : ExampleSystemTestExtension() {
    override val description = "tests if garbage is drifted correctly"
    override val corporations = "corporationJsons/corp_coll_oil_chem.json"
    override val scenario = "scenarioJsons/stormWithChemOilSpill.json"
    override val map = "mapFiles/obamna.json"
    override val name = "garbDriftCorrect1"
    override val maxTicks = 3
    override suspend fun run() {
        val expectedString = "Simulation Statistics: Corporation 1 collected 1250 of garbage."
        if (skipUntilLogType(Logs.SIMULATION_STATISTICS) != expectedString) {
            throw SystemTestAssertionError("Collected plastic should be 0!")
        }
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 1250.")
        skipLines(1)
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 2750.")
    }
}
