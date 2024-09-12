package de.unisaarland.cs.se.selab.assets

/**
 * Represents a reward in the simulation.
 *
 * @property id The unique identifier of the reward.
 * @property type The type of the reward.
 * @property visibilityRange The visibility range provided by the reward.
 * @property capacity The capacity of the reward.
 * @property garbageType The type of garbage associated with the reward.
 */
data class Reward(
    val id: Int,
    val type: RewardType,
    val visibilityRange: Int,
    val capacity: Int,
    val garbageType: GarbageType
)
