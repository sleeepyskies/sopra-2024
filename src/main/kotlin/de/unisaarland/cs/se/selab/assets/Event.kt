package de.unisaarland.cs.se.selab.assets

/**
 * Enum class for the event types.
 */
enum class EventType {
    PIRATE_ATTACK,
    RESTRICTION,
    OIL_SPILL,
    STORM,
    NONE
}

/**
 * Abstract class for the events.
 */
abstract class Event(
    open val id: Int,
    open val tick: Int
) {
    open val type: EventType = EventType.NONE
}

/**
 * Class representing Pirate Attack Events.
 */
data class PirateAttackEvent(
    override val id: Int,
    override val tick: Int,
    val shipID: Int,
) : Event(id, tick) {
    override val type: EventType = EventType.PIRATE_ATTACK
}

/**
 * Class representing Restriction Events.
 */
data class RestrictionEvent(
    override val id: Int,
    override val tick: Int,
    val location: Pair<Int, Int>,
    val radius: Int,
    var duration: Int
) : Event(id, tick) {
    override val type: EventType = EventType.RESTRICTION
}

/**
 * Class representing Oil Spill Events.
 */
data class OilSpillEvent(
    override val id: Int,
    override val tick: Int,
    val location: Pair<Int, Int>,
    val radius: Int,
    val amount: Int
) : Event(id, tick) {
    override val type: EventType = EventType.OIL_SPILL
}

/**
 * Class representing Storm Events.
 */
data class StormEvent(
    override val id: Int,
    override val tick: Int,
    val location: Pair<Int, Int>,
    val radius: Int,
    val direction: Direction,
    val speed: Int
) : Event(id, tick) {
    override val type: EventType = EventType.STORM
}
