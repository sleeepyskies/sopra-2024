package de.unisaarland.cs.se.selab.assets

/**
 * Represents a ship in the simulation.
 *
 * @property id The unique identifier of the ship.
 * @property name The name of the ship.
 * @property corporation The identifier of the corporation that owns the ship.
 * @property capacityInfo A map containing the garbage type and its capacity information (current and maximum).
 * @property visibilityRange The visibility range of the ship.
 * @property location The current location of the ship as a pair of coordinates (x, y).
 * @property direction The current direction the ship is facing.
 * @property tileId The identifier of the tile the ship is currently on.
 * @property maxVelocity The maximum velocity the ship can achieve.
 * @property currentVelocity The current velocity of the ship.
 * @property acceleration The acceleration rate of the ship.
 * @property maxFuelCapacity The maximum fuel capacity of the ship.
 * @property fuelConsumptionRate The rate at which the ship consumes fuel.
 * @property currentFuel The current amount of fuel the ship has.
 * @property currentTaskId The identifier of the current task assigned to the ship. Default is -1 (no task).
 * @property state The current state of the ship.
 * @property type The type of the ship.
 * @property hasRadio Indicates whether the ship is equipped with a radio.
 * @property hasTracker Indicates whether the ship is equipped with a tracker.
 * @property travelingToHarbor Indicates whether the ship is traveling to the harbor.
 */
data class Ship(
    val id: Int,
    val name: String,
    val corporation: Int,
    var capacityInfo: MutableMap<GarbageType, Pair<Int, Int>>,
    var visibilityRange: Int,
    var location: Pair<Int, Int>,
    var direction: Direction,
    var tileId: Int,
    val maxVelocity: Int,
    var currentVelocity: Int,
    var acceleration: Int,
    val maxFuelCapacity: Int,
    val fuelConsumptionRate: Int,
    var currentFuel: Int,
    var currentTaskId: Int = -1,
    var state: ShipState = ShipState.DEFAULT,
    val type: ShipType,
    var hasRadio: Boolean = false,
    var hasTracker: Boolean = false,
    var travelingToHarbor: Boolean = false
) {
    /**
     * Checks if the ship needs refueling or unloading.
     *
     * @return True if the ship needs refueling or unloading, false otherwise.
     */
    fun checkRefuelUnload(): Boolean {
        return state == ShipState.NEED_REFUELING || state == ShipState.NEED_UNLOADING ||
            state == ShipState.NEED_REFUELING_AND_UNLOADING
    }

    /**
     * Updates the current velocity of the ship by adding the acceleration,
     * ensuring it does not exceed the maximum velocity.
     */
    fun updateVelocity() {
        (currentVelocity + acceleration).coerceAtMost(maxVelocity)
    }

    /**
     * Moves the ship to a new tile.
     *
     * @param tid The identifier of the new tile.
     * @param tloc The new location as a pair of coordinates (x, y).
     * @param amount The distance traveled to the new tile.
     */
    fun move(tid: Int, tloc: Pair<Int, Int>, amount: Int) {
        tileId = tid
        location = tloc
        currentFuel = (currentFuel - fuelConsumptionRate * amount).coerceAtLeast(0)
    }

    /**
     * Refuels the ship to its maximum fuel capacity.
     */
    fun refuel() {
        currentFuel = maxFuelCapacity
    }

    /**
     * Unloads the ship by setting the capacity information to pairs of (b, b).
     */
    fun unload(): Map<GarbageType, Int> {
        var unloadedMap = mutableMapOf<GarbageType, Int>()
        unloadedMap[GarbageType.PLASTIC] = 0
        unloadedMap[GarbageType.OIL] = 0
        unloadedMap[GarbageType.CHEMICALS] = 0
        capacityInfo.forEach { (t, v) -> unloadedMap[t] = unloadedMap[t]!! + v.first }
        capacityInfo = capacityInfo.mapValues { (_, v) -> Pair(v.second, v.second) }.toMutableMap()
        return unloadedMap
    }

    /**
     * Checks the current capacity of a specific garbage type.
     *
     * @param garbageType The type of garbage to check.
     * @return The current capacity of the specified garbage type.
     */
    fun checkCapacity(garbageType: GarbageType): Int {
        return capacityInfo[garbageType]?.first ?: 0
    }

    /**
     * Collects a specified amount of garbage, updating the capacity information.
     *
     * @param garbageType The type of garbage to collect.
     * @param amount The amount of garbage to collect.
     */
    fun collect(garbageType: GarbageType, amount: Int) {
        val (current, max) = capacityInfo[garbageType] ?: return
        capacityInfo[garbageType] = Pair((current + amount).coerceAtMost(max), max)
        if (current + amount >= max) {
            state = if (state == ShipState.NEED_REFUELING) {
                ShipState.NEED_REFUELING_AND_UNLOADING
            } else {
                ShipState.NEED_UNLOADING
            }
        }
    }
}
