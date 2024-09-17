package de.unisaarland.cs.se.selab.tasks

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Reward
import de.unisaarland.cs.se.selab.assets.RewardType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipState
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Task
import kotlin.math.floor

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
        println("scheduled tasks for the current tick: " + scheduledTasks)
        if (scheduledTasks != null) {
            // there are scheduled tasks, assign them
            for (task in scheduledTasks) {
                val assignedShip = simData.ships.find { it.id == task.assignedShipId }
                if (assignedShip != null) {
                    println("calling assign task")
                    println(simData.activeTasks)
                    assignTask(assignedShip, task)
                    println(simData.activeTasks)
                }
            }
        }
        println("active task list: " + simData.activeTasks)
        // get active tasks, check if they have been completed
        val fulfilled = mutableListOf<Task>()
        for (task in simData.activeTasks) {
            // get the assigned ship
            val assignedShip = simData.ships.find { it.id == task.assignedShipId }
            println("assigned ship: " + assignedShip)

            // check if is task is fulfilled
            if (assignedShip != null && task.taskIsFulfilled(assignedShip.tileId)) {
                println("ship fulfilled the task")
                // update assignedShips taskID
                assignedShip.currentTaskId = -1

                // get reward, and reward ship
                val reward = simData.rewards.find { it.id == task.rewardId }
                simData.rewards.remove(reward)
                val rewardShip = simData.ships.find { it.id == task.rewardShip }

                // task is fulfilled, grant reward
                if (reward != null && rewardShip != null) {
                    // grant the ship the reward
                    fulfilled.add(task)
                    grantReward(rewardShip, reward, task)
                    // set the tasked ships state to default
                    assignedShip.state = ShipState.DEFAULT
                }
            }
        }
        simData.activeTasks.removeAll(fulfilled)
    }

    /**
     * Assigns a task to a ship.
     *
     * @param ship The ship to which the task is assigned.
     * @param task The task to be assigned.
     */
    private fun assignTask(ship: Ship, task: Task) {
        // check if ship should be assigned the task
        println("ship id: " + ship.id + "assigned ship id: " + task.assignedShipId)
        if (shouldBeAssignedTask(ship, task)) {
            println("should be assigned task is true")
            // add to active tasks list
            simData.activeTasks.add(task)

            // update ships task id
            ship.currentTaskId = task.id

            // update ship state
            ship.state = ShipState.TASKED

            // log
            Logger.assignTask(task.id, task.type, ship.id, task.targetTileId)
        }
    }

    /**
     * Grants a reward to a ship.
     *
     * @param ship The ship to which the reward is granted.
     * @param reward The reward to be granted.
     */
    private fun grantReward(ship: Ship, reward: Reward, task: Task) {
        // update ship data
        ship.visibilityRange += reward.visibilityRange
        ship.hasTracker = ship.hasTracker || reward.type == RewardType.TRACKING
        ship.hasRadio = ship.hasRadio || reward.type == RewardType.RADIO
        println("we are granting a reward")

        val capacityInfo = ship.capacityInfo
        val currentPair = capacityInfo[reward.garbageType]
        if (currentPair != null) {
            capacityInfo[reward.garbageType] = currentPair.copy(second = currentPair.second + reward.capacity)
        } else {
            capacityInfo[reward.garbageType] = Pair(reward.capacity, reward.capacity)
        }

        // log
        Logger.grantReward(task.id, ship.id, reward.type)
    }

    /**
     * Checks if the given ship should be the given task. A task should not be
     * assigned if it has state NEED_REFUELING_AND_UNLOADING or NEED_REFUELING,
     * if it does not have enough fuel to reach the tasks destination  or there is
     * no shortest path to the destination.
     * @param ship The ship to check for
     * @param task The task to check for
     * @return true if the ship should be assigned the task, false otherwise
     */
    private fun shouldBeAssignedTask(ship: Ship, task: Task): Boolean {
        println(ship.state.toString())
        println(ship.location)

        if (ship.location == simData.navigationManager.findTile(task.targetTileId)?.location) {
            return true
        }
        println("here")
        return !(
            ship.state == ShipState.NEED_REFUELING_AND_UNLOADING ||
                ship.state == ShipState.NEED_REFUELING ||
                !hasPathToTask(ship, task) ||
                !canReachTaskAndHarbor(ship, task)
            )
    }

    /**
     * Checks if the given ship has a valid path to the task's destination.
     * @param ship The ship to check for
     * @param task The task to check for
     * @return true if the ship has a valid path, false otherwise
     */
    private fun hasPathToTask(ship: Ship, task: Task): Boolean {
        // get location from tileID, can specify default value since we know
        // task tile exists from the parsing cross validation
        println("hasPath to task output:")
        val taskLocation = listOf(simData.navigationManager.locationByTileId(task.targetTileId) ?: Pair(0, 0))
        println("task location:  $taskLocation")

        // get ship location
        val shipLocation = ship.location
        println("ship location: $shipLocation")

        // get the maximum amount of tiles this ship can travel
        val maxTravelDistance = floor(ship.currentFuel.toDouble() / ship.fuelConsumptionRate.toDouble()).toInt()
        println("the maximum amount of tiles this ship can travel: $maxTravelDistance")

        // check if there is a valid path to the task destination, unpack result
        val nextHop = this.simData.navigationManager.shortestPathToLocations(
            shipLocation,
            taskLocation,
            maxTravelDistance
        ).first.first
        println("next hop: $nextHop")

        // if location current == nextHop, there is no valid path
        println("nextHop != shipLocation: ${nextHop != shipLocation}")
        return nextHop != shipLocation
    }

    /**
     * Checks if the given ship has enough fuel to reach the task,
     * as well as reach the closest home harbor from the task.
     * @param ship The ship to check for
     * @param task The task to check for
     * @return true if the ship has a valid path, false otherwise
     */
    private fun canReachTaskAndHarbor(ship: Ship, task: Task): Boolean {
        // get the ship's home harbors, we may use elvis since this is validated when parsing
        val homeHarbors = this.simData.corporations.find { it.id == ship.corporation }?.harbors ?: listOf(Pair(0, 0))

        // find the closest home harbor to the task location
        println(homeHarbors)
        val closestHarborID = this.simData.navigationManager.shortestPathToLocations(
            ship.location,
            homeHarbors,
            Int.MAX_VALUE - 1
        ).first.second
        println("closest harbor id: $closestHarborID")

        // get tile instances
        val shipTile = this.simData.navigationManager.findTile(ship.tileId)
        val taskTile = this.simData.navigationManager.findTile(task.targetTileId)
        val harborTile = this.simData.navigationManager.findTile(closestHarborID)
        println("shipTile: ${shipTile?.id}")
        println("taskTile: ${taskTile?.id}")
        println("harborTile: ${harborTile?.id}")

        if (shipTile != null && taskTile != null && harborTile != null) {
            // get travel distance from ship location to task destination
            val fromShipToTask = this.simData.navigationManager.travelDistance(shipTile, taskTile)

            // get travel distance from task location to the closest home harbor
            val fromTaskToHarbor = this.simData.navigationManager.travelDistance(taskTile, harborTile)

            // find how much fuel it takes for the ship to travel this distance
            val requiredFuel = (fromShipToTask + fromTaskToHarbor) * ship.fuelConsumptionRate

            // check if the ship this much fuel or more remaining
            println(requiredFuel)
            return ship.currentFuel >= requiredFuel / 10
        } else {
            // if null, something has gone wrong :(((
            return false
        }
    }
}
