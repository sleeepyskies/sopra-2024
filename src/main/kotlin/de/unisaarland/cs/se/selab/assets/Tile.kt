package de.unisaarland.cs.se.selab.assets

/**
 * The Tile class
 */
class Tile(
    val id: Int,
    val location: Pair<Int, Int>,
    val type: TileType,
    val isHarbor: Boolean,
    val current: Current,
    val hasCurrent: Boolean,
    val oilMaxCapacity: Int = 1000,
) {
    var isRestricted: Boolean = false
    private val arrivingGarbage: MutableList<Garbage> = mutableListOf()
    private val currentGarbage: MutableList<Garbage> = mutableListOf()
    private var currentOilAmount: Int = 0
    private var currentChemicalAmount: Int = 0
    private var currentPlasticAmount: Int = 0
    private var neighbors: MutableMap<Direction, Tile> = mutableMapOf()

    /**
     * @return the neighbors of a tile
     */
    fun getNeighbors(): List<Tile> {
        return neighbors.values.toList()
    }

    /**
     * Gets a garbageList of the tile in correct sorting order
     * @return the garbageList of the tile in sorted order
     */
    fun getGarbageByLowestID(): List<Garbage> {
        val garbageList = currentGarbage.sortedBy { it.id }
        return garbageList
    }

    /**
     * Checks if the amount of garbage can fit on the tile
     * @param garbage: the garbage to be checked
     * @return true if the garbage can fit, false otherwise
     */
    fun canGarbageFitOnTile(garbage: Garbage): Boolean {
        when (garbage.type) {
            GarbageType.OIL -> return currentOilAmount + garbage.amount <= oilMaxCapacity
            GarbageType.CHEMICALS -> return currentChemicalAmount + garbage.amount <= oilMaxCapacity
            GarbageType.PLASTIC -> return currentPlasticAmount + garbage.amount <= oilMaxCapacity
            GarbageType.NONE -> return false
        }
    }

    /**
     * Adds garbage to the tile(For when constructing the map,
     * not to be used for anything that happens within a
     * tick as that would have to go to arrivingGarbage)
     * @param garbage the garbage to be added
     */
    fun addGarbageToTile(garbage: Garbage) {
        when (garbage.type) {
            GarbageType.OIL -> currentOilAmount += garbage.amount
            GarbageType.CHEMICALS -> currentChemicalAmount += garbage.amount
            GarbageType.PLASTIC -> currentPlasticAmount += garbage.amount
            GarbageType.NONE -> return
        }
        currentGarbage.add(garbage)
    }

    /**
     * Removes garbage from the tile (Should only be used for garbage that is on
     * the tile during the tick used, not in arrivingGarbage, as the arrivingGarbage isnt yet
     * counted to the total)
     * @param garbage the garbage to be removed
     */
    fun removeGarbageFromTile(garbage: Garbage) {
        when (garbage.type) {
            GarbageType.OIL -> currentOilAmount -= garbage.amount
            GarbageType.CHEMICALS -> currentChemicalAmount -= garbage.amount
            GarbageType.PLASTIC -> currentPlasticAmount -= garbage.amount
            GarbageType.NONE -> return
        }
        currentGarbage.remove(garbage)
    }

    /**
     * Adds garbage to arriving Garbage(For when drifting)
     * @param garbage the garbage to be removed
     */
    fun addArrivingGarbageToTile(garbage: Garbage) {
        when (garbage.type) {
            GarbageType.OIL -> currentOilAmount += garbage.amount
            GarbageType.CHEMICALS -> currentChemicalAmount += garbage.amount
            GarbageType.PLASTIC -> currentPlasticAmount += garbage.amount
            GarbageType.NONE -> return
        }
        arrivingGarbage.add(garbage)
    }

    /**
     * Moves all arriving Garbage to the tile (should be called after drifting has happened)
     */
    fun moveAllArrivingGarbageToTile() {
        for (garbage in arrivingGarbage) {
            currentGarbage.add(garbage)
        }
        arrivingGarbage.clear()
    }

    /**
     * Checks if there is still garbage left on the tile (used as loop invariant,
     * we need to respect the currentAmounts,
     * so use the Tile functions)
     */
    fun checkGarbageLeft(): Boolean {
        return currentOilAmount > 0 || currentChemicalAmount > 0 || currentPlasticAmount > 0
    }

    /**
     * Sets the amount for a specific garbage indexed by id
     * @param garbageID the id of the garbage
     * @param amount the amount to be set
     * @return the garbage with the new amount
     */
    fun setAmountOfGarbage(garbageID: Int, amount: Int) {
        for (garbage in currentGarbage) {
            if (garbage.id == garbageID) {
                garbage.amount = amount
            }
        }
    }
}
