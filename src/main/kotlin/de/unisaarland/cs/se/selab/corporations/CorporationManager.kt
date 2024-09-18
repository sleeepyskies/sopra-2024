package de.unisaarland.cs.se.selab.corporations

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipState
import de.unisaarland.cs.se.selab.assets.ShipType
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Tile
import kotlin.math.min

/**
 * Manages the corporations in the simulation.
 *
 * @property simData The simulation data used to manage corporations.
 */
class CorporationManager(private val simData: SimulationData) {

    /**
     * The maximum number of ships a corporation can have.
     */
    companion object {
        private const val VELOCITY_DIVISOR = 10
    }

    /**
     * Starts the corporate phase in the simulation.
     */
    fun startCorporatePhase() {
        simData.corporations.forEach {
            moveShipsPhase(it)
            startCollectGarbagePhase(it)
            startCooperationPhase(it)
            startRefuelUnloadPhase(it)
            Logger.corporationActionFinished(it.id)
        }
    }

    /**
     * Moves ships during the corporate phase.
     *
     * @param corporation The corporation whose ships are being moved.
     */
    private fun moveShipsPhase(corporation: Corporation) {
        Logger.corporationActionMove(corporation.id)
        val gbAssignedAmountList = mutableListOf<Garbage>()
        scanAll(corporation.ships, corporation)
        corporation.ships.forEach {
            // determine behavior will return cor a collecting ship the tiles that still need assignment
            val possibleLocationsToMove = determineBehavior(it, corporation)
            // if determine behavior returns the ships location then it shouldn't move and keep its velocity as 0
            if (!(possibleLocationsToMove.size == 1 && possibleLocationsToMove[0] == it.location)) {
                it.updateVelocity()
                val tileInfoToMove = simData.navigationManager.shortestPathToLocations(
                    it.location,
                    possibleLocationsToMove,
                    it.currentVelocity / VELOCITY_DIVISOR
                )

                if (tileInfoToMove.second != 0) {
                    // getting the target location, not the actual location that the ship will move this tick
                    // so that we can assign capacities to that target
                    // and no other ship will be assigned to that location
                    gbAssignedAmountList.addAll(assignCapacityToGarbageList(tileInfoToMove.second, it.capacityInfo))
                    shipMoveToLocation(it, tileInfoToMove.first)
                    Logger.shipMovement(it.id, it.currentVelocity, it.tileId)
                    updateInfo(corporation, scan(it.location, it.visibilityRange))
                } else {
                    it.currentVelocity = 0
                }
            } else {
                it.currentVelocity = 0
            }
            // after every ship of this corporation has moved
            // we set the assignments on the garbage that ships are assigned to  0
            flushAllGarbageAssignments(gbAssignedAmountList)
            applyTrackersForCorporation(corporation)
            corporation.visibleShips.clear()
            corporation.visibleGarbage.forEach { (t, u) -> corporation.garbage[t] = u }
            corporation.visibleGarbage.clear()
        }
    }

    /**
     * Starts the garbage collection phase for a corporation.
     *
     * @param corporation The corporation starting the garbage collection phase.
     */
    private fun startCollectGarbagePhase(corporation: Corporation) {
        Logger.corporationActionCollectGarbage(corporation.id)
        corporation.ships.filter {
            it.type == ShipType.COLLECTING_SHIP || it.capacityInfo.values.isNotEmpty()
        }.forEach { ship ->
            val tile = simData.navigationManager.findTile(ship.location) ?: return
            processGarbageOnTile(tile, ship, corporation)
        }
    }

    private fun processGarbageOnTile(tile: Tile, ship: Ship, corporation: Corporation) {
        val gbList = tile.getGarbageByLowestID()
        gbList.forEach { gb ->
            if (corporation.collectableGarbageTypes.contains(gb.type)) {
                handleGarbageType(gb, tile, ship)
            }
        }
    }

    private fun handleGarbageType(gb: Garbage, tile: Tile, ship: Ship) {
        val shouldRemove = when (gb.type) {
            GarbageType.PLASTIC -> {
                if (checkEnoughShipsForPlasticRemoval(tile, getShipsOnTile(tile.location))) {
                    collectGarbageOnTile(gb, ship)
                } else {
                    false
                }
            }
            GarbageType.OIL, GarbageType.CHEMICALS -> {
                collectGarbageOnTile(gb, ship)
            }
            GarbageType.NONE -> {
                false
            }
        }
        if (shouldRemove) {
            tile.currentGarbage.remove(gb)
            simData.garbage.remove(gb)
        }
    }

