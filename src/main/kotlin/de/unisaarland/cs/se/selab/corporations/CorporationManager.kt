package de.unisaarland.cs.se.selab.corporations

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipState
import de.unisaarland.cs.se.selab.assets.ShipType
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.TaskType
import de.unisaarland.cs.se.selab.assets.Tile
import kotlin.math.min

/**
 * Manages the corporations in the simulation.
 *
 * @property simData The simulation data used to manage corporations.
 */
class CorporationManager(private val simData: SimulationData) {

    // A helper class used to reduce this classes size.
    private val helper = CorporationManagerHelper(simData)

    /**
     * The distance between 2 tiles in ticks.
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
            updateTasks()
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
        corporation.ships.sortedBy { it.id }.filter { it.currentFuel != 0 }.forEach {
            var isOnRestrictedTile = checkRestriction(it.location)
            // determine behavior will return cor a collecting ship the tiles that still need assignment
            val (possibleLocationsToMove, exploring) = determineBehavior(it, corporation)
            // if determine behavior returns the ships location then it shouldn't move and keep its velocity as 0
            val isOwnLocation = possibleLocationsToMove.size == 1 && possibleLocationsToMove[0].first == it.location
            val isRestrictedAndNotOwnLocation = isOnRestrictedTile && possibleLocationsToMove[0].first != it.location
            if (!(isOwnLocation) || isRestrictedAndNotOwnLocation || exploring) {
                val anticipatedVelocity = (it.currentVelocity + it.acceleration).coerceAtMost(it.maxVelocity)
                val tileInfoToMove: Pair<Pair<Pair<Int, Int>, Int>, Pair<Int, Int>>
                if (isOnRestrictedTile) {
                    val outOfRestrictionTile = possibleLocationsToMove[0].first
                    val shipMaxTravelDistance = min(anticipatedVelocity, it.currentFuel / it.fuelConsumptionRate)
                    // The tileID of the tile we move out to
                    val tileIdOfTile = simData.navigationManager.findTile(outOfRestrictionTile)?.id ?: -1
                    // The tileID of the tile we actually have the destination set to
                    // This is needed to make sure, we don't set our velocity to 0 until we reach that tile
                    isOnRestrictedTile = exploring
                    tileInfoToMove =
                        Pair(
                            Pair(outOfRestrictionTile, tileIdOfTile),
                            Pair(possibleLocationsToMove[0].second, tileIdOfTile)
                        )
                } else {
                    tileInfoToMove = simData.navigationManager.shortestPathToLocations(
                        it.location,
                        possibleLocationsToMove,
                        min(anticipatedVelocity, it.currentFuel / it.fuelConsumptionRate)
                    )
                }
                processShipMovement(it, tileInfoToMove, gbAssignedAmountList, exploring, isOnRestrictedTile)
                updateInfo(corporation, scan(it.location, it.visibilityRange, it.id))
            } else {
                it.currentVelocity = 0
            }
        }
        // after every ship of this corporation has moved
        // we set the assignments on the garbage that ships are assigned to  0
        flushAllGarbageAssignments(gbAssignedAmountList)
        applyTrackersForCorporation(corporation)
        corporation.visibleShips.clear()
        corporation.visibleGarbage.forEach { (t, u) -> corporation.garbage[t] = u }
        corporation.visibleGarbage.clear()
    }
    private fun processShipMovement(
        ship: Ship,
        tileInfoToMove: Pair<Pair<Pair<Int, Int>, Int>, Pair<Int, Int>>,
        gbAssignedAmountList: MutableList<Garbage>,
        exploring: Boolean,
        isOnRestrictedTile: Boolean
    ) {
        ship.updateVelocity()
        if (tileInfoToMove.second.first / VELOCITY_DIVISOR >= 1) {
            ship.currentFuel -= tileInfoToMove.second.first * ship.fuelConsumptionRate
            shipMoveToLocation(ship, tileInfoToMove.first)
            Logger.shipMovement(ship.id, ship.currentVelocity, ship.tileId)
        }
        // getting the target location, not the actual location that the ship will move this tick
        // so that we can assign capacities to that target
        // and no other ship will be assigned to that location
        if (ship.type == ShipType.COLLECTING_SHIP) {
            gbAssignedAmountList.addAll(
                assignCapacityToGarbageList(tileInfoToMove.second.second, ship.capacityInfo)
            )
        }
        checkReachedDestinationAndSetVelocity(
            ship,
            tileInfoToMove.second.first,
            tileInfoToMove.first.second,
            tileInfoToMove.second.second,
            exploring,
            isOnRestrictedTile
        )
    }

    private fun checkReachedDestinationAndSetVelocity(
        ship: Ship,
        travelAmt: Int,
        tileIdToMoveTo: Int,
        actualDestination: Int,
        exploring: Boolean,
        restricted: Boolean
    ) {
        if (tileIdToMoveTo == actualDestination && travelAmt > 0) {
            if (!exploring && !restricted) {
                ship.currentVelocity = 0
            }
        }
    }

    private fun updateTasks() {
        simData.activeTasks.forEach {
            val taskShip = simData.ships.find { ship -> ship.id == it.assignedShipId }
            when (it.type) {
                TaskType.FIND -> {
                    if (it.targetTileId == taskShip?.tileId &&
                        simData.navigationManager.findTile(it.targetTileId)?.currentGarbage?.isNotEmpty() == true
                    ) {
                        it.isCompleted = true
                    }
                }
                // dont update state yet
                TaskType.COORDINATE -> {}
                else -> {
                    if (it.targetTileId == taskShip?.tileId) {
                        it.isCompleted = true
                    }
                }
            }
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
            it.type == ShipType.COLLECTING_SHIP || it.capacityInfo.values.any { x -> x.second != 0 }
        }.forEach { ship ->
            val tile = simData.navigationManager.findTile(ship.location) ?: return
            processGarbageOnTile(tile, ship, corporation)
            helper.checkNeedUnloading(ship)
        }
    }

    private fun processGarbageOnTile(tile: Tile, ship: Ship, corporation: Corporation) {
        val gbList = tile.getGarbageByLowestID()
        gbList.filter { it.type == GarbageType.PLASTIC }.forEach { gb ->
            if (
                corporation.collectableGarbageTypes.contains(gb.type) && (ship.capacityInfo[gb.type]?.first ?: 0) != 0
            ) {
                handleGarbageType(gb, tile, ship)
            }
        }
        gbList.filter { it.type == GarbageType.OIL }.forEach { gb ->
            if (
                corporation.collectableGarbageTypes.contains(gb.type) && (ship.capacityInfo[gb.type]?.first ?: 0) != 0
            ) {
                handleGarbageType(gb, tile, ship)
            }
        }
        gbList.filter { it.type == GarbageType.CHEMICALS }.forEach { gb ->
            if (
                corporation.collectableGarbageTypes.contains(gb.type) && (ship.capacityInfo[gb.type]?.first ?: 0) != 0
            ) {
                handleGarbageType(gb, tile, ship)
            }
        }
    }

    private fun handleGarbageType(gb: Garbage, tile: Tile, ship: Ship) {
        val (shouldRemove, amt) = when (gb.type) {
            GarbageType.PLASTIC -> {
                val plasticShips = getShipsOnTile(tile.location).filter {
                    it.capacityInfo[GarbageType.PLASTIC]?.second != 0
                }
                if (checkEnoughShipsForPlasticRemoval(tile, plasticShips)) {
                    collectGarbageOnTile(gb, ship)
                } else {
                    Pair(false, 0)
                }
            }
            GarbageType.OIL, GarbageType.CHEMICALS -> {
                collectGarbageOnTile(gb, ship)
            }
            GarbageType.NONE -> {
                Pair(false, 0)
            }
        }
        if (shouldRemove) {
            tile.removeGarbageFromTile(gb)
            simData.garbage.remove(gb)
            simData.corporations.find { it.id == ship.corporation }?.garbage?.remove(gb.id)
            simData.corporations.find { it.id == ship.corporation }?.visibleGarbage?.remove(gb.id)
        } else {
            gb.amount -= amt
        }
    }

    /**
     * Starts the cooperation phase for a corporation.
     *
     * @param corporation The corporation starting the cooperation phase.
     */
    private fun startCooperationPhase(corporation: Corporation) {
        Logger.corporationActionCooperate(corporation.id)
        corporation.ships.filter { it.hasRadio || it.type == ShipType.COORDINATING_SHIP }.forEach outer@{
            getShipsOnTile(it.location).filter { ship -> ship.corporation != corporation.id }.forEach inner@{ target ->
                val targetsCorp = simData.corporations.find { corp -> corp.id == target.corporation } ?: return@inner
                if (corporation.lastCooperatedWith != targetsCorp.id) {
                    corporation.lastCooperatedWith = targetsCorp.id
                    shareInformation(corporation, getInfo(targetsCorp.id))
                    Logger.cooperate(corporation.id, targetsCorp.id, it.id, target.id)
                }
            }
        }
        corporation.ships.filter { it.state == ShipState.IS_COOPERATING }.forEach {
            val targetCorps = simData.corporations.filter { corp -> corp.harbors.contains(it.location) }
            targetCorps.forEach { corp -> shareInformation(corp, getInfo(it.corporation)) }
            simData.activeTasks.find { task -> task.assignedShipId == it.id }?.isCompleted = true
            it.state = ShipState.DEFAULT
        }
        simData.activeTasks.filter { it.type == TaskType.COORDINATE }.forEach {
            val taskedShip = simData.ships.find { ship -> ship.id == it.assignedShipId }
            if (taskedShip != null) {
                if (taskedShip.tileId == it.targetTileId) {
                    taskedShip.state = ShipState.IS_COOPERATING
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
        val corp = simData.corporations.find { it.id == corporationId } ?: return emptyMap()
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
        val shipLocation = ship.location
        val currentFuel = ship.currentFuel
        val fuelConsumption = ship.fuelConsumptionRate
        val maxTravelDistanceTiles = currentFuel / fuelConsumption / VELOCITY_DIVISOR
        val harborToTileID = corporation.harbors
            .map {
                    location ->
                location to (
                    simData.navigationManager.findTile(location)?.id
                        ?: Int.MAX_VALUE
                    )
            }
        val shouldMoveToHarbor = simData.navigationManager.shouldMoveToHarbor(
            shipLocation,
            maxTravelDistanceTiles,
            harborToTileID
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
     * @return A list of possible locations for the ship to move to, as well as a boolean
     * indicating whether the ship is exploring (needed for not setting velocity).
     */
    private fun determineBehavior(
        ship: Ship,
        corporation: Corporation
    ): Pair<List<Pair<Pair<Int, Int>, Int>>, Boolean> {
        val shipLocation = ship.location
        val shipMaxTravelDistance =
            (ship.currentVelocity + ship.acceleration).coerceAtMost(ship.maxVelocity) / VELOCITY_DIVISOR
        if (checkRestriction(ship.location)) {
            if (ship.state == ShipState.IS_COOPERATING) {
                ship.state = ShipState.TASKED
            } else {
                ship.state = ShipState.DEFAULT
            }
            val outOfRestrictionTilePlusTravelAmt = simData.navigationManager.getDestinationOutOfRestriction(
                ship.location,
                shipMaxTravelDistance
            )
            val outOfRestrictionTile = outOfRestrictionTilePlusTravelAmt.first
            val stillRestricted = simData.navigationManager.findTile(outOfRestrictionTile)?.isRestricted ?: true
            return Pair(
                listOf(
                    outOfRestrictionTile to outOfRestrictionTilePlusTravelAmt.second
                ),
                stillRestricted
            )
        }
        if (checkShipOnHarborAndNeedsToRefuelOrUnload(
                ship,
                corporation
            ).isNotEmpty()
        ) {
            return Pair(listOf(shipLocation to 0), false)
        }
        checkNeedRefuelOrUnload(ship, corporation)

        return when (ship.state) {
            ShipState.NEED_REFUELING, ShipState.NEED_UNLOADING, ShipState.NEED_REFUELING_AND_UNLOADING -> {
                val harborToTileId = corporation.harbors.map { location ->
                    location to (
                        simData.navigationManager.findTile(location)?.id
                            ?: Int.MAX_VALUE
                        )
                }
                Pair(harborToTileId, false)
            }
            ShipState.REFUELING, ShipState.UNLOADING, ShipState.REFUELING_AND_UNLOADING, ShipState.IS_COOPERATING -> {
                Pair(mutableListOf(shipLocation to 0), false)
            }
            ShipState.TASKED -> {
                Pair(handleTaskedState(ship), false)
            }
            ShipState.DEFAULT -> {
                handleDefaultState(ship, shipMaxTravelDistance, corporation)
            }
        }
    }

    /**
     * Handles the behavior of a ship that is tasked.
     *
     * @param ship The ship that is tasked.
     * @return A list containing the target location of the task.
     */
    private fun handleTaskedState(ship: Ship): List<Pair<Pair<Int, Int>, Int>> {
        val task = simData.activeTasks.find {
            it.assignedShipId == ship.id &&
                ship.currentTaskId == it.id
        } ?: return listOf(ship.location to 0)
        val location =
            simData.navigationManager.locationByTileId(task.targetTileId) ?: return listOf(ship.location to 0)
        return listOf(location to task.targetTileId)
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
        ship: Ship,
        shipMaxTravelDistance: Int,
        corporation: Corporation
    ): Pair<List<Pair<Pair<Int, Int>, Int>>, Boolean> {
        return when (ship.type) {
            ShipType.COLLECTING_SHIP -> helper.handleDefaultStateCollecting(ship, corporation)
            ShipType.COORDINATING_SHIP ->
                helper.handleDefaultStateCoordinating(ship, corporation, shipMaxTravelDistance)
            ShipType.SCOUTING_SHIP -> helper.handleDefaultStateScouting(ship, corporation, shipMaxTravelDistance)
        }
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
        info: Triple<
            Map<Int, Pair<Int, Pair<Int, Int>>>,
            Map<Int, Pair<Pair<Int, Int>, GarbageType>>,
            List<Pair<Int, Int>>
            >
    ): Boolean {
        // location, shipId-corpId is the order for the first map
        corporation.visibleShips.putAll(info.first)
        corporation.visibleGarbage.putAll(info.second)
        val scannedTiles = info.third
        val removedGarbage = corporation.garbage
            .filter { scannedTiles.contains(it.value.first) }
            .filter { !info.second.containsKey(it.key) }
        val removedShips = corporation.ships
            .filter { scannedTiles.contains(it.location) }
            .filter { !info.second.containsKey(it.id) }
        corporation.garbage.keys.removeAll(removedGarbage.keys)
        removedShips.forEach { corporation.visibleShips.remove(it.id) }
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
            val scanInfo = scan(it.location, it.visibilityRange, it.id)
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
                it.location == ship.location && !it.trackedBy.contains(corporation.id)
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
    private fun scan(location: Pair<Int, Int>, range: Int, shipWhichIsScanning: Int):
        Triple<Map<Int, Pair<Int, Pair<Int, Int>>>, Map<Int, Pair<Pair<Int, Int>, GarbageType>>, List<Pair<Int, Int>>> {
        val tilesInScanRange = simData.navigationManager.getTilesInRadius(location, range)
        val shipInfo = mutableMapOf<Int, Pair<Int, Pair<Int, Int>>>()
        val garbageInfo = mutableMapOf<Int, Pair<Pair<Int, Int>, GarbageType>>()
        for (tileLocation in tilesInScanRange) {
            val tile = simData.navigationManager.findTile(tileLocation) ?: continue
            tile.getGarbageByLowestID().forEach {
                garbageInfo[it.id] = Pair(tileLocation, it.type)
            }
            val ships = getShipsOnTile(tileLocation)
            val filteredShips = ships.filter { it.id != shipWhichIsScanning }
            filteredShips.forEach {
                shipInfo[it.id] = Pair(it.corporation, it.location)
            }
        }
        return Triple(shipInfo, garbageInfo, tilesInScanRange)
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
    private fun collectGarbageOnTile(gb: Garbage, ship: Ship): Pair<Boolean, Int> {
        var shouldRemove = false
        var colamt = 0
        when (gb.type) {
            GarbageType.PLASTIC -> {
                val collectionAmount = min(gb.amount, ship.capacityInfo[GarbageType.PLASTIC]?.first ?: 0)
                shouldRemove = gb.collectAndShouldBeRemoved(collectionAmount)
                ship.capacityInfo[GarbageType.PLASTIC] = Pair(
                    ship.capacityInfo[GarbageType.PLASTIC]?.first?.minus(collectionAmount) ?: 0,
                    ship.capacityInfo[GarbageType.PLASTIC]?.second ?: 0
                )
                if (collectionAmount != 0) {
                    Logger.garbageCollection(ship.id, collectionAmount, gb.id, ship.corporation, gb.type)
                }
                colamt = collectionAmount
            }
            GarbageType.OIL -> {
                val collectionAmount = min(gb.amount, ship.capacityInfo[GarbageType.OIL]?.first ?: 0)
                shouldRemove = gb.collectAndShouldBeRemoved(collectionAmount)
                ship.capacityInfo[GarbageType.OIL] = Pair(
                    ship.capacityInfo[GarbageType.OIL]?.first?.minus(collectionAmount) ?: 0,
                    ship.capacityInfo[GarbageType.OIL]?.second ?: 0
                )
                if (collectionAmount != 0) {
                    Logger.garbageCollection(ship.id, collectionAmount, gb.id, ship.corporation, gb.type)
                }
                colamt = collectionAmount
            }
            GarbageType.CHEMICALS -> {
                val collectionAmount = min(gb.amount, ship.capacityInfo[GarbageType.CHEMICALS]?.first ?: 0)
                shouldRemove = gb.collectAndShouldBeRemoved(collectionAmount)
                ship.capacityInfo[GarbageType.CHEMICALS] = Pair(
                    ship.capacityInfo[GarbageType.CHEMICALS]?.first?.minus(collectionAmount) ?: 0,
                    ship.capacityInfo[GarbageType.CHEMICALS]?.second ?: 0
                )
                if (collectionAmount != 0) {
                    Logger.garbageCollection(ship.id, collectionAmount, gb.id, ship.corporation, gb.type)
                }
                colamt = collectionAmount
            }
            GarbageType.NONE -> {}
        }
        return Pair(shouldRemove, colamt)
    }

    /**
     * Retrieves the ships on a tile.
     *
     * @param location The location of the tile.
     * @return The list of ships on the tile.
     */
    private fun getShipsOnTile(location: Pair<Int, Int>): List<Ship> {
        val resultList = mutableListOf<Ship>()
        val shipsOfCorp = simData.corporations.flatMap { it.ships }.sortedBy { it.id }
        for (ship in shipsOfCorp) {
            if (ship.location == location) {
                resultList.add(ship)
            }
        }
        return resultList
    }
}
