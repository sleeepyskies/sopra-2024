package de.unisaarland.cs.se.selab.travelling

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Tile

/**
 * Manages the travel-related operations in the simulation.
 *
 * @property simData The simulation data used to manage travel operations.
 */
class TravelManager(private val simData: SimulationData) {
    /**
     * The maximum number of ships a corporation can have.
     */
    companion object {
        private const val INTENSITY_FACTOR = 50
    }

    /**
     * Starts the garbage drifting phase in the simulation.
     * First get all tiles and garbage in correct order
     * then iterate over all tiles and move as much garbage on the tile as is possible
     * if the garbage needs to be split, split it and move the split garbage to the next tile
     * if we cant move the drifted garbage to the tile, the current still uses up its capacity
     */
    fun driftGarbagePhase() {
        val garbageOnMap = simData.navigationManager.getGarbageFromAllTilesInCorrectOrderForDrifting()
        val tilesToUpdate: MutableSet<Tile> = mutableSetOf()
        for ((tiled, listGarbage) in garbageOnMap) {
            // Get tile in ascending order of tileID
            val tile = simData.navigationManager.findTile(tiled)
            // Get garbage on tile in ascending order of garbageID
            val mutableListGarbage = listGarbage.toMutableList()
            if (tile == null || !tile.hasCurrent) continue
            val tileCurrent = tile.current
            val driftCapacity = tileCurrent.intensity * INTENSITY_FACTOR
            val direction = tileCurrent.direction
            val speed = tileCurrent.speed
            handleGarbageDrift(tile, mutableListGarbage, direction, speed, tilesToUpdate, driftCapacity)
        }
        // update all tiles that have garbage arriving
        updateTiles(tilesToUpdate)
    }

    /**
     * Handles the drifting of garbage on a tile.
     *
     * @param tile The tile from which garbage is being drifted.
     * @param mutableListGarbage The mutable list of garbage to be handled.
     * @param direction The direction of the current.
     * @param speed The speed of the current.
     * @param tilesToUpdate The list of tiles to be updated.
     * @param driftCapacity The capacity to drift garbage.
     */
    private fun handleGarbageDrift(
        tile: Tile,
        mutableListGarbage: MutableList<Garbage>,
        direction: Direction,
        speed: Int,
        tilesToUpdate: MutableSet<Tile>,
        driftCapacity: Int
    ) {
        var remainingDriftCapacity = driftCapacity
        while (remainingDriftCapacity != 0 && mutableListGarbage.isNotEmpty()) {
            remainingDriftCapacity = helpHandleGarbageDrift(
                tile,
                mutableListGarbage,
                direction,
                speed,
                tilesToUpdate,
                remainingDriftCapacity
            )
        }
    }

    private fun helpHandleGarbageDrift(
        tile: Tile,
        mutableListGarbage: MutableList<Garbage>,
        direction: Direction,
        speed: Int,
        tilesToUpdate: MutableSet<Tile>,
        currentDriftCapacity: Int
    ): Int {
        var garbageToBeHandled = mutableListGarbage.removeFirst()
        var wasSplit = false
        val oldGarbage = garbageToBeHandled
        if (tile.checkGarbageLeft()) {
            // Get tile path sorted furthest to nearest
            val tilePath = simData.navigationManager.calculateDrift(
                tile.location,
                direction,
                speed
            )
            // Check if garbage is too large and has to be split
            if (garbageToBeHandled.checkSplit(currentDriftCapacity)) {
                garbageToBeHandled = split(garbageToBeHandled, currentDriftCapacity)
                wasSplit = true
            }
            // Get tile to which we driftedGarbage, if null, we have to move to the next garbage pile,
            // as there is no tile to drift to, but still use drift capacity needed to make the attempt
            val tileToUpdate = driftGarbageAlongPath(
                garbageToBeHandled,
                oldGarbage,
                wasSplit,
                tilePath,
                tile,
                currentDriftCapacity
            )
            if (tileToUpdate != null) tilesToUpdate.add(tileToUpdate)
            // if was split, all capacity has been used
            if (wasSplit) {
                return 0
            } else {
                return currentDriftCapacity - garbageToBeHandled.amount
            }
        } else {
            return 0
        }
    }