    /**
     * Starts the cooperation phase for a corporation.
     *
     * @param corporation The corporation starting the cooperation phase.
     */
    private fun startCooperationPhase(corporation: Corporation) {
        Logger.corporationActionCooperate(corporation.id)
        corporation.ships.filter { it.hasRadio || it.type == ShipType.COORDINATING_SHIP }.forEach {
            getShipsOnTile(it.location).filter { ship -> ship.corporation != corporation.id }.forEach { target ->
                val targetsCorp = simData.corporations[target.corporation]
                if (corporation.lastCooperatedWith != targetsCorp.id) {
                    corporation.lastCooperatedWith = targetsCorp.id
                    shareInformation(corporation, getInfo(targetsCorp.id))
                    Logger.cooperate(corporation.id, targetsCorp.id, it.id, target.id)
                }
            }
        }
    }

    /**
     * Starts the refuel and unload phase for a corporation.
     *
     * @param corporation The corporation starting the refuel and unload phase.
     */
    private fun startRefuelUnloadPhase(corporation: Corporation) {
        Logger.corporationActionRefuel(corporation.id)
        val shipsInHarbor = getShipsInHarbor(corporation)
        for (ship in shipsInHarbor) {
            if (!handleRefuel(ship)) {
                handleUnload(ship)
            }
        }
    }

    /**
     * Retrieves the ships that are currently in the harbor.
     *
     * @param corporation The corporation whose ships are being checked.
     * @return A list of ships that are in the harbor.
     */
    private fun getShipsInHarbor(corporation: Corporation): List<Ship> {
        val corporationShips = corporation.ships
        val corporationHarbors = corporation.harbors
        return corporationShips.filter { ship ->
            corporationHarbors.any { harbor -> harbor == ship.location }
        }.sortedBy { it.id }
    }

    /**
     * Handles the refueling of a ship.
     *
     * @param ship The ship that needs to be refueled.
     */
    private fun handleRefuel(ship: Ship): Boolean {
        when (ship.state) {
            ShipState.NEED_REFUELING -> {
                ship.state = ShipState.REFUELING
                return true
            }
            ShipState.NEED_REFUELING_AND_UNLOADING -> {
                ship.state = ShipState.REFUELING_AND_UNLOADING
                return true
            }
            ShipState.REFUELING -> {
                ship.refuel()
                ship.state = ShipState.DEFAULT
                Logger.refuel(ship.id, ship.tileId)
                return true
            }
            ShipState.REFUELING_AND_UNLOADING -> {
                ship.refuel()
                ship.state = ShipState.UNLOADING
                Logger.refuel(ship.id, ship.tileId)
                return true
            }
            else -> { return false }
        }
    }

    /**
     * Handles the unloading of a ship.
     *
     * @param ship The ship that needs to be unloaded.
     */
    private fun handleUnload(ship: Ship) {
        when (ship.state) {
            ShipState.NEED_UNLOADING -> {
                ship.state = ShipState.UNLOADING
            }
            ShipState.UNLOADING -> {
                val unloadedMap = ship.unload()
                if (unloadedMap[GarbageType.PLASTIC] != 0) {
                    Logger.unload(
                        ship.id,
                        unloadedMap[GarbageType.PLASTIC] ?: 0,
                        GarbageType.PLASTIC.toString(),
                        ship.tileId
                    )
                }
                if (unloadedMap[GarbageType.OIL] != 0) {
                    Logger.unload(
                        ship.id,
                        unloadedMap[GarbageType.OIL] ?: 0,
                        GarbageType.OIL.toString(),
                        ship.tileId
                    )
                }
                if (unloadedMap[GarbageType.CHEMICALS] != 0) {
                    Logger.unload(
                        ship.id,
                        unloadedMap[GarbageType.CHEMICALS] ?: 0,
                        GarbageType.CHEMICALS.toString(),
                        ship.tileId
                    )
                }
                ship.state = ShipState.DEFAULT
            }
            else -> {}
        }
    }

