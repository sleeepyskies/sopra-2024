package de.unisaarland.cs.se.selab.corporations

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.*
import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Ship
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
            scanAll(it.ships)
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
        }.forEach {
            var tile = simData.navigationManager.findTile(it.location) ?: return
            var gbList = tile.getGarbageByLowestID()
            gbList.forEach { gb ->
                if (corporation.collectableGarbageTypes.contains(gb.type)) {
                    when (gb.type) {
                        GarbageType.PLASTIC -> {
                            if (checkEnoughShipsForPlasticRemoval(tile, getShipsOnTile(tile.location))) {
                                collectGarbageOnTile(gb, it)
                            }
                        }
                        GarbageType.OIL -> { collectGarbageOnTile(gb, it) }
                        GarbageType.CHEMICALS -> { collectGarbageOnTile(gb, it) }
                        GarbageType.NONE -> {}
                    }
                }
            }
        }
    }

    /**
     * Starts the cooperation phase for a corporation.
     *
     * @param corporation The corporation starting the cooperation phase.
     */
    fun startCooperationPhase(corporation: Corporation) {
        TODO()
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
        TODO()
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
        TODO()
    }

    /**
     * Scans all ships in the simulation.
     *
     * @param ships The list of ships to scan.
     */
    fun scanAll(ships: List<Ship>) {
        TODO()
    }

    /**
     * Flushes all garbage assignments.
     *
     * @param garbageList The list of garbage to flush assignments for.
     */
    fun flushAllGarbageAssignments(garbageList: List<Garbage>) {
        TODO()
    }

    /**
     * Applies trackers for a corporation.
     *
     * @param corporation The corporation to apply trackers for.
     */
    fun applyTrackersForCorporation(corporation: Corporation) {
        TODO()
    }

    /**
     * Checks if there is a restriction at a location.
     *
     * @param location The location to check.
     * @return True if there is a restriction, false otherwise.
     */
    fun checkRestriction(location: Pair<Int, Int>): Boolean {
        TODO()
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
                GarbageType.PLASTIC -> {
                    if (pCap > 0) {
                        gb.assignedCapacity += if (pCap > gb.amount) gb.amount else pCap
                        pCap = (pCap - gb.amount).coerceAtLeast(0)
                        gbAssignedAmountList.add(gb)
                    }
                }
                GarbageType.OIL -> {
                    if (oCap > 0) {
                        gb.assignedCapacity += if (oCap > gb.amount) gb.amount else oCap
                        oCap = (oCap - gb.amount).coerceAtLeast(0)
                        gbAssignedAmountList.add(gb)
                    }
                }
                GarbageType.CHEMICALS -> {
                    if (cCap > 0) {
                        gb.assignedCapacity += if (cCap > gb.amount) gb.amount else cCap
                        cCap = (cCap - gb.amount).coerceAtLeast(0)
                        gbAssignedAmountList.add(gb)
                    }
                }
                GarbageType.NONE -> {}
            }
        }
        return gbAssignedAmountList
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
        TODO()
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
