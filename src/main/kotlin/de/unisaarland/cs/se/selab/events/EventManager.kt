package de.unisaarland.cs.se.selab.events

import de.unisaarland.cs.se.selab.assets.*

/**
 * The EventManager class is responsible for managing the events that occur in the simulation.
 * It is responsible for creating, updating, and deleting events.
 * It is also responsible for notifying the simulation of the events that have occurred.
 */
class EventManager(private val simulationData: SimulationData) {
    private fun startEventPhase() {
        val activeEvents = simulationData.activeEvents
        // Filter out non-ending restriction events
        val nonEndingRestrictionEvents = activeEvents.filterNot { it is RestrictionEvent }
        val endingEvents = reduceDuration(activeEvents)


    }
    private fun reduceDuration(activeEvents: List<Event>): List<Event> {
        val endingEvents = mutableListOf<Event>()
        if (activeEvents.isNotEmpty()) {
            val eventsWithDuration = activeEvents.filterIsInstance<RestrictionEvent>()
            for (event: RestrictionEvent in eventsWithDuration) {
                event.duration -= 1
                if (event.duration == 0) {
                    endingEvents.add(event)
                }
            }
            return endingEvents
        }
    }
    private fun checkEndingEvent(endingEvents: List<Event>) {
        val restrictedEvents = endingEvents.filterIsInstance<RestrictionEvent>()
        for (event in restrictedEvents) {
            val location = event.location
            val radius = event.radius
            val coordinatesList = simulationData.navigationManager.getTilesInRadius(location, radius)
            // initialize and update graph structure function.

            for (coordinates in coordinatesList) {
                val currentTile = simulationData.navigationManager.findTile(coordinates)
                currentTile?.isRestricted = false
            }
        }
    }
}