    /**
     * Checks if there are enough ships for plastic removal on a tile.
     *
     * @param tile The tile to check.
     * @param ships The list of ships to check.
     * @return True if there are enough ships, false otherwise.
     */
    private fun checkEnoughShipsForPlasticRemoval(tile: Tile, ships: List<Ship>): Boolean {
        return (
            tile.getGarbageByLowestID().sumOf {
                when (it.type) {
                    GarbageType.PLASTIC -> it.amount
                    else -> 0
                }
            }
                <=
                ships.sumOf { it.capacityInfo[GarbageType.PLASTIC]?.first ?: 0 }
            )
    }

    /**
     * Retrieves information about a corporation.
     *
     * @param corporationId The ID of the corporation.
     * @return A triple containing maps and a list with the corporation's information.
     */
    private fun getInfo(
        corporationId: Int
    ): Map<Int, Pair<Pair<Int, Int>, GarbageType>> {
        val corp = simData.corporations[corporationId]
        // garbageId, location-type
        val garbageInfo = mutableMapOf<Int, Pair<Pair<Int, Int>, GarbageType>>()
        corp.garbage.forEach { t, (u, type) ->
            garbageInfo[t] = Pair(u, type)
        }
        corp.visibleGarbage.forEach { t, (u, type) ->
            garbageInfo[t] = Pair(u, type)
        }
        return garbageInfo
    }

    /**
     * Moves a ship to a specified location.
     *
     * @param ship The ship to move.
     * @param tileInfo The information of the tile to move the ship to, including location and tile ID.
     */
    private fun shipMoveToLocation(ship: Ship, tileInfo: Pair<Pair<Int, Int>, Int>) {
        ship.tileId = tileInfo.second
        ship.location = tileInfo.first
    }

    /**
     * Checks if a ship needs to refuel or unload. And sets the state accordingly
     * @param ship The ship to check.
     * @param corporation The corporation to which the ship belongs.
     */
    private fun checkNeedRefuelOrUnload(ship: Ship, corporation: Corporation) {
        // checkNeedRefuelUnload
        val shipLocation = ship.location
        val currentFuel = ship.currentFuel
        val fuelConsumption = ship.fuelConsumptionRate
        val maxTravelDistanceTiles = currentFuel / fuelConsumption / VELOCITY_DIVISOR
        val shouldMoveToHarbor = simData.navigationManager.shouldMoveToHarbor(
            shipLocation,
            maxTravelDistanceTiles,
            corporation.harbors
        )
        if (shouldMoveToHarbor) {
            ship.state = when (ship.state) {
                ShipState.NEED_UNLOADING -> {
                    ShipState.NEED_REFUELING_AND_UNLOADING
                }
                ShipState.TASKED -> {
                    ship.currentTaskId = -1
                    ShipState.NEED_REFUELING
                }
                else -> {
                    ShipState.NEED_REFUELING
                }
            }
        }
        if (ship.capacityInfo.values.any { it.first <= 0 }) {
            ship.state = when (ship.state) {
                ShipState.NEED_REFUELING -> {
                    ShipState.NEED_REFUELING_AND_UNLOADING
                }
                ShipState.TASKED -> {
                    ShipState.TASKED
                }
                else -> {
                    ShipState.NEED_UNLOADING
                }
            }
        }
    }

    private fun checkShipOnHarborAndNeedsToRefuelOrUnload(ship: Ship, corporation: Corporation): List<Pair<Int, Int>> {
        val shipLocation = ship.location
        val shipIsOnHarbor = corporation.harbors.any { it == shipLocation }
        return if (shipIsOnHarbor) {
            when (ship.state) {
                ShipState.REFUELING, ShipState.UNLOADING, ShipState.REFUELING_AND_UNLOADING -> {
                    listOf(shipLocation)
                }
                else -> {
                    emptyList()
                }
            }
        } else {
            emptyList()
        }
    }

