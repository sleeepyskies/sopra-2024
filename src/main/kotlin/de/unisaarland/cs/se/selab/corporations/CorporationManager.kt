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

    companion object {
        /**
         * The maximum number of ships a corporation can have.
         */
        private const val VELOCITY_DIVISOR = 10
    }

    /**
     * Starts the corporate phase in the simulation.
     */
    fun startCorporatePhase() {
        simData.corporations.forEach {
            scanAll(it.ships, it)
            moveShipsPhase(it)
            startCollectGarbagePhase(it)
            startCooperationPhase(it)
            startRefuelUnloadPhase(it)
        }
    }

    /**
     * Moves ships during the corporate phase.
     *
     * @param corporation The corporation whose ships are being moved.
     */
    fun moveShipsPhase(corporation: Corporation) {
        Logger.corporationActionMove(corporation.id)
        val gbAssignedAmountList = mutableListOf<Garbage>()
        scanAll(corporation.ships, corporation)
        corporation.ships.forEach {
            // determine behavior will return cor a collecting ship the tiles that still need assignment
            val possibleLocationsToMove = determineBehavior(it, corporation)
            // if determine behavior returns the ships location then it shouldn't move and keep its velocity as 0
            if (possibleLocationsToMove.size != 1 || possibleLocationsToMove[0] != it.location) {
                it.updateVelocity()
                val tileInfoToMove = simData.navigationManager.shortestPathToLocations(
                    it.location,
                    possibleLocationsToMove,
                    it.currentVelocity / VELOCITY_DIVISOR
                )
                if (tileInfoToMove.first.second != 0) {
                    // getting the target location, not the actual location that the ship will move this tick
                    // so that we can assign capacities to that target
                    // and no other ship will be assigned to that location
                    gbAssignedAmountList.addAll(assignCapacityToGarbageList(tileInfoToMove.second, it.capacityInfo))
                    Logger.shipMovement(it.id, it.tileId, tileInfoToMove.first.second)
                    shipMoveToLocation(it, tileInfoToMove.first.first)
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
        }
    }

    /**
     * Starts the garbage collection phase for a corporation.
     *
     * @param corporation The corporation starting the garbage collection phase.
     */
    fun startCollectGarbagePhase(corporation: Corporation) {
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
        when (gb.type) {
            GarbageType.PLASTIC -> {
                if (checkEnoughShipsForPlasticRemoval(tile, getShipsOnTile(tile.location))) {
                    collectGarbageOnTile(gb, ship)
                }
            }
            GarbageType.OIL, GarbageType.CHEMICALS -> {
                collectGarbageOnTile(gb, ship)
            }
            GarbageType.NONE -> {
                // No action needed
            }
        }
    }

    /**
     * Starts the cooperation phase for a corporation.
     *
     * @param corporation The corporation starting the cooperation phase.
     */
    fun startCooperationPhase(corporation: Corporation) {
        Logger.corporationActionCooperate(corporation.id)
        corporation.ships.filter { it.hasRadio || it.type == ShipType.COORDINATING_SHIP }.forEach {
            getShipsOnTile(it.location).forEach { target ->
                val targetsCorp = simData.corporations.get(target.corporation)
                if (corporation.lastCooperatedWith != targetsCorp.id) {
                    corporation.lastCooperatedWith = targetsCorp.id
                    updateInfo(corporation, getInfo(targetsCorp.id))
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
    fun startRefuelUnloadPhase(corporation: Corporation) {
        // check if the ship is at a harbor
        // if it is at a harbor then refuel and unload
        // get the ships of the corporation
        val corporationShips = corporation.ships
        // get the harbors of the corporation
        val corporationHarbors = corporation.harbors
        // getting a list of ships that are in the harbor
        val shipsInHarbor = corporationShips.filter { ship ->
            corporationHarbors.any { harbor -> harbor == ship.location }
        }.sortedBy { it.id }
        for (ship in shipsInHarbor) {
            // refuel the ship
            if (ship.state == ShipState.NEED_REFUELING) {
                ship.refuel()
                ship.state = ShipState.DEFAULT
            }
            // unload the garbage
            if (ship.state == ShipState.NEED_UNLOADING) {
                ship.unload()
                ship.state = ShipState.DEFAULT
            }
            if (ship.state == ShipState.NEED_REFUELING_AND_UNLOADING) {
                ship.state = ShipState.NEED_UNLOADING
                ship.refuel()
            }
        }
    }

    /**
     * Checks if there are enough ships for plastic removal on a tile.
     *
     * @param tile The tile to check.
     * @param ships The list of ships to check.
     * @return True if there are enough ships, false otherwise.
     */
    fun checkEnoughShipsForPlasticRemoval(tile: Tile, ships: List<Ship>): Boolean {
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
    fun getInfo(
        corporationId: Int
    ): Triple<Map<Pair<Int, Int>, Pair<Int, Int>>, Map<Int, Pair<Pair<Int, Int>, GarbageType>>, List<Pair<Int, Int>>> {
        var corp = simData.corporations.get(corporationId) ?: return Triple(mapOf(), mapOf(), listOf())
        // location, shipid-corpid
        var shipInfo = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        // garbageid, location-type
        var garbageInfo = mutableMapOf<Int, Pair<Pair<Int, Int>, GarbageType>>()
        var harborInfo = mutableListOf<Pair<Int, Int>>()
        corp.ships.forEach { shipInfo.put(it.location, Pair(it.id, corp.id)) }
        corp.knownShips.forEach { t, (u, loc) ->
            shipInfo.put(loc, Pair(t, u))
        }
        corp.garbage.forEach { t, (u, type) ->
            garbageInfo.put(t, Pair(u, type))
        }
        corp.visibleGarbage.forEach { t, (u, type) ->
            garbageInfo.put(t, Pair(u, type))
        }
        harborInfo.addAll(corp.harbors)
        harborInfo.addAll(corp.knownHarbors)
        return Triple(shipInfo, garbageInfo, harborInfo)
    }

    /**
     * Moves a ship to a specified location.
     *
     * @param ship The ship to move.
     * @param location The location to move the ship to.
     */
    fun shipMoveToLocation(ship: Ship, tileInfo: Pair<Pair<Int, Int>, Int>) {
        ship.tileId = tileInfo.second
        ship.location = tileInfo.first
    }

    /**
     * Determines the behavior of a ship for a corporation.
     *
     * @param ship The ship whose behavior is being determined.
     * @param corporation The corporation to which the ship belongs.
     */
    fun determineBehavior(ship: Ship, corporation: Corporation): List<Pair<Int, Int>> {
        TODO()
    }

    /**
     * Updates the information of a corporation.
     *
     * @param cId The ID of the corporation.
     * @param info The new information to update.
     * @return True if the update was successful, false otherwise.
     */
    fun updateInfo(
        corporation: Corporation,
        info: Triple<
            Map<Pair<Int, Int>, Pair<Int, Int>>,
            Map<Int, Pair<Pair<Int, Int>, GarbageType>>,
            List<Pair<Int, Int>>
            >
    ): Boolean {
        // location, shipid-corpid is the order for the first map
        info.first.forEach { (k, v) -> corporation.knownShips[v.first] = Pair(v.second, k) }
        corporation.garbage.putAll(info.second)
        corporation.knownHarbors.addAll(info.third)
        return true
    }

    /**
     * Scans all ships in the simulation.
     *
     * @param ships The list of ships to scan.
     */
    fun scanAll(ships: List<Ship>, corporation: Corporation) {
        ships.forEach {
            var scanInfo = scan(it.location, it.visibilityRange)
            updateInfo(corporation, scanInfo)
        }
    }

    /**
     * Flushes all garbage assignments.
     *
     * @param garbageList The list of garbage to flush assignments for.
     */
    fun flushAllGarbageAssignments(garbageList: List<Garbage>) {
        garbageList.forEach { it.assignedCapacity = 0 }
    }

    /**
     * Applies trackers for a corporation.
     *
     * @param corporation The corporation to apply trackers for.
     */
    fun applyTrackersForCorporation(corporation: Corporation) {
        corporation.ships.filter { it.hasTracker }.forEach { ship ->
            simData.garbage.filter {
                it.location == ship.location
            }.forEach { it.trackedBy.add(corporation.id) }
        }
    }

    /**
     * Checks if there is a restriction at a location.
     *
     * @param location The location to check.
     * @return Returns is restricted, true if the tile doesn't exist.
     */
    fun checkRestriction(location: Pair<Int, Int>): Boolean {
        return simData.navigationManager.findTile(location)?.isRestricted ?: true
    }

    /**
     * Scans a location within a range.
     *
     * @param location The location to scan.
     * @param range The range to scan within.
     * @return A triple containing maps and a list with the scan results.
     */
    fun scan(location: Pair<Int, Int>, range: Int):
        Triple<
            Map<Pair<Int, Int>, Pair<Int, Int>>,
            Map<Int, Pair<Pair<Int, Int>, GarbageType>>,
            List<Pair<Int, Int>>
            > {
        TODO()
    }

    /**
     * Assigns capacity to a list of garbage at a location.
     *
     * @param tileId The ID of the tile where the garbage is located.
     * @param capacities The capacities to assign to the garbage.
     */
    fun assignCapacityToGarbageList(
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
            val assignedAmount = if (capacity > garbage.amount) garbage.amount else capacity
            garbage.assignedCapacity += assignedAmount
            gbAssignedAmountList.add(garbage)
            return (capacity - assignedAmount).coerceAtLeast(0)
        }
        return capacity
    }

    /**
     * Collects garbage on a tile.
     *
     * @param location The location of the tile.
     */
    fun collectGarbageOnTile(gb: Garbage, ship: Ship) {
        when (gb.type) {
            GarbageType.PLASTIC -> {
                val collectionAmount = min(gb.amount, ship.capacityInfo[GarbageType.PLASTIC]?.first ?: 0)
                gb.amount -= collectionAmount
                ship.capacityInfo[GarbageType.PLASTIC] = Pair(
                    ship.capacityInfo[GarbageType.PLASTIC]?.first?.minus(collectionAmount) ?: 0,
                    ship.capacityInfo[GarbageType.PLASTIC]?.second ?: 0
                )
                Logger.garbageCollection(ship.id, collectionAmount, gb.type.toString(), gb.amount)
            }
            GarbageType.OIL -> {
                val collectionAmount = min(gb.amount, ship.capacityInfo[GarbageType.OIL]?.first ?: 0)
                gb.amount -= collectionAmount
                ship.capacityInfo[GarbageType.OIL] = Pair(
                    ship.capacityInfo[GarbageType.OIL]?.first?.minus(collectionAmount) ?: 0,
                    ship.capacityInfo[GarbageType.OIL]?.second ?: 0
                )
                Logger.garbageCollection(ship.id, collectionAmount, gb.type.toString(), gb.amount)
            }
            GarbageType.CHEMICALS -> {
                val collectionAmount = min(gb.amount, ship.capacityInfo[GarbageType.CHEMICALS]?.first ?: 0)
                gb.amount -= collectionAmount
                ship.capacityInfo[GarbageType.CHEMICALS] = Pair(
                    ship.capacityInfo[GarbageType.CHEMICALS]?.first?.minus(collectionAmount) ?: 0,
                    ship.capacityInfo[GarbageType.CHEMICALS]?.second ?: 0
                )
                Logger.garbageCollection(ship.id, collectionAmount, gb.type.toString(), gb.amount)
            }
            GarbageType.NONE -> {}
        }
    }

    /**
     * Retrieves the ships on a tile.
     *
     * @param location The location of the tile.
     * @return The list of ships on the tile.
     */
    fun getShipsOnTile(location: Pair<Int, Int>): List<Ship> {
        val resultList = mutableListOf<Ship>()
        val shipsOfCorp = simData.corporations.flatMap { it.ships }
        for (ship in shipsOfCorp) {
            if (ship.location == location) {
                resultList.add(ship)
            }
        }
        return resultList
    }

    /**
     * Finds a cooperator for a ship at a location.
     *
     * @param ship The ship for which to find a cooperator.
     * @param location The location to find a cooperator at.
     */
    fun findCooperator(ship: Ship, location: Pair<Int, Int>) {
        TODO()
    }
}
