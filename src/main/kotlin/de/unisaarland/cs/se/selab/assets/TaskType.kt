package de.unisaarland.cs.se.selab.assets

/**
 * Represents the different types of tasks that can be assigned to a ship in the simulation.
 */
enum class TaskType {
    /**
     * Task type for collecting items.
     */
    COLLECT,

    /**
     * Task type for exploring new areas.
     */
    EXPLORE,

    /**
     * Task type for finding specific items or locations.
     */
    FIND,

    /**
     * Task type for coordinating activities.
     */
    COORDINATE
}