    /**
     * Determines the behavior of a ship for a corporation.
     *
     * @param ship The ship whose behavior is being determined.
     * @param corporation The corporation to which the ship belongs.
     * @return A list of possible locations for the ship to move to.
     */
    private fun determineBehavior(ship: Ship, corporation: Corporation): List<Pair<Int, Int>> {
        val shipType = ship.type
        val shipLocation = ship.location
        val shipMaxTravelDistance = ship.currentVelocity / VELOCITY_DIVISOR

        if (checkRestriction(ship.location)) {
            return listOf(
                simData.navigationManager.getDestinationOutOfRestriction(
                    ship.location,
                    shipMaxTravelDistance
                )
            )
        }
        if (checkShipOnHarborAndNeedsToRefuelOrUnload(ship, corporation).isNotEmpty()) return listOf(shipLocation)
        checkNeedRefuelOrUnload(ship, corporation)

        return when (ship.state) {
            ShipState.NEED_REFUELING, ShipState.NEED_UNLOADING, ShipState.NEED_REFUELING_AND_UNLOADING -> {
                val possibleHarbors = corporation.harbors
                    .filter { simData.navigationManager.canReachDestination(ship, it) }
                possibleHarbors.ifEmpty {
                    listOf(shipLocation)
                }
                possibleHarbors
            }
            ShipState.REFUELING, ShipState.UNLOADING, ShipState.REFUELING_AND_UNLOADING -> {
                mutableListOf(shipLocation)
            }
            ShipState.WAITING_FOR_PLASTIC -> {
                listOf(shipLocation)
            }
            ShipState.TASKED -> {
                handleTaskedState(ship)
            }
            ShipState.DEFAULT -> {
                handleDefaultState(shipType, shipLocation, shipMaxTravelDistance, corporation)
            }
        }
    }

    /**
     * Handles the behavior of a ship that is tasked.
     *
     * @param ship The ship that is tasked.
     * @return A list containing the target location of the task.
     */
    private fun handleTaskedState(ship: Ship): List<Pair<Int, Int>> {
        val task = simData.activeTasks.find {
            it.assignedShipId == ship.id &&
                ship.currentTaskId == it.id
        } ?: return listOf(ship.location)
        val location = simData.navigationManager.locationByTileId(task.targetTileId) ?: return listOf(ship.location)
        return listOf(location)
    }

    /**
     * Handles the default behavior of a ship based on its type.
     *
     * @param shipType The type of the ship.
     * @param shipLocation The current location of the ship.
     * @param shipMaxTravelDistance The maximum travel distance of the ship.
     * @param corporation The corporation to which the ship belongs.
     * @return A list of possible locations for the ship to move to.
     */
    private fun handleDefaultState(
        shipType: ShipType,
        shipLocation: Pair<Int, Int>,
        shipMaxTravelDistance: Int,
        corporation: Corporation
    ): List<Pair<Int, Int>> {
        return when (shipType) {
            ShipType.COLLECTING_SHIP -> {
                if (corporation.visibleGarbage.isNotEmpty()) {
                    var out = corporation.visibleGarbage
                        .filter { corporation.collectableGarbageTypes.contains(it.value.second) }
                        .filter { (k, _) -> getOnlyAssignableGarbagePredicate(k) }
                        .map { it.value.first }.toList()
                    if (out.isEmpty()) out = listOf(shipLocation)
                    return out
                }
                listOf(shipLocation)
            }
            ShipType.COORDINATING_SHIP -> {
                if (corporation.visibleShips.isNotEmpty()) {
                    return corporation.visibleShips.map { it.value.second }.toList()
                }
                listOf(simData.navigationManager.getExplorePoint(shipLocation, shipMaxTravelDistance))
            }
            ShipType.SCOUTING_SHIP -> {
                if (corporation.visibleGarbage.isNotEmpty()) {
                    return corporation.visibleGarbage.map { it.value.first }.toList()
                }
                if (corporation.garbage.isNotEmpty()) {
                    return corporation.garbage.map { it.value.first }.toList()
                }
                listOf(simData.navigationManager.getExplorePoint(shipLocation, shipMaxTravelDistance))
            }
        }
    }

    private fun getOnlyAssignableGarbagePredicate(garbageID: Int): Boolean {
        val garbageObject = simData.garbage.find { it.id == garbageID }
        val garbageAssignedCapacity = garbageObject?.assignedCapacity
        val garbageAmount = garbageObject?.amount
        val assignableCapacity = garbageAmount?.minus(garbageAssignedCapacity ?: 0)
        return assignableCapacity == null || assignableCapacity > 0
    }

