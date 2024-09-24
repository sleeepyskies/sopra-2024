package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * ok
 */
class TestOilSpillIds : ExampleSystemTestExtension() {
    override val name = "TestOilSpillIds"
    override val description = "A storm event occurs and moves garbage of type OIL. " +
        "A corporation then moves its COLLECTING ship towards it, as it is then known about. " +
        "However, the OIL is drifted away by a current, so the ship cannot collect it"

    override val map = "mapFiles/bigMap1.json"
    override val corporations = "corporationJsons/testOilSpillIds_corporation.json"
    override val scenario = "scenarioJsons/testOilSpillIds_scenario.json"
    override val maxTicks = 8

    /*
    Note: All garbageIDs have been incremented by 1, as we do
    not know if the initial garbage ID should be 1 or 0
     */

    override suspend fun run() {
        initSimulation()

        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        tick5()
        tick6()
        tick7()

        simEnd()
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 55 moved with speed 10 to tile 380.")
        skipLines(4)

        // Ship Drift Phase
        assertNextLine("Current Drift: Ship 51 drifted from tile 467 to tile 464.")

        // Event Phase
        assertNextLine("Event: Event 69 of type OIL_SPILL happened.")
        assertNextLine("Event: Event 70 of type OIL_SPILL happened.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 381.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 750 of garbage OIL with 18.")
        assertNextLine("Garbage Collection: Ship 50 collected 250 of garbage OIL with 37.")
        skipLines(3)

        // Ship Drift Phase
        assertNextLine("Current Drift: Ship 51 drifted from tile 464 to tile 462.")
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 355.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 750 of garbage OIL with 14.")
        assertNextLine("Garbage Collection: Ship 50 collected 250 of garbage OIL with 33.")
        skipLines(3)
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 330.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 750 of garbage OIL with 9.")
        assertNextLine("Garbage Collection: Ship 50 collected 250 of garbage OIL with 28.")
        skipLines(3)
    }

    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 304.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 750 of garbage OIL with 4.")
        assertNextLine("Garbage Collection: Ship 50 collected 250 of garbage OIL with 23.")
        skipLines(3)
    }

    private suspend fun tick5() {
        assertNextLine("Simulation Info: Tick 5 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 280.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 750 of garbage OIL with 1.")
        assertNextLine("Garbage Collection: Ship 50 collected 250 of garbage OIL with 20.")
        skipLines(3)
    }

    private suspend fun tick6() {
        assertNextLine("Simulation Info: Tick 6 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 281.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 750 of garbage OIL with 2.")
        assertNextLine("Garbage Collection: Ship 50 collected 250 of garbage OIL with 21.")
        skipLines(3)
    }

    private suspend fun tick7() {
        assertNextLine("Simulation Info: Tick 7 started.")

        // Corporation Phase
        skipLines(1)
        assertNextLine("Ship Movement: Ship 50 moved with speed 10 to tile 282.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 50 collected 750 of garbage OIL with 3.")
        assertNextLine("Garbage Collection: Ship 50 collected 250 of garbage OIL with 22.")
        skipLines(3)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 7000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 7000.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 12000.")
        assertEnd()
    }
}
