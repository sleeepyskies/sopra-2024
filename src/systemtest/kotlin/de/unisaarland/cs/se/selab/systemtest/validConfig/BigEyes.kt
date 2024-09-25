package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
/**
 *  BigEyes: A ship gets a decent amount of telescopes.
 *
**/
class BigEyes : ExampleSystemTestExtension() {
    override val description = "BigEyes: A ship gets a decent amount of telescopes."
    override val corporations = "corporationJsons/BigEyes_corporations.json"
    override val scenario = "scenarioJsons/BigEyes_scenario.json"
    override val map = "mapFiles/bigMap1.json"
    override val name = "BigEyes"
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

        skipLines(1)
        skipLines(4)

        assertNextLine("Task: Task 0 of type EXPLORE with ship 1 is added with destination 245.")
    }

    private suspend fun tick1() {
        assertNextLine("Simulation Info: Tick 1 started.")

        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 245.")
        skipLines(4)

        assertNextLine("Reward: Task 0: Ship 1 received reward of type TELESCOPE.")
        assertNextLine("Task: Task 1 of type EXPLORE with ship 1 is added with destination 246.")
    }

    private suspend fun tick2() {
        assertNextLine("Simulation Info: Tick 2 started.")

        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 246.")
        skipLines(4)

        assertNextLine("Reward: Task 1: Ship 1 received reward of type TELESCOPE.")
        assertNextLine("Task: Task 2 of type EXPLORE with ship 1 is added with destination 245.")
    }

    private suspend fun tick3() {
        assertNextLine("Simulation Info: Tick 3 started.")

        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 245.")
        skipLines(1)
        skipLines(3)

        assertNextLine("Reward: Task 2: Ship 1 received reward of type TELESCOPE.")
        assertNextLine("Task: Task 3 of type EXPLORE with ship 1 is added with destination 244.")
    }

    private suspend fun tick4() {
        assertNextLine("Simulation Info: Tick 4 started.")

        skipLines(1)
        assertNextLine("Ship Movement: Ship 1 moved with speed 10 to tile 244.")
        assertNextLine("Ship Movement: Ship 2 moved with speed 10 to tile 137.")
        skipLines(1)
        assertNextLine("Garbage Collection: Ship 2 collected 1000 of garbage PLASTIC with 2.")
        skipLines(3)

        assertNextLine("Reward: Task 3: Ship 1 received reward of type TELESCOPE.")
    }

    private suspend fun simEnd() {
        assertNextLine("Simulation Info: Simulation ended.")
        assertNextLine("Simulation Info: Simulation statistics are calculated.")
        assertNextLine("Simulation Statistics: Corporation 1 collected 1000 of garbage.")
        assertNextLine("Simulation Statistics: Total amount of plastic collected: 1000.")
        assertNextLine("Simulation Statistics: Total amount of oil collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of chemicals collected: 0.")
        assertNextLine("Simulation Statistics: Total amount of garbage still in the ocean: 0.")
        assertEnd()
    }
}