    /**
     * Updates the information of a corporation.
     *
     * @param corporation The corporation whose information is being updated.
     * @param info A pair containing maps with the corporation's new information.
     * @return True if the update was successful, false otherwise.
     */
    private fun updateInfo(
        corporation: Corporation,
        info: Pair<
            Map<Int, Pair<Int, Pair<Int, Int>>>,
            Map<Int, Pair<Pair<Int, Int>, GarbageType>>,
            >
    ): Boolean {
        // location, shipId-corpId is the order for the first map
        corporation.visibleShips.putAll(info.first)
        corporation.visibleGarbage.putAll(info.second)
        return true
    }

    /**
     * Shares the information of a corporation.
     *
     * @param to The corporation to share the information with.
     * @param info A map containing the information to share.
     * @return True if the information was successfully shared, false otherwise.
     */
    private fun shareInformation(
        to: Corporation,
        info: Map<Int, Pair<Pair<Int, Int>, GarbageType>>,
    ): Boolean {
        to.garbage.putAll(info)
        return true
    }

    /**
     * Scans all ships in the simulation.
     *
     * @param ships The list of ships to scan.
     */
    private fun scanAll(ships: List<Ship>, corporation: Corporation) {
        ships.forEach {
            val scanInfo = scan(it.location, it.visibilityRange)
            updateInfo(corporation, scanInfo)
        }
        simData.garbage.filter { it.trackedBy.contains(corporation.id) }.forEach {
            corporation.visibleGarbage[it.id] = Pair(it.location, it.type)
        }
    }

    /**
     * Flushes all garbage assignments.
     *
     * @param garbageList The list of garbage to flush assignments for.
     */
    private fun flushAllGarbageAssignments(garbageList: List<Garbage>) {
        garbageList.forEach { it.assignedCapacity = 0 }
    }

    /**
     * Applies trackers for a corporation.
     *
     * @param corporation The corporation to apply trackers for.
     */
    private fun applyTrackersForCorporation(corporation: Corporation) {
        corporation.ships.filter { it.hasTracker }.forEach { ship ->
            simData.garbage.filter {
                it.location == ship.location
            }.forEach {
                it.trackedBy.add(corporation.id)
                Logger.attachTracker(corporation.id, it.id, ship.id)
            }
        }
    }

    /**
     * Checks if there is a restriction at a location.
     *
     * @param location The location to check.
     * @return Returns is restricted, true if the tile doesn't exist.
     */
    private fun checkRestriction(location: Pair<Int, Int>): Boolean {
        return simData.navigationManager.findTile(location)?.isRestricted ?: true
    }

    /**
     * Scans a location within a range.
     *
     * @param location The location to scan.
     * @param range The range to scan within.
     * @return A triple containing maps and a list with the scan results.
     */
    private fun scan(location: Pair<Int, Int>, range: Int):
        Pair<Map<Int, Pair<Int, Pair<Int, Int>>>, Map<Int, Pair<Pair<Int, Int>, GarbageType>>> {
        val tilesInScanRange = simData.navigationManager.getTilesInRadius(location, range)
        val shipInfo = mutableMapOf<Int, Pair<Int, Pair<Int, Int>>>()
        val garbageInfo = mutableMapOf<Int, Pair<Pair<Int, Int>, GarbageType>>()
        for (tileLocation in tilesInScanRange) {
            val tile = simData.navigationManager.findTile(tileLocation) ?: continue
            tile.getGarbageByLowestID().forEach {
                garbageInfo[it.id] = Pair(tileLocation, it.type)
            }
            val ships = getShipsOnTile(tileLocation)
            ships.forEach {
                shipInfo[it.id] = Pair(it.corporation, it.location)
            }
        }
        return Pair(shipInfo, garbageInfo)
    }

