package de.unisaarland.cs.se.selab.events

import de.unisaarland.cs.se.selab.assets.*

/**
 * The EventManager class is responsible for managing the events that occur in the simulation.
 * It is responsible for creating, updating, and deleting events.
 * It is also responsible for notifying the simulation of the events that have occurred.
 */
class EventManager(private val simulationData: SimulationData) {
    private fun startEventPhase(){
        val activeEvents = simulationData.activeEvents
        reduceDuration(activeEvents)


    }
    private fun reduceDuration(activeEvents: List<Event>){
        val endingEvents = mutableListOf<Event>()
        if (activeEvents.isNotEmpty()) {
            val eventsWithDuration = activeEvents.filterIsInstance<RestrictionEvent>()
            for (event: RestrictionEvent in eventsWithDuration) {
                event.duration -= 1
                if(event.duration == 0){
                    endingEvents.add(event)
                }
            }

        }
        checkEndingEvent(endingEvents)
    }
    private fun checkEndingEvent(endingEvents: List<Event>){
        val restrictedEvents = endingEvents.filterIsInstance<RestrictionEvent>()
        for (event in restrictedEvents){
            val location = event.location
            val radius = event.radius
            val ships = simulationData.corporations.flatMap { it.ships }
            for (ship in ships){
                val shipLocation = ship.location
                if (isWithinRadius(location, shipLocation, radius)){
                    ship.restrictions.remove(event)
                }
            }
        }
    }
}