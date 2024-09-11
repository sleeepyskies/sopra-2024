package de.unisaarland.cs.se.selab.parsing

import de.unisaarland.cs.se.selab.Simulator
import de.unisaarland.cs.se.selab.corporations.CorporationManager
import de.unisaarland.cs.se.selab.events.EventManager
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import de.unisaarland.cs.se.selab.tasks.TaskManager
import de.unisaarland.cs.se.selab.travelling.TravelManager

/**
 * SimulationParser receives a map, corporation and scenario file as well as a max tick.
 * It is responsible for instantiating the Map, Corporation and Scenario Parsers, as
 * well as cross validating these results. Returns a Simulator.
 */
class SimulationParser(
    private val mapFile: String,
    private val corporationFile: String,
    private val scenarioFile: String,
    private val maxTick: Int,
) {
    val mapParser = MapParser()
    val corporationParser = CorporationParser()
    val scenarioParser = ScenarioParser()
    val travelManager = TravelManager()
    val navigationManager = NavigationManager()
    val eventManager = EventManager()
    val corporationManager = CorporationManager()
    val taskManager = TaskManager()

    /**
     * Creates and returns an instantiated Simulator
     */
    public fun createSimulator(): Simulator {
        TODO()
    }

    /**
     * Creates and returns an instantiated TravelManager
     */
    public fun createTravelManager(): TravelManager {
        TODO()
    }

    /**
     * Creates and returns an instantiated EventManager
     */
    public fun createEventManager(): EventManager {
        TODO()
    }

    /**
     * Creates and returns an instantiated CorporationManager
     */
    public fun createCorporationManager(): CorporationManager {
        TODO()
    }

    /**
     * Checks that harbors only occur on shore tiles.
     */
    public fun crossValidateHarborsOnShores(): Boolean {
        TODO()
    }

    /**
     * Checks that ships initial locations are only on valid tiles.
     */
    public fun crossValidateShipsOnTiles(): Boolean {
        TODO()
    }

    /**
     * Checks that garbage only occurs on valid tiles.
     */
    public fun crossValidateGarbageOnTiles(): Boolean {
        TODO()
    }

    /**
     * Checks that events only occur on valid tiles.
     */
    public fun crossValidateEventsOnTiles(): Boolean {
        TODO()
    }

    /**
     * Checks that all PirateShipAttack Events affect valid ships.
     */
    public fun crossValidateEventsOnShips(): Boolean {
        TODO()
    }

    /**
     * Checks that all tasks have been correctly assigned to ships.
     */
    public fun crossValidateTasksForShips(): Boolean {
        TODO()
    }
}
