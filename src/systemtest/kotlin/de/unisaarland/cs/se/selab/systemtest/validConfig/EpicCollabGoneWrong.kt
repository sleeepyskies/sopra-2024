package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/** documented test **/
class EpicCollabGoneWrong : ExampleSystemTestExtension() {
    override val description = "omg this will be so epic"
    override val corporations = "corporationJsons/epicCollabingCorporations.json"
    override val scenario = "scenarioJsons/empty_scen.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "hey followers"
    override val maxTicks = 3

    override suspend fun run() {
        skipLines(8)
        assertNextLine("Cooperation: Corporation 1 cooperated with corporation 2 with ship 1 to ship 2.")
        skipLines(5)
        assertNextLine("Cooperation: Corporation 2 cooperated with corporation 1 with ship 2 to ship 1.")
        skipLines(4)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 3.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 3.")
        skipLines(6)
        assertNextLine("Ship Movement: Ship 1 moved with speed 20 to tile 1.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 2 moved with speed 20 to tile 1.")
        skipLines(12)
        assertEnd()
    }
}
