package de.unisaarland.cs.se.selab.events

import de.unisaarland.cs.se.selab.assets.OilSpillEvent
import de.unisaarland.cs.se.selab.assets.PirateAttackEvent
import de.unisaarland.cs.se.selab.assets.RestrictionEvent
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.StormEvent

/**
 * Manages the events in the simulation.
 *
 * @property simData The simulation data used to manage events.
 */
class EventManager(private val simData: SimulationData) {

    /**
     * Starts the event phase in the simulation.
     */
    fun startEventPhase() {
        TODO()
    }

    /**
     * Applies a restriction event to the simulation.
     *
     * @param restriction The restriction event to be applied.
     */
    fun applyRestriction(restriction: RestrictionEvent) {
        TODO()
    }

    /**
     * Applies a pirate attack event to the simulation.
     *
     * @param attack The pirate attack event to be applied.
     */
    fun applyPirateAttack(attack: PirateAttackEvent) {
        TODO()
    }

    /**
     * Applies an oil spill event to the simulation.
     *
     * @param oilSpill The oil spill event to be applied.
     */
    fun applyOilSpillEvent(oilSpill: OilSpillEvent) {
        TODO()
    }

    /**
     * Applies a storm event to the simulation.
     *
     * @param storm The storm event to be applied.
     */
    fun applyStormEvent(storm: StormEvent) {
        TODO()
    }

    /**
     * Reverses a restriction event in the simulation.
     *
     * @param restriction The restriction event to be reversed.
     */
    fun reverseRestrictedEvent(restriction: RestrictionEvent) {
        TODO()
    }
}
