package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * CollectingShipMovesToVisibleGarbage
 */
class CorporationGetsInformedThenCollectingShipGoes : ExampleSystemTestExtension() {
    override val description = "Tests if information gain by the corporation is handled " +
        "correctly"
    override val corporations = "corporationJsons/twoCorpsOneScoutingCorp.json"
    override val scenario = "scenarioJsons/garbageWhichCanOnlyBeSeenByOne.json"
    override val map = "mapFiles/obamna.json"
    override val name = "Tests information gain "
    override val maxTicks = 5

    override suspend fun run() {
        initSimulation()

        tick0()

        tick1()

        tick2()

        tick3()

        tick4()

        simEnd()
    }
    private suspend fun initSimulation() {
        skipLines(4)
    }
    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        assertNextLine("Corporation Action: Corporation 1 is starting to move its ships.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 15 to tile 2.")
        skipLines(2)
        assertNextLine("Cooperation: Corporation 1 cooperated with corporation 2 with ship 1 to ship 2.")
        skipLines(3)
        assertNextLine("Ship Movement: Ship 4 moved with speed 10 to tile 4.")
        skipLines(2)
        assertNextLine("Cooperation: Corporation 2 cooperated with corporation 1 with ship 2 to ship 1.")
        skipLines(2)
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 15 to tile 1.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 4 moved with speed 10 to tile 3.")
        skipLines(4)
        assertNextLine("Current Drift: Ship 1 drifted from tile 1 to tile 7.")
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 30 to tile 4.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 12.")
        assertNextLine("Ship Movement: Ship 4 moved with speed 10 to tile 8.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 3 collected 2000 of garbage CHEMICALS with 70.")
        assertNextLine("Garbage Collection: Ship 3 collected 1000 of garbage CHEMICALS with 71.")
        skipLines(3)
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 45 to tile 6.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 8.")
        assertNextLine("Ship Movement: Ship 4 moved with speed 10 to tile 2.")
        skipLines(4)
        assertNextLine("Current Drift: Ship 1 drifted from tile 6 to tile 8.")
    }

    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 50 to tile 5.")
        skipLines(5)
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 3.")
        assertNextLine("Ship Movement: Ship 4 moved with speed 10 to tile 1.")
        skipLines(4)
        assertNextLine("Current Drift: Ship 4 drifted from tile 1 to tile 7.")
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 3000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 3000.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
