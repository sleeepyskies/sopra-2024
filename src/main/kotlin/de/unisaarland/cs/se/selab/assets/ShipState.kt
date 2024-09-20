package de.unisaarland.cs.se.selab.assets

/**
 * Enum representing the different states a ship can be in during the simulation.
 */
enum class ShipState {
    /**
     * The ship needs both refueling and unloading.
     */
    NEED_REFUELING_AND_UNLOADING,

    /**
     * The ship needs refueling.
     */
    NEED_REFUELING,

    /**
     * The ship needs unloading.
     */
    NEED_UNLOADING,

    /**
     * The ship is waiting for plastic.
     */
    WAITING_FOR_PLASTIC,

    /**
     * The ship is currently tasked with a mission.
     */
    TASKED,
    IS_COOPERATING,

    REFUELING,
    UNLOADING,
    REFUELING_AND_UNLOADING,

    /**
     * The default state of the ship.
     */
    DEFAULT
}
