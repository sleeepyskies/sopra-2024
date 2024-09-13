package de.unisaarland.cs.se.selab.events

import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Event
import de.unisaarland.cs.se.selab.assets.OilSpillEvent
import de.unisaarland.cs.se.selab.assets.PirateAttackEvent
import de.unisaarland.cs.se.selab.assets.RestrictionEvent
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.StormEvent
import de.unisaarland.cs.se.selab.assets.Tile

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
        checkEndingEvent(endingEvents + nonEndingRestrictionEvents)
        checkScheduledEvents()
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
        }
        return endingEvents
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
            simulationData.activeEvents.remove(event)
        }
        simulationData.navigationManager.initializeAndUpdateGraphStructure()
        val oilEvents = endingEvents.filterIsInstance<OilSpillEvent>()
        for (event in oilEvents) {
            simulationData.activeEvents.remove(event)
        }
        val stormEvents = endingEvents.filterIsInstance<StormEvent>()
        for (event in stormEvents) {
            simulationData.activeEvents.remove(event)
        }
        val pirateEvents = endingEvents.filterIsInstance<PirateAttackEvent>()
        for (event in pirateEvents) {
            simulationData.activeEvents.remove(event)
        }
    }
    private fun checkScheduledEvents() {
        val scheduledEvents = simulationData.scheduledEvents
        val currentTick = simulationData.tick
        val eventsOnCurrentTick = scheduledEvents[currentTick]
        if (eventsOnCurrentTick != null) {
            simulationData.activeEvents.addAll(eventsOnCurrentTick)
            scheduledEvents.remove(currentTick)
            val restrictedEvents = eventsOnCurrentTick.filterIsInstance<RestrictionEvent>()
            applyRestriction(restrictedEvents)
            val oilEvents = eventsOnCurrentTick.filterIsInstance<OilSpillEvent>()
            applyOilSpillEvent(oilEvents)
            val stormEvents = eventsOnCurrentTick.filterIsInstance<StormEvent>()
            applyStormEvent(stormEvents)
            val pirateEvents = eventsOnCurrentTick.filterIsInstance<PirateAttackEvent>()
            applyPirateAttack(pirateEvents)
        }
    }
    private fun applyOilSpillEvent(oilSpillEvents: List<OilSpillEvent>) {
        for (event in oilSpillEvents) {
            val location = event.location
            val radius = event.radius
            val coordinatesList = simulationData.navigationManager.getTilesInRadius(location, radius)
            for (coordinates in coordinatesList) {
                val currentTile = simulationData.navigationManager.findTile(coordinates)
                if (currentTile?.currentOilAmount == null ||
                    currentTile.currentOilAmount >= currentTile.oilMaxCapacity
                ) {
                    continue
                }
                if (currentTile.currentOilAmount + event.amount > currentTile.oilMaxCapacity) {
                    currentTile.currentOilAmount = currentTile.oilMaxCapacity
                } else {
                    currentTile.currentOilAmount += event.amount
                }
            }
        }
    }
    private fun applyRestriction(restrictedEvents: List<RestrictionEvent>) {
        for (event in restrictedEvents) {
            val location = event.location
            val radius = event.radius
            val coordinatesList = simulationData.navigationManager.getTilesInRadius(location, radius)
            // initialize and update graph structure function.
            for (coordinates in coordinatesList) {
                val currentTile = simulationData.navigationManager.findTile(coordinates)
                currentTile?.isRestricted = true
            }
        }
        simulationData.navigationManager.initializeAndUpdateGraphStructure()
    }
    private fun applyStormEvent(stormEvents: List<StormEvent>) {
        for (event in stormEvents) {
            val location = event.location
            val radius = event.radius
            val coordinatesList = simulationData.navigationManager.getTilesInRadius(location, radius)
            for (coordinates in coordinatesList) {
                val tileAffectedByStorm = simulationData.navigationManager.findTile(coordinates) ?: continue
                handleGarbageDrift(tileAffectedByStorm, coordinates, event.direction, event.speed)
            }
        }
    }
    private fun handleGarbageDrift(tile: Tile, coordinates: Pair<Int, Int>, direction: Direction, speed: Int) {
        val garbageOfAffectedTile = tile.getGarbageByLowestID()
        val tilesToDriftGarbage = simulationData.navigationManager.calculateDrift(coordinates, direction, speed)
        for (garbage in garbageOfAffectedTile) {
            for (currentTile in tilesToDriftGarbage) {
                if (currentTile.canGarbageFitOnTile(garbage)) {
                    currentTile.addGarbageToTile(garbage)
                    garbage.location = currentTile.location
                    garbage.tileId = currentTile.id
                }
            }
        }
    }
    private fun applyPirateAttack(pirateAttackEvents: List<PirateAttackEvent>) {
        for (event in pirateAttackEvents) {
            val shipID = event.shipID
            val ship = simulationData.ships.find { it.id == shipID } ?: continue
            val corporationIdOfShip = ship.corporation
            // Searches for a Corporation, which owns the ship and removes the ship from the corporation.
            simulationData.corporations.find { it.id == corporationIdOfShip }?.ships?.remove(ship)
            simulationData.ships.remove(ship)
            // Handling pirate attack: remove ship from corporation, remove from simulation data.
        }
    }
}