    /**
     * Assigns capacity to a list of garbage at a location.
     *
     * @param tileId The ID of the tile where the garbage is located.
     * @param capacities The capacities to assign to the garbage.
     */
    private fun assignCapacityToGarbageList(
        tileId: Int,
        capacities: Map<GarbageType, Pair<Int, Int>>
    ): List<Garbage> {
        val gbAssignedAmountList = mutableListOf<Garbage>()
        val tile = simData.navigationManager.findTile(tileId) ?: return gbAssignedAmountList
        var pCap = capacities[GarbageType.PLASTIC]?.first ?: 0
        var oCap = capacities[GarbageType.OIL]?.first ?: 0
        var cCap = capacities[GarbageType.CHEMICALS]?.first ?: 0
        // assigning the capacities as long as they exist for garbage on tile from the lowest id
        tile.getGarbageByLowestID().forEach { gb ->
            when (gb.type) {
                GarbageType.PLASTIC -> pCap = assignCapacity(gb, pCap, gbAssignedAmountList)
                GarbageType.OIL -> oCap = assignCapacity(gb, oCap, gbAssignedAmountList)
                GarbageType.CHEMICALS -> cCap = assignCapacity(gb, cCap, gbAssignedAmountList)
                GarbageType.NONE -> {}
            }
        }
        return gbAssignedAmountList
    }
    private fun assignCapacity(garbage: Garbage, capacity: Int, gbAssignedAmountList: MutableList<Garbage>): Int {
        if (capacity > 0) {
            val capacityLeftToAssign = garbage.amount - garbage.assignedCapacity
            val assignedAmount = if (capacity > capacityLeftToAssign) capacityLeftToAssign else capacity
            garbage.assignedCapacity += assignedAmount
            gbAssignedAmountList.add(garbage)
            return (capacity - assignedAmount).coerceAtLeast(0)
        }
        return capacity
    }

    /**
     * Collects garbage on a tile.
     *
     * @param gb The garbage to be collected.
     * @param ship The ship collecting the garbage.
     * @return True if the garbage should be removed from the tile, false otherwise.
     */
    private fun collectGarbageOnTile(gb: Garbage, ship: Ship): Boolean {
        var shouldRemove = false
        when (gb.type) {
            GarbageType.PLASTIC -> {
                val collectionAmount = min(gb.amount, ship.capacityInfo[GarbageType.PLASTIC]?.first ?: 0)
                shouldRemove = gb.collectAndShouldBeRemoved(collectionAmount)
                ship.capacityInfo[GarbageType.PLASTIC] = Pair(
                    ship.capacityInfo[GarbageType.PLASTIC]?.first?.minus(collectionAmount) ?: 0,
                    ship.capacityInfo[GarbageType.PLASTIC]?.second ?: 0
                )
                Logger.garbageCollection(ship.id, collectionAmount, gb.type.toString(), gb.amount)
            }
            GarbageType.OIL -> {
                val collectionAmount = min(gb.amount, ship.capacityInfo[GarbageType.OIL]?.first ?: 0)
                shouldRemove = gb.collectAndShouldBeRemoved(collectionAmount)
                ship.capacityInfo[GarbageType.OIL] = Pair(
                    ship.capacityInfo[GarbageType.OIL]?.first?.minus(collectionAmount) ?: 0,
                    ship.capacityInfo[GarbageType.OIL]?.second ?: 0
                )
                Logger.garbageCollection(ship.id, collectionAmount, gb.type.toString(), gb.amount)
            }
            GarbageType.CHEMICALS -> {
                val collectionAmount = min(gb.amount, ship.capacityInfo[GarbageType.CHEMICALS]?.first ?: 0)
                shouldRemove = gb.collectAndShouldBeRemoved(collectionAmount)
                ship.capacityInfo[GarbageType.CHEMICALS] = Pair(
                    ship.capacityInfo[GarbageType.CHEMICALS]?.first?.minus(collectionAmount) ?: 0,
                    ship.capacityInfo[GarbageType.CHEMICALS]?.second ?: 0
                )
                Logger.garbageCollection(ship.id, collectionAmount, gb.type.toString(), gb.amount)
            }
            GarbageType.NONE -> {}
        }
        return shouldRemove
    }

    /**
     * Retrieves the ships on a tile.
     *
     * @param location The location of the tile.
     * @return The list of ships on the tile.
     */
    private fun getShipsOnTile(location: Pair<Int, Int>): List<Ship> {
        val resultList = mutableListOf<Ship>()
        val shipsOfCorp = simData.corporations.flatMap { it.ships }
        for (ship in shipsOfCorp) {
            if (ship.location == location) {
                resultList.add(ship)
            }
        }
        return resultList
    }
}
