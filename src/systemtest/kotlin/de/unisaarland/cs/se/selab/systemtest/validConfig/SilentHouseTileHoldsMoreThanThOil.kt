package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**asd**/
class SilentHouseTileHoldsMoreThanThOil : ExampleSystemTestExtension() {
    override val description = "silent house tile holds more than thousand oil in one pile"
    override val corporations = "corporationJsons/corpOnSmallMapSimple.json"
    override val scenario = "scenarioJsons/tooMuchOilOnTile.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "SilentHouseTileHoldsMoreThanThOil"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: corpOnSmallMapSimple.json successfully parsed and validated.")
        assertNextLine("Initialization Info: tooMuchOilOnTile.json is invalid.")
        assertEnd()
    }
}
