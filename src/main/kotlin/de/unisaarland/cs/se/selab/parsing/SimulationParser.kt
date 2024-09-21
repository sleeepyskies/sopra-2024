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
    }

    // debug logger
    // private val log: Log = LogFactory.getLog("debugger")

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
        val mapParser = MapParser(this.mapFile)

        // parse and validate map
        if (mapParser.parseMap()) {
            // file valid
            Logger.initInfo(this.mapFile)
            this.navigationManager = mapParser.getNavManager()
            this.navigationManager.initializeAndUpdateGraphStructure()
        } else {
            // file invalid
            Logger.initInfoInvalid(this.mapFile)
            return null
        }

        // get the idLocationMapping from MapParser
        val idLocationMapping = mapParser.idLocationMapping

        // init corporation parser and scenario parser
        val corporationParser = CorporationParser(this.corporationFile, idLocationMapping)
        val scenarioParser = ScenarioParser(this.scenarioFile, idLocationMapping)
        // parse and validate corporations
        if (corporationParser.parseAllCorporations()) {
            // file valid
            this.corporations = corporationParser.corporations.sortedBy { it.id }
            this.ships = corporationParser.ships.sortedBy { it.id }
        } else {
            // file invalid
            Logger.initInfoInvalid(this.corporationFile)
            return null
        }

        if (crossValidateCorporations()) {
            Logger.setCorporationsInitialCollectedGarbage(corporations.map { it.id })
            Logger.initInfo(this.corporationFile)
        } else {
            Logger.initInfoInvalid(this.corporationFile)
            return null
        }

        // parse and validate scenario
        if (scenarioParser.parseScenario()) {
            // file valid
            this.garbage = scenarioParser.garbage.sortedBy { it.id }
            this.rewards = scenarioParser.rewards.sortedBy { it.id }
            // convert value from MutableList to List
            this.events = scenarioParser.events.mapValues { entry -> entry.value.sortedBy { it.id }.toList() }.toMutableMap()
            this.tasks = scenarioParser.tasks.mapValues { entry -> entry.value.sortedBy { it.id }.toList() }.toMutableMap()
        } else {
            // file invalid
            Logger.initInfoInvalid(this.scenarioFile)
            return null
        }

        placeGarbageOnTiles(scenarioParser.tileXYtoGarbage)

        // return final simulator
        return if (crossValidateScenario()) {
            makeManagers(scenarioParser.highestGarbageID)
            // log
            Logger.initInfo(this.scenarioFile)
            Simulator(maxTick, travelManager, corporationManager, eventManager, taskManager)
        } else {
            Logger.initInfoInvalid(this.scenarioFile)
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
                for (garb in garbage) {
                    tile.addGarbageToTile(garb)
                }
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
     * Checks that harbors only occur on shore tiles, as well
     * as non-null tiles, and that those tiles have "harbor: true"
     */
    private fun crossValidateCorporationHarborOnHarborTile(): Boolean {
        // get all harbor locations
        val locations = this.corporations.flatMap { it.harbors }

        for (location in locations) {
            // get tile
            val tile = navigationManager.tiles[location]

            // check tile non-null, is SHORE and is a harbor
            if (tile == null || !tile.isHarbor || tile.type != TileType.SHORE) {
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
                return false
            }
        }
        return true
    }

    /**
     * Checks that garbage only occurs on valid tiles, no tile has more than
     * 1000 OIL and that DEEP_OCEAN tiles have no chemicals on them initially.
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
        if ((tileOilMap.values.maxOrNull() ?: 0) > THOUSAND) {
            return false
        }

        // check no DEEP_OCEAN has any chemicals at all
        for (tile in this.navigationManager.tiles.values.filter { it.type == TileType.DEEP_OCEAN }) {
            if (tile.currentGarbage.any { it.type == GarbageType.CHEMICALS }) {
                return false
            }
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
                return false
            }
        }

        // check oilSpillEvents
        for (event in oilSpillEvents) {
            val tile = this.navigationManager.tiles[event.location]
            if (tile == null || tile.type == TileType.LAND) {
                return false
            }
        }

        // check stormEvents
        for (event in stormEvents) {
            val tile = this.navigationManager.tiles[event.location]
            if (tile == null || tile.type == TileType.LAND) {
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
            if (this.ships.none { it.id == attack.shipID }) {
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
                return false
            }

            // check reward ship
            if (!this.ships.any { it.id == task.rewardShip }) {
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
            if (rewardShip == null || assignedShip == null || rewardShip.corporation != assignedShip.corporation) {
                return false
            }
        }
        return true
    }

    private fun crossValidateAllHarborsOnMapBelongToACorporation(): Boolean {
        val allHarborsOfCorporations = this.corporations.flatMap { it.harbors }.toSet()
        val allTilesWithHarbor = this.navigationManager.tiles.values.filter { it.isHarbor }
        for (harbor in allTilesWithHarbor) {
            val tile = allHarborsOfCorporations.find { it == harbor.location }
            if (tile == null) {
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
                return false
            }

            when (task.type) {
                TaskType.EXPLORE, TaskType.FIND, TaskType.COLLECT -> {
                    if (tileDest.type == TileType.LAND) {
                        return false
                    }
                }
                TaskType.COORDINATE -> {
                    if (!tileDest.isHarbor) {
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
    private fun crossValidateCorporations(): Boolean {
        return crossValidateCorporationHarborOnHarborTile() &&
            crossValidateShipsOnTiles() &&
            crossValidateShipsCanReachHarbor() &&
            crossValidateAllHarborsOnMapBelongToACorporation()
    }

    /**
     * Calls all cross validation methods
     */
    private fun crossValidateScenario(): Boolean {
        return crossValidateGarbageOnTiles() &&
            crossValidateEventsOnTiles() &&
            crossValidateEventsOnShips() &&
            crossValidateTasksForShips() &&
            crossValidateTasksSameShipCorporation() &&
            crossValidateTaskLocation() &&
            crossValidateRewardAssignedIds()
        // crossValidateRewardGarbageTypeOnTile()
    }

    // commented out since we do not need this check
    // https://forum.se.cs.uni-saarland.de:51443/t/no-garbage-on-task-tile/545

    /**
     private fun crossValidateRewardGarbageTypeOnTile(): Boolean {
     // get all reward initial locations
     for (task in this.tasks.flatMap { it.value }.filter { it.type == TaskType.COLLECT }) {
     // get tile id of the task
     val tileId = task.targetTileId
     // get tile
     val tile = this.navigationManager.findTile(tileId)
     val reward = this.rewards.find { it.id == task.rewardId }
     if (!isValidRewardOnTile(reward, tile)) {
     return false
     }
     }
     return true
     }

     private fun isValidRewardOnTile(reward: Reward?, tile: Tile?): Boolean {
     return when (reward?.garbageType) {
     GarbageType.PLASTIC -> isValidPlasticReward(tile)
     GarbageType.OIL -> isValidOilReward(tile)
     GarbageType.CHEMICALS -> isValidChemicalsReward(tile)
     GarbageType.NONE, null -> false
     }
     }

     private fun isValidPlasticReward(tile: Tile?): Boolean {
     if (tile != null && tile.currentGarbage.count { it.type == GarbageType.PLASTIC } == 0) {
     log.error("SIMULATION PARSER: A reward has an invalid initial tile, there is no plastic on this tile.")
     return false
     }
     return true
     }

     private fun isValidOilReward(tile: Tile?): Boolean {
     if (tile != null && tile.currentGarbage.count { it.type == GarbageType.OIL } == 0) {
     println(tile.id)
     println(tile.currentGarbage)
     log.error("SIMULATION PARSER: A reward has an invalid initial tile, there is no oil on this tile.")
     return false
     }
     return true
     }

     private fun isValidChemicalsReward(tile: Tile?): Boolean {
     if (tile != null && tile.currentGarbage.count { it.type == GarbageType.CHEMICALS } == 0) {
     log.error("SIMULATION PARSER: A reward has an invalid initial tile, there is no chemicals on this tile.")
     return false
     }
     return true
     }
     **/
}
