package de.unisaarland.cs.se.selab.parsing

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.Simulator
import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Event
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.OilSpillEvent
import de.unisaarland.cs.se.selab.assets.PirateAttackEvent
import de.unisaarland.cs.se.selab.assets.RestrictionEvent
import de.unisaarland.cs.se.selab.assets.Reward
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.StormEvent
import de.unisaarland.cs.se.selab.assets.Task
import de.unisaarland.cs.se.selab.assets.TaskType
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.corporations.CorporationManager
import de.unisaarland.cs.se.selab.events.EventManager
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import de.unisaarland.cs.se.selab.tasks.TaskManager
import de.unisaarland.cs.se.selab.travelling.TravelManager
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

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
    /**
     * Companion object containing all constants used in SimulationParser
     */
    companion object {
        const val THOUSAND = 1000
        const val EVENT_LAND = "SIMULATION PARSER: An event occurs on a land tile."
        const val TASK_INVALID = "SIMULATION PARSER: A task has an invalid location."
    }

    // debug logger
    private val log: Log = LogFactory.getLog("debugger")

    // data
    private lateinit var corporations: List<Corporation>
    private lateinit var ships: List<Ship>
    private lateinit var events: Map<Int, List<Event>>
    private lateinit var garbage: List<Garbage>
    private lateinit var rewards: List<Reward>
    private lateinit var tasks: Map<Int, List<Task>>

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
            ships.toMutableList(),
            garbage.toMutableList(),
            mutableListOf(),
            events.toMutableMap(),
            tasks.toMutableMap(),
            mutableListOf(),
            rewards.toMutableList(),
            highestID
        )

        this.travelManager = TravelManager(simData)
        this.eventManager = EventManager(simData)
        this.corporationManager = CorporationManager(simData)
        this.taskManager = TaskManager(simData)
    }

    /**
     * Checks that harbors only occur on shore tiles, as well as non-null tiles.
     */
    private fun crossValidateHarborsOnShores(): Boolean {
        // get all harbor locations
        val locations = this.corporations.flatMap { it.harbors }

        for (location in locations) {
            // get tile
            val tile = navigationManager.tiles[location]

            // check tile non-null, is SHORE and is a harbor
            if (tile == null || !tile.isHarbor || tile.type != TileType.SHORE) {
                log.error("SIMULATION PARSER: A corporation has a harbor on an invalid tile.")
                return false
            }
        }
        return true
    }

    /**
     * Checks that ships initial locations are only on valid tiles.
     */
    private fun crossValidateShipsOnTiles(): Boolean {
        // get all ship initial locations
        val locations = this.ships.map { it.location }

        for (location in locations) {
            // get tile
            val tile = navigationManager.tiles[location]

            // check tile non-null and is not LAND
            if (tile == null || tile.type == TileType.LAND) {
                log.error("SIMULATION PARSER: A ship has an invalid initial tile.")
                return false
            }
        }
        return true
    }

    /**
     * Checks that garbage only occurs on valid tiles and that no tile has more than 1000 OIL
     */
    private fun crossValidateGarbageOnTiles(): Boolean {
        // get all garbage initial locations
        val locations = this.garbage.map { it.location }

        // check all garbage is on a valid tile
        for (location in locations) {
            // get tile
            val tile = navigationManager.tiles[location]

            // check tile non-null and is not LAND
            if (tile == null || tile.type == TileType.LAND) {
                log.error("SIMULATION PARSER: A garbage has an invalid initial tile.")
                return false
            }
        }

        // make map of tileID to OIL amount
        val tileOilMap = mutableMapOf<Int, Int>()
        for (gb in this.garbage) {
            if (gb.type == GarbageType.OIL) {
                val currentAmount = tileOilMap.getOrDefault(gb.tileId, 0)
                tileOilMap[gb.tileId] = currentAmount + gb.amount
            }
        }

        // check no tile has more than 1000 OIL
        if (tileOilMap.values.max() > THOUSAND) {
            log.error("SIMULATION PARSER: A garbage has an invalid initial tile.")
            return false
        }

        return true
    }

    /**
     * Checks that events only occur on valid tiles.
     */
    private fun crossValidateEventsOnTiles(): Boolean {
        // get events
        val restrictionEvents = this.events.flatMap { it.value }.filterIsInstance<RestrictionEvent>()
        val oilSpillEvents = this.events.flatMap { it.value }.filterIsInstance<OilSpillEvent>()
        val stormEvents = this.events.flatMap { it.value }.filterIsInstance<StormEvent>()

        // check restriction events
        for (event in restrictionEvents) {
            val tile = this.navigationManager.tiles[event.location]
            if (tile == null || tile.type == TileType.LAND) {
                log.error(EVENT_LAND)
                return false
            }
        }

        // check oilSpillEvents
        for (event in oilSpillEvents) {
            val tile = this.navigationManager.tiles[event.location]
            if (tile == null || tile.type == TileType.LAND) {
                log.error(EVENT_LAND)
                return false
            }
        }

        // check stormEvents
        for (event in stormEvents) {
            val tile = this.navigationManager.tiles[event.location]
            if (tile == null || tile.type == TileType.LAND) {
                log.error(EVENT_LAND)
                return false
            }
        }

        return true
    }

    /**
     * Checks that all PirateShipAttack Events affect valid ships.
     */
    private fun crossValidateEventsOnShips(): Boolean {
        // get list of all events
        val events = this.events.flatMap { it.value }

        // get only pirate attack events
        val pirateEvents = events.filterIsInstance<PirateAttackEvent>()

        // check pirates only attack real ships
        for (attack in pirateEvents) {
            if (!this.ships.any { it.id == attack.shipID }) {
                log.error("SIMULATION PARSER: A pirate attack event ${attack.id} has invalid shipID.")
                return false
            }
        }

        return true
    }

    /**
     * Checks that all tasks have been correctly assigned to assigned ships and reward ships.
     */
    private fun crossValidateTasksForShips(): Boolean {
        // get tasks
        val tasks = this.tasks.flatMap { it.value }

        for (task in tasks) {
            // check assigned ship
            if (!this.ships.any { it.id == task.assignedShipId }) {
                log.error("SIMULATION PARSER: The task ${task.id} has an invalid assigned shipID.")
                return false
            }

            // check reward ship
            if (!this.ships.any { it.id == task.rewardId }) {
                log.error("SIMULATION PARSER: The task ${task.id} has an invalid reward shipID.")
                return false
            }
        }

        return true
    }

    /**
     * Checks that all ships can reach at least one home harbor.
     */
    private fun crossValidateShipsCanReachHarbor(): Boolean {
        // iterate over all corporations
        for (corporation in this.corporations) {
            // get this corporation's ships
            val ships = corporation.ships

            // get this corporation's harbors
            val harbors = corporation.harbors

            // check that each ship can reach at least one harbor
            for (ship in ships) {
                if (!shipCanReachHarbor(ship.location, harbors)) {
                    log.error("SIMULATION PARSER: The ship ${ship.id} cannot reach any home harbors.")
                    return false
                }
            }
        }
        return true
    }

    /**
     * Checks that all tasks have an assigned ship and reward ship from the same corporation.
     */
    private fun crossValidateRewardAssignedIds(): Boolean {
        // iterate over all tasks
        for (task in this.tasks.flatMap { it.value }) {
            // get ships
            val rewardShip = this.ships.find { it.id == task.rewardShip }
            val assignedShip = this.ships.find { it.id == task.assignedShipId }
            if (rewardShip == null || assignedShip == null || rewardShip.id != assignedShip.id) {
                log.error(
                    "SIMULATION PARSER: The task ${task.id} has an " +
                        "assigned ship and a reward ship from different corporations."
                )
                return false
            }
        }
        return true
    }

    /**
     * Takes a location, and checks whether any of the homeHarbors are reachable from this point.
     */
    private fun shipCanReachHarbor(location: Pair<Int, Int>, homeHarbors: List<Pair<Int, Int>>): Boolean {
        // check if location is a homeHarbor
        if (homeHarbors.contains(location)) return true

        // check if a home harbor is reachable
        val res = this.navigationManager.shortestPathToLocations(location, homeHarbors, Int.MAX_VALUE - 1)

        // unpack location from result
        val result = res.first.first

        return result != location
    }

    /**
     * Checks that all tasks have the same ship corporation as the assigned ship and reward ship.
     */
    private fun crossValidateTasksSameShipCorporation(): Boolean {
        for (task in this.tasks.flatMap { it.value }) {
            val assignedShip = this.ships.find { it.id == task.assignedShipId }
            val rewardShip = this.ships.find { it.id == task.rewardShip }
            if (assignedShip?.corporation != rewardShip?.corporation) {
                log.error(
                    "SIMULATION PARSER: The task ${task.id} has an " +
                        "assigned ship and a reward ship from different corporations."
                )
                return false
            }
        }
        return true
    }

    /**
     * Checks that each task has a valid location.
     */
    private fun crossValidateTaskLocation(): Boolean {
        for (task in this.tasks.flatMap { it.value }) {
            val tileDest = this.navigationManager.findTile(task.targetTileId)

            if (tileDest == null) {
                log.error("SIMULATION PARSER: The task ${task.id} has a null location.")
                return false
            }

            when (task.type) {
                TaskType.COLLECT -> {
                    if (tileDest.currentGarbage.isEmpty()) {
                        log.error(TASK_INVALID)
                        return false
                    }
                }
                TaskType.EXPLORE, TaskType.FIND -> {
                    if (tileDest.type == TileType.LAND) {
                        log.error(TASK_INVALID)
                        return false
                    }
                }
                TaskType.COORDINATE -> {
                    if (!tileDest.isHarbor) {
                        log.error(TASK_INVALID)
                        return false
                    }
                }
            }
        }
        return true
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
            crossValidateTasksForShips() &&
            crossValidateShipsCanReachHarbor() &&
            crossValidateTasksSameShipCorporation() &&
            crossValidateTaskLocation() &&
            crossValidateRewardAssignedIds()
    }
}
