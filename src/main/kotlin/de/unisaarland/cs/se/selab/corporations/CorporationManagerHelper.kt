package de.unisaarland.cs.se.selab.corporations

import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.SimulationData

/**
 * A helper class for the CorporationManager. Holds some extra functions to reduce the class size.
 */
class CorporationManagerHelper(simulationData: SimulationData) {

    private val simData = simulationData

    /**
     * Helper method for handleDefaultState() in CorporationManager. Handles the state of COLLECTING ships.
     */
    fun handleDefaultStateCollecting(ship: Ship, corporation: Corporation): Pair<List<Pair<Int, Int>>, Boolean> {
        if (corporation.visibleGarbage.any {
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
                .map { it.value.first }.toList()
            if (out.isEmpty()) out = listOf(ship.location)
            return Pair(out, false)
        }
        return Pair(listOf(ship.location), false)
    }

    /**
     * Helper method for handleDefaultState() in CorporationManager. Handles the state of COORDINATING ships.
     */
    fun handleDefaultStateCoordinating(
        ship: Ship,
        corporation: Corporation,
        shipMaxTravelDistance: Int
    ): Pair<List<Pair<Int, Int>>, Boolean> {
        val shipsToBeConsideredInVisibility = corporation.visibleShips
            .filter {
                it.value.first != corporation.lastCooperatedWith &&
                    it.value.first != corporation.lastCooperatedWith &&
                    simData.navigationManager.traversablePathExists(ship.location, it.value.second)
            }
        if (shipsToBeConsideredInVisibility.isNotEmpty()) {
            return Pair(shipsToBeConsideredInVisibility.map { it.value.second }.toList(), false)
        }
        return Pair(listOf(simData.navigationManager.getExplorePoint(ship.location, shipMaxTravelDistance)), true)
    }

    /**
     * Helper method for handleDefaultState() in CorporationManager. Handles the state of SCOUTING ships.
     */
    fun handleDefaultStateScouting(
        ship: Ship,
        corporation: Corporation,
        shipMaxTravelDistance: Int
    ): Pair<List<Pair<Int, Int>>, Boolean> {
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
                }.map { it.value.first }.toList(),
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
                }.map { it.value.first }.toList(),
                false
            )
        }
        return Pair(
            listOf(simData.navigationManager.getExplorePoint(ship.location, shipMaxTravelDistance)),
            true
        )
    }

    private fun getOnlyAssignableGarbagePredicate(garbageID: Int): Boolean {
        val garbageObject = simData.garbage.find { it.id == garbageID }
        val garbageAssignedCapacity = garbageObject?.assignedCapacity
        val garbageAmount = garbageObject?.amount
        val assignableCapacity = garbageAmount?.minus(garbageAssignedCapacity ?: 0)
        return assignableCapacity == null || assignableCapacity > 0
    }
}
