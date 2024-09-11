package de.unisaarland.cs.se.selab.assets

class Tile(
    val id : Int,
    val location : Pair<Int, Int>,
    val type: TileType,
    val isHarbor : Boolean,
    val current : Current,
    val hasCurrent : Boolean,
    val oilMaxCapacity : Int = 1000,
    private val arrivingGarbage: MutableList<Garbage> = mutableListOf(),
    private val currentGarbage : MutableList<Garbage> = mutableListOf(),
    var isRestricted : Boolean = false,
    private var currentOilAmount : Int = 0,
    private var currentChemicalAmount : Int = 0,
    private var currentPlasticAmount : Int = 0,
    var neighbors : MutableMap<Direction,Pair<Int,Pair<Int,Int>>> = mutableMapOf(),
) {
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
        }
        currentGarbage.remove(garbage)
    }

    /**
     * Adds garbage to arriving Garbage(For when drifting)
     * @param garbage the garbage to be removed
     */
    fun addArrivingGarbageToTile(garbage: Garbage) {
        arrivingGarbage.add(garbage)
    }
    /**
     * Moves all arriving Garbage to the tile (should be called after drifting has happened)
     */
    fun moveAllArrivingGarbageToTile() {
        for (garbage in arrivingGarbage) {
            when (garbage.type) {
                GarbageType.OIL -> currentOilAmount += garbage.amount
                GarbageType.CHEMICALS -> currentChemicalAmount += garbage.amount
                GarbageType.PLASTIC -> currentPlasticAmount += garbage.amount
            }
            currentGarbage.add(garbage)
        }
        arrivingGarbage.clear()
    }

    /**
     * Checks if there is still garbage left on the tile (used as loop invariant,
     * we need to respect the currentAmounts,
     * so use the Tile functions)
     */
    fun checkGarbageLeft() : Boolean {
        return currentOilAmount > 0 || currentChemicalAmount > 0 || currentPlasticAmount > 0
    }
}