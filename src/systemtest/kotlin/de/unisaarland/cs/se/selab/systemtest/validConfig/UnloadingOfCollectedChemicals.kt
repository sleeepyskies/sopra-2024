package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

class UnloadingOfCollectedChemicals : ExampleSystemTestExtension() {
    override val description = "tests the unloading of chemicals"
    override val corporations = "corporationJsons/unloadingOfCollectedChemsCorp.json"
    override val scenario = "scenarioJsons/unloadingOfCollectedChemsScenario.json"
    override val map = "mapFiles/smallMap1.json"
    override val name = "tests the unloading of chemicals"
    override val maxTicks = 13
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
    }
}
