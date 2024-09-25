package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension

/**
 * 10MeterTelescope - I love soba noodles
 */
class ChiefKeef : ExampleSystemTestExtension() {
    override val description = "Ship gets quite a lot of telescopes."
    override val corporations = "corporationJsons/ChiefKeef_corp.json"
    override val scenario = "scenarioJsons/ChiefKeef_scen.json"
    override val map = "mapFiles/veryLongMap.json"
    override val name = "ChiefKeef - 10MeterTelescope"
    override val maxTicks = 11

    override suspend fun run() {
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
        tick9()
        tick10()

        simEnd()
    }

    private suspend fun tick0() {
        assertNextLine("Simulation Info: Tick 0 started.")
        skipLines(5)

        // Task Phase
        assertNextLine("Task: Task 0 of type EXPLORE with ship 12 is added with destination 29.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")
        skipLines(5)

        // Task Phase
        assertNextLine("Reward: Task 0: Ship 11 received reward of type TELESCOPE.")
        assertNextLine("Task: Task 1 of type EXPLORE with ship 12 is added with destination 29.")
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")
        skipLines(5)

        // Task Phase
        assertNextLine("Reward: Task 1: Ship 11 received reward of type TELESCOPE.")
        assertNextLine("Task: Task 2 of type EXPLORE with ship 12 is added with destination 29.")
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")
        skipLines(5)

        // Task Phase
        assertNextLine("Reward: Task 2: Ship 11 received reward of type TELESCOPE.")
        assertNextLine("Task: Task 3 of type EXPLORE with ship 12 is added with destination 29.")
    }

    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")
        skipLines(5)

        // Task Phase
        assertNextLine("Reward: Task 3: Ship 11 received reward of type TELESCOPE.")
        assertNextLine("Task: Task 4 of type EXPLORE with ship 12 is added with destination 29.")
    }

    private suspend fun tick5() {
        assertNextLine("Simulation Info: Tick 5 started.")
        skipLines(5)

        // Task Phase
        assertNextLine("Reward: Task 4: Ship 11 received reward of type TELESCOPE.")
    }

    private suspend fun tick6() {
        assertNextLine("Simulation Info: Tick 6 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 11 moved with speed 10 to tile 1.")
        skipLines(4)
    }

    private suspend fun tick7() {
        assertNextLine("Simulation Info: Tick 7 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 11 moved with speed 20 to tile 3.")
        skipLines(4)
    }

    private suspend fun tick8() {
        assertNextLine("Simulation Info: Tick 8 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 11 moved with speed 30 to tile 6.")
        skipLines(4)
    }

    private suspend fun tick9() {
        assertNextLine("Simulation Info: Tick 9 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 11 moved with speed 40 to tile 10.")
        skipLines(4)
    }

    private suspend fun tick10() {
        assertNextLine("Simulation Info: Tick 10 started.")
        skipLines(1)
        assertNextLine("Ship Movement: Ship 11 moved with speed 50 to tile 15.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 11 collected 1000 of garbage OIL with 0.")
        skipLines(3)
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 1000.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
