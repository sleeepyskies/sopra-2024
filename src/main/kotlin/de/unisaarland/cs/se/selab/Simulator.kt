package de.unisaarland.cs.se.selab

import de.unisaarland.cs.se.selab.corporations.CorporationManager
import de.unisaarland.cs.se.selab.events.EventManager
import de.unisaarland.cs.se.selab.tasks.TaskManager
import de.unisaarland.cs.se.selab.travelling.TravelManager

class Simulator(
    private val maxTicks: Int,
    private val travelManager: TravelManager,
    private val corporationManager: CorporationManager,
    private val eventManager: EventManager,
    private val taskManager: TaskManager
    ) {
    private var currentTick: Int = 0
    fun run() {
        while (currentTick < maxTicks) {
            corporationManager.startCorporatePhase()
            travelManager.driftGarbagePhase()
            travelManager.shipDriftingPhase()
            eventManager.startEventPhase()
            taskManager.startTasksPhase()
            currentTick++
        }
    }

}
