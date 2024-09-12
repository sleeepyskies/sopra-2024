package de.unisaarland.cs.se.selab.travelling

import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.SimulationData

/**
 * Manages the travel-related operations in the simulation.
 *
 * @property simData The simulation data used to manage travel operations.
 */
class TravelManager(private val simData: SimulationData) {

    /**
     * Starts the garbage drifting phase in the simulation.
     */
    fun driftGarbagePhase() {
        TODO()
    }

    /**
     * Starts the ship drifting phase in the simulation.
     */
    fun shipDriftingPhase() {
        TODO()
    }

    /**
     * Drifts garbage to a new location.
     *
     * @param location The new location of the garbage.
     * @param garbage The garbage to be drifted.
     */
    fun driftGarbage(location: Pair<Int, Int>, garbage: Garbage) {
        TODO()
    }

    /**
     * Drifts a ship to a new location.
     *
     * @param location The new location of the ship.
     * @param ship The ship to be drifted.
     */
    fun driftShip(location: Pair<Int, Int>, ship: Ship) {
        TODO()
    }

    /**
     * Checks if there is any tile with garbage left.
     *
     * @return True if there is a tile with garbage left, false otherwise.
     */
    fun tileWithGarbageLeft(): Boolean {
        TODO()
    }

    /**
     * Checks if there is any tile with an undrifted ship left.
     *
     * @return True if there is a tile with an undrifted ship left, false otherwise.
     */
    fun tileWithUndriftedShipLeft(): Boolean {
        TODO()
    }

    /**
     * Splits the garbage into a specified amount.
     *
     * @param garbage The garbage to be split.
     * @param amount The amount to split the garbage into.
     * @return The new garbage created from the split.
     */
    fun split(garbage: Garbage, amount: Int): Garbage {
        TODO()
    }

    /**
     * Moves all arriving garbage to a tile.
     */
    fun moveAllArrivingGarbageToTile() {
        TODO()
    }

    /**
     * Retrieves the remaining garbage in the ocean.
     *
     * @return The list of remaining garbage in the ocean.
     */
    fun getRemainingGarbageInOcean(): List<Garbage> {
        TODO()
    }
}
