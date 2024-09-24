package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * A
 */
class AssignManyShipsToGarbage : ExampleSystemTestExtension() {
    override val corporations: String = "corporationJsons/assignManyShipsToGarbage_corp.json"
    override val description: String = "garbage pile needs many corps to assign their ships."
    override val map: String = "mapFiles/bigMap1.json"
    override val maxTicks: Int = 2
    override val name: String = "AssignManyShipsToGarbage"
    override val scenario: String = "scenarioJsons/assignManyShipsToGarbage_scenario.json"

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
        assertNextLine("Simulation Info: Tick 0 started.")

        // Corporation 1
        skipLines(5)

        // Corporation 2
        skipLines(5)

        // Corporation 3
        skipLines(5)
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        // Corporation 1
        skipLines(1)
        assertNextLine("Ship Movement: Ship 101 moved with speed 10 to tile 236.")
        assertNextLine("Ship Movement: Ship 102 moved with speed 10 to tile 236.")
        assertNextLine("Ship Movement: Ship 103 moved with speed 10 to tile 236.")
        assertNextLine("Ship Movement: Ship 104 moved with speed 10 to tile 236.")
        assertNextLine("Ship Movement: Ship 105 moved with speed 10 to tile 236.")
        skipLines(4)

        // Corporation 2
        skipLines(1)
        assertNextLine("Ship Movement: Ship 201 moved with speed 10 to tile 236.")
        assertNextLine("Ship Movement: Ship 204 moved with speed 10 to tile 236.")
        assertNextLine("Ship Movement: Ship 205 moved with speed 10 to tile 236.")
        skipLines(4)

        // Corporation 3
        skipLines(1)
        assertNextLine("Ship Movement: Ship 301 moved with speed 10 to tile 236.")
        assertNextLine("Ship Movement: Ship 302 moved with speed 10 to tile 236.")
        assertNextLine("Ship Movement: Ship 304 moved with speed 10 to tile 236.")
        assertNextLine("Ship Movement: Ship 305 moved with speed 10 to tile 236.")
        skipLines(4)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 100 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 200 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Corporation 300 collected 0 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 9000.")
        assertEnd()
    }
}
