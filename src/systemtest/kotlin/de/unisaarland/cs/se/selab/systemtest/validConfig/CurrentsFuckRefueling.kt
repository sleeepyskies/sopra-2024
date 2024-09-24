package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/** checks when refueling is not possible due to currents */
class CurrentsFuckRefueling : ExampleSystemTestExtension() {
    override val description = "no"
    override val corporations = "corporationJsons/currentsFuckRefuelingCorp.json"
    override val scenario = "scenarioJsons/currentsFuckRefuelingScenario.json"

    override val map = "mapFiles/bigMap1.json"
    override val name = "task should be assigned even if ship capacity is full"
    override val maxTicks = 30

    override suspend fun run() {
        initSimulation()
    }
    suspend fun initSimulation() {
        skipLines(4)
    }
}
