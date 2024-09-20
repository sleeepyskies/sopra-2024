package de.unisaarland.cs.se.selab.events

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Event
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.OilSpillEvent
import de.unisaarland.cs.se.selab.assets.PirateAttackEvent
import de.unisaarland.cs.se.selab.assets.RestrictionEvent
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.StormEvent
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType

/**
 * The EventManager class is responsible for managing the events that occur in the simulation.
 * It is responsible for creating, updating, and deleting events.
 * It is also responsible for notifying the simulation of the events that have occurred.
 */
class EventManager(private val simulationData: SimulationData) {
    /**
     * Starts the event phase of the simulation.
     * This method is responsible for checking the active events and reducing their duration.
     * It also checks for ending events and applies the effects of the events.
     */
    fun startEventPhase() {
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
        scheduledEvents[currentTick]?.sortedBy { it.id }?.forEach {
            when (it) {
                is OilSpillEvent -> {
                    applyOilSpillEvent(it)
                    Logger.event(it.id, it.type.toString())
                }
                is StormEvent -> {
                    applyStormEvent(it)
                    Logger.event(it.id, it.type.toString())
                }
                is PirateAttackEvent -> {
                    applyPirateAttack(it)
                    Logger.event(it.id, it.type.toString())
                }
                is RestrictionEvent -> {
                    applyRestriction(it)
                    Logger.event(it.id, it.type.toString())
                }
            }
        }
    }
    private fun applyOilSpillEvent(event: OilSpillEvent) {
        val location = event.location
        val radius = event.radius
        val coordinatesList = simulationData.navigationManager.getTilesInRadius(location, radius)
        val updateToCorporations: MutableList<Garbage> = mutableListOf()
        val tilesToUpdate: MutableList<Tile> = mutableListOf()
        for (coordinates in coordinatesList) {
            val currentTile = simulationData.navigationManager.findTile(coordinates)
            if (currentTile == null ||
                currentTile.currentOilAmount >= currentTile.oilMaxCapacity || currentTile.type == TileType.LAND
            ) {
                continue
            }
            processTile(currentTile, event, updateToCorporations, tilesToUpdate)
        }
        updateCorporations(updateToCorporations)
        updateTiles(tilesToUpdate)
    }

    private fun applyRestriction(event: RestrictionEvent) {
        val location = event.location
        val radius = event.radius
        val coordinatesList = simulationData.navigationManager.getTilesInRadius(location, radius)
        // initialize and update graph structure function.
        for (coordinates in coordinatesList) {
            val currentTile = simulationData.navigationManager.findTile(coordinates)
            currentTile?.isRestricted = true
        }
        simulationData.navigationManager.initializeAndUpdateGraphStructure()
        simulationData.activeEvents.add(event)
    }
    private fun applyStormEvent(event: StormEvent) {
        val location = event.location
        val radius = event.radius
        val coordinatesList = simulationData.navigationManager.getTilesInRadius(location, radius)
        val tilesToUpdate = mutableListOf<Tile>()
        for (coordinates in coordinatesList) {
            val tileAffectedByStorm = simulationData.navigationManager.findTile(coordinates) ?: continue
            tilesToUpdate.addAll(handleGarbageDrift(tileAffectedByStorm, coordinates, event.direction, event.speed))
        }
        tilesToUpdate.forEach { it.moveAllArrivingGarbageToTile() }
    }
    private fun handleGarbageDrift(tile: Tile, coordinates: Pair<Int, Int>, direction: Direction, speed: Int):
        List<Tile> {
        val garbageOfAffectedTile = tile.getGarbageByLowestID()
        val tileToBeUpdate = mutableListOf<Tile>()
        val tilesToDriftGarbage = simulationData.navigationManager.calculateDrift(coordinates, direction, speed)
        for (garbage in garbageOfAffectedTile) {
            for (currentTile in tilesToDriftGarbage) {
                val tileToBeUpdated = driftGarbageIfCanFit(tile, currentTile, garbage, tileToBeUpdate)
                if (tileToBeUpdated != null) {
                    tileToBeUpdate.add(tileToBeUpdated)
                    break
                }
            }
        }
        return tileToBeUpdate
    }

    private fun driftGarbageIfCanFit(
        tile: Tile,
        currentTile: Tile,
        garbage: Garbage,
        tileToBeUpdate: MutableList<Tile>
    ): Tile? {
        if (currentTile.canGarbageFitOnTile(garbage)) {
            tile.removeGarbageFromTile(garbage)
            // Logger.currentDriftGarbage(garbage.type.toString(), garbage.id, garbage.amount, tile.id, currentTile.id)
            if (currentTile.type == TileType.DEEP_OCEAN && garbage.type == GarbageType.CHEMICALS) {
                simulationData.garbage.remove(garbage)
                return null
            }
            currentTile.addArrivingGarbageToTile(garbage)
            garbage.location = currentTile.location
            garbage.tileId = currentTile.id
            tileToBeUpdate.add(currentTile)
            updateCorporations(listOf(garbage))
            return currentTile
        }
        return null
    }

    private fun applyPirateAttack(event: PirateAttackEvent) {
        val shipID = event.shipID
        val ship = simulationData.ships.find { it.id == shipID } ?: return
        val corporationIdOfShip = ship.corporation
        // Searches for a Corporation, which owns the ship and removes the ship from the corporation.
        simulationData.corporations.find { it.id == corporationIdOfShip }?.ships?.remove(ship)
        simulationData.ships.remove(ship)
        // Handling pirate attack: remove ship from corporation, remove from simulation data.
    }
    private fun processTile(
        currentTile: Tile,
        event: OilSpillEvent,
        updateToCorporations: MutableList<Garbage>,
        tilesToUpdate: MutableList<Tile>
    ) {
        if (currentTile.currentOilAmount + event.amount > currentTile.oilMaxCapacity) {
            val newAmountGarbage = currentTile.oilMaxCapacity - currentTile.currentOilAmount
            val newGarbage = createGarbage(newAmountGarbage, currentTile)
            updateToCorporations.add(newGarbage)
            currentTile.addGarbageToTile(newGarbage)
            simulationData.garbage.add(newGarbage)
        } else {
            val newGarbage = createGarbage(event.amount, currentTile)
            updateToCorporations.add(newGarbage)
            currentTile.addGarbageToTile(newGarbage)
            simulationData.garbage.add(newGarbage)
        }
        tilesToUpdate.add(currentTile)
    }
    private fun createGarbage(amount: Int, tile: Tile): Garbage {
        val newGarbage = Garbage(
            simulationData.currentHighestGarbageID + 1,
            amount,
            GarbageType.OIL,
            tile.id,
            tile.location
        )
        simulationData.currentHighestGarbageID = newGarbage.id
        return newGarbage
    }

    private fun updateCorporations(updateToCorporations: List<Garbage>) {
        val corporations = simulationData.corporations
        for (corporation in corporations) {
            for (garbage in updateToCorporations) {
                corporation.visibleGarbage[garbage.id] = Pair(garbage.location, garbage.type)
            }
        }
    }

    private fun updateTiles(tilesToUpdate: List<Tile>) {
        for (tile in tilesToUpdate) {
            tile.moveAllArrivingGarbageToTile()
        }
    }
}
