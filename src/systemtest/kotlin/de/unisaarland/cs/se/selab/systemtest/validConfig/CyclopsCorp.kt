package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/** checks when refueling is not possible due to currents */
class CyclopsCorp : ExampleSystemTestExtension() {
    override val description = "Tests if ships proritize their respective garbage to be collected and " +
            "collecting ships see"
    override val corporations = "corporationJsons/cyclopsCorporation.json"
    override val scenario = "scenarioJsons/cyclopsScenario.json"
    override val map = "mapFiles/cyclopsMap.json"
    override val name = "CyclopsCorp"
    override val maxTicks = 2

    override suspend fun run() {
        initSimulation()
        tick0()
        tick1()
        simEnd()
    }

    private suspend fun initSimulation() {
        skipLines(4)
    }

    private suspend fun tick0() {
        skipLines(2)
        assertNextLine("Ship Movement: Ship 0 moved with speed 10 to tile 22.")
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 1.")
        skipLines(4)
    }


    private suspend fun tick1() {
        skipLines(6)
    }

    private suspend fun simEnd() {
        skipLines(2)
        // assertNextLine("Simulation Info: Simulation ended.")
        // assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 0 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 2000.")
        assertEnd()
    }
}
