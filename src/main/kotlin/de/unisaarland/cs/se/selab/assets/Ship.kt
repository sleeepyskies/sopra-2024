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
 * @property driftedLocation The drifted location of the ship as a pair of coordinates (x, y).
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
 */
data class Ship(
    val id: Int,
    val name: String,
    val corporation: Int,
    var capacityInfo: Map<GarbageType, Pair<Int, Int>>,
    var visibilityRange: Int,
    var location: Pair<Int, Int>,
    var driftedLocation: Pair<Int, Int>,
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
    var hasTracker: Boolean = false
)
