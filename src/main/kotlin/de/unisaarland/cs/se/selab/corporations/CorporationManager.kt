package de.unisaarland.cs.se.selab.corporations

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipType
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Tile

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
        corporation.ships.forEach {
            val possibleLocationsToMove = determineBehavior(it, corporation)
            if (possibleLocationsToMove.size != 1 || possibleLocationsToMove[0] != it.location) {
                it.updateVelocity()
                val tileInfoToMove = simData.navigationManager.shortestPathToLocations(
                    it.location,
                    possibleLocationsToMove,
                    it.currentVelocity / VELOCITY_DIVISOR
                )
                Logger.shipMovement(it.id, it.tileId, tileInfoToMove.second)
                shipMoveToLocation(it, tileInfoToMove.first)
                updateInfo(corporation, scan(it.location, it.visibilityRange))
            } else {
                it.acceleration = 0
                it.updateVelocity()
            }
        }
    }

    /**
     * Starts the garbage collection phase for a corporation.
     *
     * @param corporation The corporation starting the garbage collection phase.
     */
    fun startCollectGarbagePhase(corporation: Corporation) {
        Logger.corporationActionCollectGarbage(corporation.id)
        corporation.ships.filter { it.type == ShipType.COLLECTING_SHIP }.forEach {
            var tile = simData.navigationManager.findTile(it.location)
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
        TODO()
    }

    /**
     * Checks if there are enough ships for plastic removal on a tile.
     *
     * @param tile The tile to check.
     * @param ships The list of ships to check.
     * @return True if there are enough ships, false otherwise.
     */
    fun checkEnoughtShipsForPlasticRemoval(tile: Tile, ships: List<Ship>): Boolean {
        TODO()
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
     * @param location The location of the garbage.
     * @param tileId The ID of the tile where the garbage is located.
     * @param capacities The capacities to assign to the garbage.
     */
    fun assignCapacityToGarbageList(
        location: Pair<Int, Int>,
        tileId: Int,
        capacities: Map<GarbageType, Pair<Int, Int>>
    ) {
        TODO()
    }

    /**
     * Collects garbage on a tile.
     *
     * @param location The location of the tile.
     */
    fun collectGarbageOnTile(location: Pair<Int, Int>) {
        TODO()
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
