package de.unisaarland.cs.se.selab.parsing

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.Simulator
import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Event
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.Reward
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Task
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
    // data
    private lateinit var corporations: List<Corporation>
    private lateinit var ships: List<Ship>
    private lateinit var events: MutableMap<Int, List<Event>>
    private lateinit var garbage: MutableList<Garbage>
    private lateinit var rewards: MutableList<Reward>
    private lateinit var tasks: MutableMap<Int, List<Task>>

    // Managers
    private lateinit var travelManager: TravelManager
    private lateinit var eventManager: EventManager
    private lateinit var corporationManager: CorporationManager
    private lateinit var taskManager: TaskManager
    private lateinit var navigationManager: NavigationManager

    /**
     * Creates and returns an instantiated Simulator
     */
    fun createSimulator(): Simulator? {
        // init map parser
        val mapParser = MapParser(mapFile)

        // parse and validate map
        if (mapParser.parseMap()) {
            // file valid
            Logger.initInfo(mapFile)
            this.navigationManager = mapParser.getNavManager()
        } else {
            // file invalid
            Logger.initInfoInvalid(mapFile)
            return null
        }

        // get the idLocationMapping from MapParser
        val idLocationMapping = mapParser.idLocationMapping

        // init corporation parser and scenario parser
        val corporationParser = CorporationParser(corporationFile, idLocationMapping)
        val scenarioParser = ScenarioParser(scenarioFile, idLocationMapping)

        // parse and validate corporations
        if (corporationParser.parseAllCorporations()) {
            // file valid
            this.corporations = corporationParser.corporations
            this.ships = corporationParser.ships
        } else {
            // file invalid
            Logger.initInfoInvalid(corporationFile)
            return null
        }

        // parse and validate scenario
        if (scenarioParser.parseScenario()) {
            // file valid
            this.garbage = scenarioParser.garbage
            this.rewards = scenarioParser.rewards
            // convert value from MutableList to List
            this.events = scenarioParser.events.mapValues { entry -> entry.value.toList() }.toMutableMap()
            this.tasks = scenarioParser.tasks.mapValues { entry -> entry.value.toList() }.toMutableMap()
        } else {
            // file invalid
            Logger.initInfoInvalid(scenarioFile)
            return null
        }

        // return final simulator
        return if (crossValidate()) {
            placeGarbageOnTiles(scenarioParser.tileXYtoGarbage)
            makeManagers(scenarioParser.highestGarbageID)
            // log
            Logger.initInfo(this.corporationFile)
            Logger.initInfo(this.scenarioFile)
            Simulator(maxTick, travelManager, corporationManager, eventManager, taskManager)
        } else {
            null
        }
    }

    /**
     * Places all garbage on their correct tile.
     */
    private fun placeGarbageOnTiles(tileXYtoGarbage: Map<Pair<Int, Int>, List<Garbage>>) {
        for ((loc, tile) in this.navigationManager.tiles) {
            // get relevant garbage
            val garbage = tileXYtoGarbage[loc]

            // place on tile
            if (garbage != null) {
                tile.currentGarbage.addAll(garbage)
            }
        }
    }

    /**
     * Creates and sets all managers as well as the sim data.
     */
    private fun makeManagers(highestID: Int) {
        // make sim data
        val simData = SimulationData(
            navigationManager,
            corporations,
            ships,
            garbage,
            mutableListOf(),
            events,
            tasks,
            mutableListOf(),
            rewards,
            highestID
        )

        this.travelManager = TravelManager(simData)
        this.eventManager = EventManager(simData)
        this.corporationManager = CorporationManager(simData)
        this.taskManager = TaskManager(simData)
    }

    /**
     * Checks that harbors only occur on shore tiles.
     */
    private fun crossValidateHarborsOnShores(): Boolean {
        TODO()
    }

    /**
     * Checks that ships initial locations are only on valid tiles.
     */
    private fun crossValidateShipsOnTiles(): Boolean {
        TODO()
    }

    /**
     * Checks that garbage only occurs on valid tiles.
     */
    private fun crossValidateGarbageOnTiles(): Boolean {
        TODO()
    }

    /**
     * Checks that events only occur on valid tiles.
     */
    private fun crossValidateEventsOnTiles(): Boolean {
        TODO()
    }

    /**
     * Checks that all PirateShipAttack Events affect valid ships.
     */
    private fun crossValidateEventsOnShips(): Boolean {
        TODO()
    }

    /**
     * Checks that all tasks have been correctly assigned to ships.
     */
    private fun crossValidateTasksForShips(): Boolean {
        TODO()
    }

    /**
     * Calls all cross validation methods
     */
    private fun crossValidate(): Boolean {
        return crossValidateHarborsOnShores() &&
            crossValidateShipsOnTiles() &&
            crossValidateGarbageOnTiles() &&
            crossValidateEventsOnTiles() &&
            crossValidateEventsOnShips() &&
            crossValidateTasksForShips()
    }
}
