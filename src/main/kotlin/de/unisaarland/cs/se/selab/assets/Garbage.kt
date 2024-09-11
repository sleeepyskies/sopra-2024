package de.unisaarland.cs.se.selab.assets

class Garbage(
    val id : Int,
    var amount : Int,
    val type: GarbageType,
    var tileId : Int,
    val location : Pair<Int, Int>,
    var assignedCapacity : Int = 0,
    var trackedBy : List<Int> = listOf()
) {
}