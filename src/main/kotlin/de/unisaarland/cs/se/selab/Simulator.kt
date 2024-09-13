package de.unisaarland.cs.se.selab

import de.unisaarland.cs.se.selab.corporations.CorporationManager
import de.unisaarland.cs.se.selab.events.EventManager
import de.unisaarland.cs.se.selab.tasks.TaskManager
import de.unisaarland.cs.se.selab.travelling.TravelManager

/**
 * Simulates the entire process by managing different phases and components.
 *
 * @property maxTicks The maximum number of ticks the simulation will run.
 * @property travelManager Manages travel-related operations in the simulation.
 * @property corporationManager Manages the corporations in the simulation.
 * @property eventManager Manages events in the simulation.
 * @property taskManager Manages tasks in the simulation.
 */
class Simulator(
    private val maxTicks: Int,
    private val travelManager: TravelManager,
    private val corporationManager: CorporationManager,
    private val eventManager: EventManager,
    private val taskManager: TaskManager
) {
    private var currentTick: Int = 0

    /**
     * Runs the simulation until the maximum number of ticks is reached.
     */
    fun run() {
        while (currentTick < maxTicks) {
            Logger.simTick(currentTick)
            corporationManager.startCorporatePhase()
            travelManager.driftGarbagePhase()
            travelManager.shipDriftingPhase()
            eventManager.startEventPhase()
            taskManager.startTasksPhase()
            currentTick++
        }
        Logger.corporationTotalUncollectedGarbage = travelManager.getRemainingGarbageInOcean().map { it.amount }.sum()
        Logger.simulationEnd()
        Logger.simulationInfoStatistics()
    }
}
