package de.unisaarland.cs.se.selab.corporations

import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipState
import de.unisaarland.cs.se.selab.assets.SimulationData

/**
 * A helper class for the CorporationManager. Holds some extra functions to reduce the class size.
 */
class CorporationManagerHelper(simulationData: SimulationData) {

    private val simData = simulationData

    /** helper for magic number */
    companion object {
        private const val VELOCITY_DIVISOR = 10
    }

    /**
     * Helper method for garbage collection in CorporationManager. Handles the state of UNLOADING ships.
     */
    fun checkNeedUnloading(ship: Ship) {
        if (ship.capacityInfo.values.any { it.first <= 0 && it.second != 0 }) {
            ship.state = when (ship.state) {
                ShipState.NEED_REFUELING -> {
                    ShipState.NEED_REFUELING_AND_UNLOADING
                }
                ShipState.TASKED -> {
                    ShipState.TASKED
                }
                ShipState.IS_COOPERATING -> {
                    ShipState.IS_COOPERATING
                }
                ShipState.REFUELING_AND_UNLOADING -> {
                    ShipState.REFUELING_AND_UNLOADING
                }
                ShipState.REFUELING -> {
                    ShipState.REFUELING
                }
                ShipState.UNLOADING -> {
                    ShipState.UNLOADING
                }
                else -> {
                    ShipState.NEED_UNLOADING
                }
            }
        }
    }

    /**
     * Helper method for handleDefaultState() in CorporationManager. Handles the state of COLLECTING ships.
     */
    fun handleDefaultStateCollecting(
        ship: Ship,
        corporation: Corporation
    ): Pair<List<Pair<Pair<Int, Int>, Int>>, Boolean> {
        if (
            corporation.visibleGarbage.any {
                simData.navigationManager.traversablePathExists(ship.location, it.value.first)
            }
        ) {
            var out = corporation.visibleGarbage
                .filter {
                    corporation.collectableGarbageTypes.contains(it.value.second) &&
                        ship.capacityInfo.contains(it.value.second) &&
                        simData.navigationManager.traversablePathExists(ship.location, it.value.first)
                }
                .filter { (k, _) -> getOnlyAssignableGarbagePredicate(k) }
                .map { Pair(it.value.first, it.key) }.toList()
            if (out.isEmpty()) out = listOf(ship.location to 0)
            return Pair(out, false)
        }
        return Pair(listOf(ship.location to 0), false)
    }

    /**
     * Helper method for handleDefaultState() in CorporationManager. Handles the state of COORDINATING ships.
     */
    fun handleDefaultStateCoordinating(
        ship: Ship,
        corporation: Corporation,
        shipMaxTravelDistance: Int
    ): Pair<List<Pair<Pair<Int, Int>, Int>>, Boolean> {
        val shipsToBeConsideredInVisibility = corporation.visibleShips
            .filter {
                ship.corporation != it.value.first &&
                    it.value.first != corporation.lastCooperatedWith &&
                    simData.navigationManager.traversablePathExists(ship.location, it.value.second)
            }
        if (shipsToBeConsideredInVisibility.isNotEmpty()) {
            return Pair(shipsToBeConsideredInVisibility.map { it.value.second to it.key }.toList(), false)
        }
        return Pair(listOf(simData.navigationManager.getExplorePoint(ship.location, shipMaxTravelDistance) to 0), true)
    }

    /**
     * Helper method for handleDefaultState() in CorporationManager. Handles the state of SCOUTING ships.
     */
    fun handleDefaultStateScouting(
        ship: Ship,
        corporation: Corporation,
        shipMaxTravelDistance: Int
    ): Pair<List<Pair<Pair<Int, Int>, Int>>, Boolean> {
        if (corporation.visibleGarbage.any {
                simData.navigationManager.traversablePathExists(ship.location, it.value.first)
            }
        ) {
            return Pair(
                corporation.visibleGarbage.filter {
                    simData.navigationManager.traversablePathExists(
                        ship.location,
                        it.value.first
                    )
                }.map { it.value.first to it.key }.toList(),
                false
            )
        }
        if (corporation.garbage.any {
                simData.navigationManager.traversablePathExists(
                    ship.location,
                    it.value.first
                )
            }
        ) {
            return Pair(
                corporation.garbage.filter {
                    simData.navigationManager.traversablePathExists(
                        ship.location,
                        it.value.first
                    )
                }.map { it.value.first to it.key }.toList(),
                false
            )
        }
        return Pair(
            listOf(simData.navigationManager.getExplorePoint(ship.location, shipMaxTravelDistance) to 0),
            true
        )
    }

    /**
     * Helper method for processShipMovement in CorporationManager.
     */
    fun checkReachedDestinationAndSetVelocity(
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

    /**
     * Encapsulates the logic for making a ship need refueling
     */
    fun makeShipRefueling(ship: Ship) {
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

    /**
     * Checks if a ship can reach a harbor from a location to go to
     */
    fun checkIfShipCanReachHarborFromThere(
        tileInfoToMove: Pair<Pair<Pair<Int, Int>, Int>, Pair<Int, Int>>,
        ship: Ship,
        corporation: Corporation
    ): Boolean {
        val tileToMoveToLocation = tileInfoToMove.first.first
        val maxTravelDistanceAfterMovement =
            (ship.currentFuel - tileInfoToMove.second.first * ship.fuelConsumptionRate) / ship.fuelConsumptionRate
        val distanceInTiles = maxTravelDistanceAfterMovement / VELOCITY_DIVISOR
        val homeHarborsToTileId = corporation.harbors.map { location ->
            location to (
                simData.navigationManager.findTile(location)?.id
                    ?: Int.MAX_VALUE
                )
        }
        return !simData.navigationManager
            .shouldMoveToHarbor(tileToMoveToLocation, distanceInTiles, homeHarborsToTileId)
    }

    /**
     * A filter predicate used to filter garbage by if it has unassigned capacity left.
     */
    private fun getOnlyAssignableGarbagePredicate(garbageID: Int): Boolean {
        val garbageObject = simData.garbage.find { it.id == garbageID }
        val garbageAssignedCapacity = garbageObject?.assignedCapacity
        val garbageAmount = garbageObject?.amount
        val assignableCapacity = garbageAmount?.minus(garbageAssignedCapacity ?: 0)
        return assignableCapacity == null || assignableCapacity > 0
    }
}
