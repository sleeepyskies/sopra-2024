package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**tety**/
class Atlantis3 : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/atlantisCorp3.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "atlantis3"
    override val maxTicks = 5

    override suspend fun run() {
        skipLines(1)
        assertNextLine("Initialization Info: atlantisCorp3.json is invalid.")
    }
}
