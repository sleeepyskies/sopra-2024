package de.unisaarland.cs.se.selab.assets

import de.unisaarland.cs.se.selab.navigation.NavigationManager

/**
 * Represents the data used in the simulation.
 *
 * @property navigationManager The manager responsible for navigation.
 * @property corporations A list of corporations involved in the simulation.
 * @property garbage A list of garbage items in the simulation.
 * @property activeEvents A list of currently active events.
 * @property scheduledEvents A map of scheduled events, keyed by their scheduled time.
 * @property scheduledTasks A map of scheduled tasks, keyed by their scheduled time.
 * @property activeTasks A list of currently active tasks.
 * @property rewards A list of rewards available in the simulation.
 */
data class SimulationData(
    val navigationManager: NavigationManager,
    var corporations: List<Corporation> = mutableListOf(),
    var ships: List<Ship> = mutableListOf(),
    var garbage: MutableList<Garbage> = mutableListOf(),
    var activeEvents: MutableList<Event> = mutableListOf(),
    var scheduledEvents: MutableMap<Int, List<Event>> = mutableMapOf(),
    var scheduledTasks: MutableMap<Int, List<Task>> = mutableMapOf(),
    var activeTasks: MutableList<Task> = mutableListOf(),
    var rewards: MutableList<Reward> = mutableListOf(),
    var currentHighestGarbageID: Int,
    var tick: Int = 0
)
