package de.unisaarland.cs.se.selab.assets

/**
 * Abstract class for the events.
 */
abstract class Event(
    open val id: Int,
    open val tick: Int,
)

/**
 * Class representing Pirate Attack Events.
 */
data class PirateAttackEvent(
    override val id: Int,
    override val tick: Int,
    private val shipID: Int
) : Event(id, tick)

/**
 * Class representing Restriction Events.
 */
data class RestrictionEvent(
    override val id: Int,
    override val tick: Int,
    private val location: Pair<Int, Int>,
    private val radius: Int,
    private val duration: Int
) : Event(id, tick)

/**
 * Class representing Oil Spill Events.
 */
data class OilSpillEvent(
    override val id: Int,
    override val tick: Int,
    private val location: Pair<Int, Int>,
    private val radius: Int,
    private val duration: Int
) : Event(id, tick)

/**
 * Class representing Storm Events.
 */
data class StormEvent(
    override val id: Int,
    override val tick: Int,
    private val location: Pair<Int, Int>,
    private val radius: Int,
    private val duration: Int,
    private val direction: Direction
) : Event(id, tick)
