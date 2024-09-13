package de.unisaarland.cs.se.selab.assets

/**
 * Represents a corporation in the simulation.
 *
 * @property name The name of the corporation.
 * @property id The unique identifier of the corporation.
 * @property harbors The list of harbors associated with the corporation.
 * @property ships The list of ships owned by the corporation.
 * @property collectableGarbageTypes The types of garbage that the corporation can collect.
 * @property garbage The map of garbage collected by the corporation, keyed by garbage ID.
 * @property visibleGarbage The map of visible garbage for the corporation, keyed by garbage ID.
 * @property knownHarbors The list of harbors known to the corporation.
 * @property knownShips The map of ships known to the corporation, keyed by ship ID.
 * @property lastCooperatedWith The ID of the last corporation this corporation cooperated with.
 */
data class Corporation(
    val name: String,
    val id: Int,
    val harbors: List<Pair<Int, Int>>,
    val ships: MutableList<Ship>,
    val collectableGarbageTypes: List<GarbageType>,
) {
    val garbage: MutableMap<Int, Pair<Pair<Int, Int>, GarbageType>> = mutableMapOf()
    val visibleGarbage: MutableMap<Int, Pair<Pair<Int, Int>, GarbageType>> = mutableMapOf()
    val knownHarbors: MutableList<Pair<Int, Int>> = mutableListOf()

    // shipId, corporationId, location
    val visibleShips: MutableMap<Int, Pair<Int, Pair<Int, Int>>> = mutableMapOf()
    var lastCooperatedWith: Int = -1
}
