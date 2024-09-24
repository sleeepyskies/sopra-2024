package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**Tests the behaviour of 3 corporations with regards to known and visible garbage**/
class VisibleGarbagePriority : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/visibleGarbagePriority.json"
    override val scenario = "scenarioJsons/visibleGarbagePriorityScenario.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "checks if corporations prioritize visible garbage over known garbage"
    override val maxTicks = 9
    private val corporation1StartedToMove = "Corporation Action: Corporation 1 is starting to move its ships."
    private val corporation2StartedToMove = "Corporation Action: Corporation 2 is starting to move its ships."
    private val corporation2StartedCollecting = "Corporation Action: Corporation 2 is starting to collect garbage."
    private val corporation1StartedCollecting = "Corporation Action: Corporation 1 is starting to collect garbage."
    private val corporation2StartedCooperating = "Corporation Action: Corporation" +
        " 2 is starting to cooperate with other corporations."
    private val corporation1StartedCooperating = "Corporation Action: Corporation" +
        " 1 is starting to cooperate with other corporations."
    private val corporation2StartedRefueling = "Corporation Action: Corporation 2 is starting to refuel."
    private val corporation1StartedRefueling = "Corporation Action: Corporation 1 is starting to refuel."
    private val corporation2FinishedActions = "Corporation Action: Corporation 2 finished its actions."
    private val corporation1FinishedActions = "Corporation Action: Corporation 1 finished its actions."
    override suspend fun run() {
        initSimulation()
    }
    private suspend fun initSimulation() {
        skipLines(4)
        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        tick5()
        tick6()
        tick7()
        tick8()
        simEnd()
    }
    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        assertNextLine(corporation1StartedToMove)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 5.")
        assertNextLine("Ship Movement: Ship 4 moved with speed 10 to tile 2.")
        assertNextLine(corporation1StartedCollecting)
        assertNextLine(corporation1StartedCooperating)
        assertNextLine(corporation1StartedRefueling)
        assertNextLine(corporation1FinishedActions)

        assertNextLine(corporation2StartedToMove)
        assertNextLine(corporation2StartedCollecting)
        assertNextLine(corporation2StartedCooperating)
        assertNextLine(corporation2StartedRefueling)
        assertNextLine(corporation2FinishedActions)

        skipLines(1)
        assertNextLine("Ship Movement: Ship 5 moved with speed 10 to tile 26.")
        skipLines(4)
    }
    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        assertNextLine(corporation1StartedToMove)
        assertNextLine("Ship Movement: Ship 1 moved with speed 20 to tile 3.")
        assertNextLine("Ship Movement: Ship 4 moved with speed 20 to tile 4.")
        assertNextLine(corporation1StartedCollecting)
        assertNextLine(corporation1StartedCooperating)
        assertNextLine(corporation1StartedRefueling)
        assertNextLine(corporation1FinishedActions)

        assertNextLine(corporation2StartedToMove)
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 5.")
        assertNextLine(corporation2StartedCollecting)
        assertNextLine(corporation2StartedCooperating)
        assertNextLine(corporation2StartedRefueling)
        assertNextLine(corporation2FinishedActions)

        skipLines(1)
        assertNextLine("Ship Movement: Ship 5 moved with speed 20 to tile 22.")
        skipLines(4)
    }
    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        assertNextLine(corporation1StartedToMove)
        assertNextLine("Ship Movement: Ship 1 moved with speed 20 to tile 11.")
        assertNextLine("Ship Movement: Ship 4 moved with speed 20 to tile 5.")
        assertNextLine(corporation1StartedCollecting)
        assertNextLine(corporation1StartedCooperating)
        assertNextLine("Cooperation: Corporation 1 cooperated with corporation 2 with ship 4 to ship 3.")
        assertNextLine(corporation1StartedRefueling)
        assertNextLine(corporation1FinishedActions)

        assertNextLine(corporation2StartedToMove)
        assertNextLine("Ship Movement: Ship 3 moved with speed 15 to tile 4.")
        assertNextLine(corporation2StartedCollecting)
        assertNextLine(corporation2StartedCooperating)
        assertNextLine(corporation2StartedRefueling)
        assertNextLine(corporation2FinishedActions)

        skipLines(1)
        assertNextLine("Ship Movement: Ship 5 moved with speed 20 to tile 11.")
        skipLines(4)
    }
    suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")
        assertNextLine(corporation1StartedToMove)
        assertNextLine("Ship Movement: Ship 4 moved with speed 10 to tile 4.")
        assertNextLine(corporation1StartedCollecting)
        assertNextLine(corporation1StartedCooperating)
        assertNextLine(corporation1StartedRefueling)
        assertNextLine(corporation1FinishedActions)

        assertNextLine(corporation2StartedToMove)
        assertNextLine("Ship Movement: Ship 3 moved with speed 20 to tile 2.")
        assertNextLine(corporation2StartedCollecting)
        assertNextLine(corporation2StartedCooperating)
        assertNextLine(corporation2StartedRefueling)
        assertNextLine(corporation2FinishedActions)

        skipLines(5)
        assertNextLine("Event: Event 1 of type OIL_SPILL happened.")
    }
    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")
        assertNextLine(corporation1StartedToMove)
        assertNextLine("Ship Movement: Ship 4 moved with speed 20 to tile 12.")
        assertNextLine(corporation1StartedCollecting)
        assertNextLine(corporation1StartedCooperating)
        assertNextLine(corporation1StartedRefueling)
        assertNextLine(corporation1FinishedActions)

        assertNextLine(corporation2StartedToMove)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 5.")
        assertNextLine("Ship Movement: Ship 3 moved with speed 20 to tile 3.")
        assertNextLine(corporation2StartedCollecting)
        assertNextLine(corporation2StartedCooperating)
        assertNextLine(corporation2StartedRefueling)
        assertNextLine(corporation2FinishedActions)

        skipLines(5)
    }
    private suspend fun tick5() {
        assertNextLine("Simulation Info: Tick 5 started.")
        assertNextLine(corporation1StartedToMove)
        assertNextLine("Ship Movement: Ship 4 moved with speed 20 to tile 11.")
        assertNextLine(corporation1StartedCollecting)
        assertNextLine(corporation1StartedCooperating)
        assertNextLine("Cooperation: Corporation 1 cooperated with corporation 3 with ship 4 to ship 5.")
        assertNextLine(corporation1StartedRefueling)
        assertNextLine(corporation1FinishedActions)

        assertNextLine(corporation2StartedToMove)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 14.")
        assertNextLine(corporation2StartedCollecting)
        assertNextLine("Garbage Collection: Ship 2 collected 1000 of garbage OIL with 74.")
        assertNextLine(corporation2StartedCooperating)
        assertNextLine(corporation2StartedRefueling)
        assertNextLine(corporation2FinishedActions)

        skipLines(5)
    }
    private suspend fun tick6() {
        assertNextLine("Simulation Info: Tick 6 started.")
        assertNextLine(corporation1StartedToMove)
        assertNextLine("Ship Movement: Ship 4 moved with speed 10 to tile 12.")
        assertNextLine(corporation1StartedCollecting)
        assertNextLine(corporation1StartedCooperating)
        assertNextLine(corporation1StartedRefueling)
        assertNextLine(corporation1FinishedActions)

        assertNextLine(corporation2StartedToMove)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 3.")
        assertNextLine(corporation2StartedCollecting)
        assertNextLine("Garbage Collection: Ship 2 collected 1000 of garbage OIL with 71.")
        assertNextLine(corporation2StartedCooperating)
        assertNextLine(corporation2StartedRefueling)
        assertNextLine(corporation2FinishedActions)

        skipLines(5)
    }
    private suspend fun tick7() {
        assertNextLine("Simulation Info: Tick 7 started.")
        assertNextLine(corporation1StartedToMove)
        assertNextLine("Ship Movement: Ship 4 moved with speed 20 to tile 3.")
        assertNextLine(corporation1StartedCollecting)
        assertNextLine(corporation1StartedCooperating)
        assertNextLine("Cooperation: Corporation 1 cooperated with corporation 2 with ship 4 to ship 2.")
        assertNextLine(corporation1StartedRefueling)
        assertNextLine(corporation1FinishedActions)

        assertNextLine(corporation2StartedToMove)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 12.")
        assertNextLine(corporation2StartedCollecting)
        assertNextLine("Garbage Collection: Ship 2 collected 1000 of garbage OIL with 72.")
        assertNextLine(corporation2StartedCooperating)
        assertNextLine(corporation2StartedRefueling)
        assertNextLine(corporation2FinishedActions)

        skipLines(5)
    }
    private suspend fun tick8() {
        assertNextLine("Simulation Info: Tick 8 started.")
        assertNextLine(corporation1StartedToMove)
        assertNextLine("Ship Movement: Ship 4 moved with speed 10 to tile 12.")
        assertNextLine(corporation1StartedCollecting)
        assertNextLine(corporation1StartedCooperating)
        assertNextLine(corporation1StartedRefueling)
        assertNextLine(corporation1FinishedActions)

        assertNextLine(corporation2StartedToMove)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 11.")
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 13.")
        assertNextLine(corporation2StartedCollecting)
        assertNextLine("Garbage Collection: Ship 2 collected 500 of garbage OIL with 70.")
        assertNextLine(corporation2StartedCooperating)
        assertNextLine(corporation2StartedRefueling)
        assertNextLine(corporation2FinishedActions)

        skipLines(1)
        assertNextLine("Ship Movement: Ship 5 moved with speed 20 to tile 30.")
        skipLines(4)
    }
    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 3500 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 3 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 3500.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 4000.")
        assertEnd()
    }
}
