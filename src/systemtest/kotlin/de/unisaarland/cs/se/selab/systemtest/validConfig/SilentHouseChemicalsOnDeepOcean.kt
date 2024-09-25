package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**awsed**/
class SilentHouseChemicalsOnDeepOcean : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/corpOnSmallMapSimple.json"
    override val scenario = "scenarioJsons/chemsOnDeepOcean.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "NoWayToLeaveRestriction: task should be assigned even if ship capacity is full"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpOnSmallMapSimple.json successfully parsed and validated.")
        assertNextLine("Initialization Info: chemsOnDeepOcean.json is invalid.")
        assertEnd()
    }
}
