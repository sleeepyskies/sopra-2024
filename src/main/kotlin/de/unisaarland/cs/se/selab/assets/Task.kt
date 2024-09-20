package de.unisaarland.cs.se.selab.assets

/**
 * Represents a task in the simulation.
 *
 * @property id The unique identifier of the task.
 * @property type The type of the task.
 * @property tick The tick at which the task is scheduled.
 * @property assignedShipId The identifier of the ship assigned to the task.
 * @property targetTileId The identifier of the target tile for the task.
 * @property rewardId The identifier of the reward associated with the task.
 * @property rewardShip The identifier of the ship that will receive the reward.
 */
data class Task(
    val id: Int,
    val type: TaskType,
    val tick: Int,
    val assignedShipId: Int,
    val targetTileId: Int,
    val rewardId: Int,
    val rewardShip: Int
) {
    var isCompleted = false
}
