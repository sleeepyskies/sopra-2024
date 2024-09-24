package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/** Collecting ship spawns **/
class CannotCollectPlasticHasToWaitForHelp : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/collectingShipWaitsForHelp.json"
    override val scenario = "scenarioJsons/cannotCollectPlasticHasToWaitForHelp.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "CannotCollectPlasticHasToWaitForHelp"
    override val maxTicks = 6
    private val corporationStartedToMove = "Corporation Action: Corporation 2 is starting to move its ships."
    private val corporationStartedCollecting = "Corporation Action: Corporation 2 is starting to collect garbage."
    private val corporationStartedCooperating = "Corporation Action: Corporation" +
        " 2 is starting to cooperate with other corporations."
    private val corporationStartedRefueling = "Corporation Action: Corporation 2 is starting to refuel."
    private val corporationFinishedActions = "Corporation Action: Corporation 2 finished its actions."
    override suspend fun run() {
        initSimulation()
        tick0()
        tick1()
        tick2()
        tick3()
        tick4()
        tick5()
        simEnd()
    }
    private suspend fun initSimulation() {
        skipLines(4)
    }
    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        assertNextLine(corporationStartedToMove)
        assertNextLine("Ship Movement: Ship 1 moved with speed 25 to tile 32.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 15.")
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 22.")
        assertNextLine(corporationStartedCollecting)
        assertNextLine(corporationStartedCooperating)
        assertNextLine(corporationStartedRefueling)
        assertNextLine(corporationFinishedActions)
    }
    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        assertNextLine(corporationStartedToMove)
        assertNextLine("Ship Movement: Ship 1 moved with speed 50 to tile 25.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 16.")
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 10.")
        assertNextLine(corporationStartedCollecting)
        assertNextLine(corporationStartedCooperating)
        assertNextLine(corporationStartedRefueling)
        assertNextLine(corporationFinishedActions)
    }
    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        assertNextLine(corporationStartedToMove)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 17.")
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 25.")
        assertNextLine(corporationStartedCollecting)
        assertNextLine(corporationStartedCooperating)
        assertNextLine(corporationStartedRefueling)
        assertNextLine(corporationFinishedActions)
    }
    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")
        assertNextLine(corporationStartedToMove)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 34.")
        assertNextLine(corporationStartedCollecting)
        assertNextLine(corporationStartedCooperating)
        assertNextLine(corporationStartedRefueling)
        assertNextLine(corporationFinishedActions)
    }
    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")
        assertNextLine(corporationStartedToMove)
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 25.")
        assertNextLine(corporationStartedCollecting)
        assertNextLine("Garbage Collection: Ship 2 collected 1000 of garbage PLASTIC with 70")
        assertNextLine("Garbage Collection: Ship 3 collected 1000 of garbage PLASTIC with 70")
        assertNextLine(corporationStartedCooperating)
        assertNextLine(corporationStartedRefueling)
        assertNextLine(corporationFinishedActions)
    }
    private suspend fun tick5() {
        assertNextLine("Simulation Info: Tick 5 started.")
        assertNextLine(corporationStartedToMove)
        assertNextLine("Ship Movement: Ship 1 moved with speed 25 to tile 22.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 34.")
        assertNextLine("Ship Movement: Ship 3 moved with speed 10 to tile 34.")
        assertNextLine(corporationStartedCollecting)
        assertNextLine(corporationStartedCooperating)
        assertNextLine(corporationStartedRefueling)
        assertNextLine(corporationFinishedActions)
    }
    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 2 collected 2000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 2000.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
