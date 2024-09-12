package de.unisaarland.cs.se.selab.tasks

import de.unisaarland.cs.se.selab.assets.Reward
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.SimulationData

/**
 * Manages the tasks in the simulation.
 *
 * @property simData The simulation data used to manage tasks.
 */
class TaskManager(val simData: SimulationData) {

    /**
     * Starts the tasks phase in the simulation.
     */
    fun startTasksPhase() {
        TODO()
    }

    /**
     * Assigns a task to a ship.
     *
     * @param ship The ship to which the task is assigned.
     * @param taskId The identifier of the task to be assigned.
     */
    fun assignTask(ship: Ship, taskId: Int) {
        TODO()
    }

    /**
     * Grants a reward to a ship.
     *
     * @param ship The ship to which the reward is granted.
     * @param reward The reward to be granted.
     */
    fun grantReward(ship: Ship, reward: Reward) {
        TODO()
    }

    /**
     * Checks if there are any scheduled tasks.
     *
     * @return True if there are scheduled tasks, false otherwise.
     */
    fun checkScheduledTasks(): Boolean {
        TODO()
    }

    /**
     * Checks if there are any active tasks.
     *
     * @return True if there are active tasks, false otherwise.
     */
    fun checkActiveTasks(): Boolean {
        TODO()
    }
}
