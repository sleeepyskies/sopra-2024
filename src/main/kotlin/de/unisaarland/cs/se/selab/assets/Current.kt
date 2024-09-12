package de.unisaarland.cs.se.selab.assets

/**
 * Data class representing the present current on a tile.
 */
data class Current(
    val direction: Direction,
    val intensity: Int,
    val speed: Int
)