    /**
     * Drifts garbage along a path.
     * @param garbage the garbage to be drifted
     * @param oldGarbage the old garbage that was split
     * @param wasSplit if the garbage was split
     * @param path the path to drift along
     * @param tile the tile to drift from
     * @param driftCapacity the capacity to drift
     * @return the tile the garbage was drifted to
     */
    private fun driftGarbageAlongPath(
        garbage: Garbage,
        oldGarbage: Garbage,
        wasSplit: Boolean,
        path: List<Tile>,
        tile: Tile,
        driftCapacity: Int
    ): Tile? {
        // check if any of the tiles can fit the garbage and add it to the tile
        for (candidateTile in path) {
            if (candidateTile.canGarbageFitOnTile(garbage)) {
                // Split logic,
                if (wasSplit) {
                    simData.currentHighestGarbageID = garbage.id
                    tile.setAmountOfGarbage(oldGarbage.id, oldGarbage.amount - driftCapacity)
                } else {
                    tile.removeGarbageFromTile(garbage)
                }
                // add it to tiles that have to be updated because they now have arriving garbage
                driftGarbage(candidateTile.location, candidateTile.id, garbage)
                candidateTile.addArrivingGarbageToTile(garbage)
                Logger.currentDriftGarbage(
                    garbage.type.toString(),
                    garbage.id,
                    garbage.amount,
                    tile.id,
                    candidateTile.id
                )
                return candidateTile
            }
        }
        return null
    }

    /**
     * called to update all tiles which garbage has drifted to
     * @param tiles the tiles to be updated
     */
    private fun updateTiles(tiles: Set<Tile>) {
        for (tile in tiles) {
            tile.moveAllArrivingGarbageToTile()
        }
    }

    /**
     * Starts the ship drifting phase in the simulation.
     * First get all tiles and ships in correct order
     * then iterate over all tiles and move as many ships on the tile as is possbible
     * to the next tile in the direction of the current
     * if there is no tile to drift to with the current, we can go to the next tile
     */
    fun shipDriftingPhase() {
        val shipsOnMap = getShipsByLowestTileIDThenLowestShipID()
        for ((tile, listShips) in shipsOnMap) {
            val startTile = simData.navigationManager.findTile(tile)
            val mutableListShips = listShips.toMutableList()
            if (startTile == null || !startTile.hasCurrent) continue
            val tileCurrent = startTile.current
            var driftCapacity = tileCurrent.intensity
            val direction = tileCurrent.direction
            val speed = tileCurrent.speed
            while (driftCapacity != 0 && mutableListShips.isNotEmpty()) {
                val shipToBeHandled = mutableListShips.removeFirst()
                val tilePath = simData.navigationManager.calculateDrift(
                    startTile.location,
                    direction,
                    speed
                )
                if (tilePath.isEmpty()) break
                val tileToDriftTo = tilePath.first()
                driftShip(tileToDriftTo.location, tileToDriftTo.id, shipToBeHandled)
                Logger.currentShipDrift(shipToBeHandled.id, startTile.id, tileToDriftTo.id)
                driftCapacity--
            }
        }
    }

    /**
     * Retrieves ships from simData in order lowest tileID first then lowest shipID inside of the list of ships.
     */
    private fun getShipsByLowestTileIDThenLowestShipID(): List<Pair<Int, List<Ship>>> {
        return simData.ships
            .groupBy { it.tileId }
            .mapValues { entry -> entry.value.sortedBy { it.id } }
            .toList()
            .sortedBy { it.first }
    }

    /**
     * Drifts garbage to a new location.
     *
     * @param location The new location of the garbage.
     * @param garbage The garbage to be drifted.
     */
    fun driftGarbage(location: Pair<Int, Int>, tileID: Int, garbage: Garbage) {
        garbage.location = location
        garbage.tileId = tileID
    }

    /**
     * Drifts a ship to a new location.
     *
     * @param location The new location of the ship.
     * @param ship The ship to be drifted.
     */
    fun driftShip(location: Pair<Int, Int>, tileID: Int, ship: Ship) {
        ship.location = location
        ship.tileId = tileID
    }

    /**
     * Splits the garbage into a specified amount.
     *
     * @param garbage The garbage to be split.
     * @param amount The amount to split the garbage into.
     * @return The new garbage created from the split.
     */
    fun split(garbage: Garbage, amount: Int): Garbage {
        val newId = simData.currentHighestGarbageID + 1
        val newGarbage = garbage.copy(id = newId, amount = amount, trackedBy = mutableListOf())
        return newGarbage
    }

    /**
     * Retrieves the remaining garbage in the ocean.
     *
     * @return The list of remaining garbage in the ocean.
     */
    fun getRemainingGarbageInOcean(): List<Garbage> {
        return simData.garbage
    }
}
