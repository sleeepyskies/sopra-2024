package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**asd**/
class CorpWithNoShips404 : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/corpWithNoShips404.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "CorpWithNoShips404"
    override val maxTicks = 9

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpWithNoShips404.json is invalid.")
        assertEnd()
    }
}
