package de.unisaarland.cs.se.selab.assets

/**
 * Represents garbage in the simulation.
 *
 * @property id The unique identifier of the garbage.
 * @property amount The amount of garbage.
 * @property type The type of garbage.
 * @property tileId The identifier of the tile where the garbage is located.
 * @property location The location of the garbage as a pair of coordinates.
 * @property assignedCapacity The capacity assigned to the garbage.
 * @property trackedBy The list of IDs tracking the garbage.
 */
data class Garbage(
    val id: Int,
    var amount: Int,
    val type: GarbageType,
    var tileId: Int,
    var location: Pair<Int, Int>,
    var assignedCapacity: Int = 0,
    var trackedBy: MutableList<Int> = mutableListOf()
) {

    /**
     * Collects a specified amount of garbage and determines if it should be removed.
     *
     * @param amt The amount of garbage to collect.
     * @return True if the garbage should be removed, false otherwise.
     */
    fun collectAndShouldBeRemoved(amt: Int): Boolean {
        amount -= amt
        return amount <= 0
    }

    /**
     * Checks if the garbage should be split based on the intensity.
     *
     * @param intensity The intensity to check against.
     * @return True if the garbage should be split, false otherwise.
     */
    fun checkSplit(driftCapacity: Int): Boolean {
        return when (type) {
            GarbageType.OIL, GarbageType.PLASTIC -> amount > driftCapacity
            else -> false
        }
    }
}
