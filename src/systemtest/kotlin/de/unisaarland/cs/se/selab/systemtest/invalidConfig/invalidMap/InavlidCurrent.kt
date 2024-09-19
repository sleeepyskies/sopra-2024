package de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidMap

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * applying pirate attack and checking if ship was removed
 */
class InavlidCurrent : ExampleSystemTestExtension() {
    override val description = "Current with 1000 speed"
    override val corporations = "corporationJsons/corpWithTwoShips.json"
    override val scenario = "scenarioJsons/restrictionAndOilSpillPlusCooperatingTask.json"
    override val map = "mapFiles/validationFiles/invalidFiles/obamnaWrongCurretn.json"
    override val name = "test"
    override val maxTicks = 0
    override suspend fun run() {
        assertNextLine("Initialization Info: obamnaWrongCuretn.json is invalid.")
        assertEnd()
    }
}
