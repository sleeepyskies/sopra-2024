package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**tety**/
class Atlantis4 : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/atlantisCorp4.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "atlantis3"
    override val maxTicks = 5

    override suspend fun run() {
        skipLines(1)
        assertNextLine("Initialization Info: atlantisCorp4.json is invalid.")
    }
}
