package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**adsf**/
class ShipsCollabOnChemCollection : ExampleSystemTestExtension() {
    override val description = "ships collaborate on chemical collection"
    override val corporations = "corporationJsons/shipsCollabOnChemCollection_corps.json"
    override val scenario = "scenarioJsons/shipsCollabOnChemCollection_scen.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "shipsCollabOnChemCollection"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: smallMap1.json successfully parsed and validated.")
        assertNextLine("Initialization Info: shipsCollabOnChemCollection_corps.json successfully parsed and validated.")
        assertNextLine("Initialization Info: shipsCollabOnChemCollection_scen.json successfully parsed and validated.")
        skipLines(3)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 5.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 5.")
        skipLines(6)
        assertNextLine("Garbage Collection: Ship 3 collected 1000 of garbage CHEMICALS with 73.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 14.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 14.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 1 collected 1000 of garbage CHEMICALS with 73.")
        skipLines(4)
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 5.")
        skipLines(6)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 5.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 3.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 7.")
        skipLines(6)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 7.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 2.")
        skipLines(8)
        assertNextLine("Unload: Ship 3 unloaded 1000 of garbage CHEMICALS at harbor 7.")
        skipLines(3)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 1.")
        skipLines(3)
        assertNextLine("Unload: Ship 1 unloaded 1000 of garbage CHEMICALS at harbor 7.")
        skipLines(8)
        assertNextLine("Simulation Statistics: Corporation 1 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 2000.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
