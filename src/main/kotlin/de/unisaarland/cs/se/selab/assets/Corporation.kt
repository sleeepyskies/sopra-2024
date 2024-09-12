package de.unisaarland.cs.se.selab.assets

class Corporation(
    private val name: String,
    private val id: Int,
    private val harbors: List<Pair<Int, Int>>,
    private val ships: List<Ship>,
    private val collectableGarbageTypes: List<GarbageType>,
    ) {
    private val garbage: Map<Int, Pair<Int, Int>> = emptyMap()
    private val visibleGarbage: Map<Int, Pair<Int, Int>> = emptyMap()
    private val knownHarbors: List<Pair<Int, Int>> = emptyList()
    private val knownShips: Map<Int, Pair<Int, Pair<Int, Int>>> = emptyMap()
    private val lastCooperatedWith: Int = -1
}