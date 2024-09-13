package de.unisaarland.cs.se.selab.tasks

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Reward
import de.unisaarland.cs.se.selab.assets.RewardType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Task

/**
 * Manages the tasks in the simulation.
 *
 * @property simData The simulation data used to manage tasks.
 */
class TaskManager(private val simData: SimulationData) {

    /**
     * Starts the tasks phase in the simulation.
     */
    fun startTasksPhase() {
        // get tasks scheduled for this tick
        val scheduledTasks = simData.scheduledTasks[simData.tick]
        if (scheduledTasks != null) {
            // there are scheduled tasks, assign them
            for (task in scheduledTasks) {
                val assignedShip = simData.ships.find { it.id == task.assignedShipId }
                if (assignedShip != null) {
                    assignTask(assignedShip, task)
                }
            }
        }

        // get active tasks, check if they have been completed
        for (task in simData.activeTasks) {
            // get the assigned ship
            val assignedShip = simData.ships.find { it.id == task.assignedShipId }

            // check if is task is fulfilled
            if (assignedShip != null && task.taskIsFulfilled(assignedShip.tileId)) {
                // update assignedShips taskID
                assignedShip.currentTaskId = -1

                // get reward, and reward ship
                val reward = simData.rewards.find { it.id == task.rewardId }
                val rewardShip = simData.ships.find { it.id == task.rewardShip }

                // task is fulfilled, grant reward
                if (reward != null && rewardShip != null) {
                    grantReward(rewardShip, reward, task)
                }
            }
        }
    }

    /**
     * Assigns a task to a ship.
     *
     * @param ship The ship to which the task is assigned.
     * @param task The task to be assigned.
     */
    fun assignTask(ship: Ship, task: Task) {
        // add to active tasks list
        simData.activeTasks.add(task)

        // update ships task id
        ship.currentTaskId = task.id

        // log
        Logger.assignTask(task.id, task.type, ship.id, task.targetTileId)
    }

    /**
     * Grants a reward to a ship.
     *
     * @param ship The ship to which the reward is granted.
     * @param reward The reward to be granted.
     */
    fun grantReward(ship: Ship, reward: Reward, task: Task) {
        // remove from active tasks
        simData.activeTasks.remove(task)

        // update ship data
        ship.visibilityRange += reward.visibilityRange
        ship.hasTracker = ship.hasTracker || reward.type == RewardType.TRACKING
        ship.hasRadio = ship.hasRadio || reward.type == RewardType.RADIO

        val capacityInfo = ship.capacityInfo
        if (capacityInfo[GarbageType.PLASTIC] != null) {
            val currentPair = capacityInfo[GarbageType.PLASTIC]!!
            capacityInfo[GarbageType.PLASTIC] = currentPair.copy(second = currentPair.second + reward.capacity)
        }

        // log
        Logger.grantReward(task.id, ship.id, reward.type)
    }
}
