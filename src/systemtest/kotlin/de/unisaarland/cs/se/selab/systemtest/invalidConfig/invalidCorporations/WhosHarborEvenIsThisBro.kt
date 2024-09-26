package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * WhosHarborEvenIsThisBro
 */
class WhosHarborEvenIsThisBro : ExampleSystemTestExtension() {
    override val description = "There is an unclaimed harbor on the map."
    override val corporations = "corporationJsons/WhosHarborEvenIsThisBro_corporations.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/obamna.json"

    // override val map = "mapFiles/obamna.json"
    override val name = "WhosHarborEvenIsThisBro"
    override val maxTicks = 0

    override suspend fun run() {
        assertNextLine("Initialization Info: obamna.json successfully parsed and validated.")
        assertNextLine("Initialization Info: WhosHarborEvenIsThisBro_corporations.json is invalid.")
        assertEnd()
    }
}
