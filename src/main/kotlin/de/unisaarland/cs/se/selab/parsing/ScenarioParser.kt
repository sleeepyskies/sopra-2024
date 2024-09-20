package de.unisaarland.cs.se.selab.parsing

import de.unisaarland.cs.se.selab.assets.Event
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.OilSpillEvent
import de.unisaarland.cs.se.selab.assets.PirateAttackEvent
import de.unisaarland.cs.se.selab.assets.RestrictionEvent
import de.unisaarland.cs.se.selab.assets.Reward
import de.unisaarland.cs.se.selab.assets.RewardType
import de.unisaarland.cs.se.selab.assets.StormEvent
import de.unisaarland.cs.se.selab.assets.Task
import de.unisaarland.cs.se.selab.assets.TaskType
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * A parser for scenarios, which includes parsing and validating various components
 * such as events, garbage, rewards, and tasks.
 *
 * @property scenarioFilepath The path to the scenario file.
 * @property events A list of events to be parsed.
 * @property garbage A list of garbage items to be parsed.
 * @property rewards A list of rewards to be parsed.
 * @property tasks A list of tasks to be parsed.
 * @property idLocationMapping A mapping of tileID to tile coordinates.
 */
class ScenarioParser(
    private val scenarioFilepath: String,
    private val idLocationMapping: Map<Int, Pair<Int, Int>>
) {
    /**
     * Companion object for holding any constants used in ScenarioParser
     */
    companion object {
        const val TYPE = "type"
        const val ID = "id"
        const val LOCATION = "location"
        const val RADIUS = "radius"
    }

    // debug logger
    // private val log: Log = LogFactory.getLog("debugger")

    // parser helper
    private val helper = ParserHelper()

    // schemas
    private val scenarioSchema = "scenario.schema"
    private val eventSchema = "event.schema"
    private val garbageSchema = "garbage.schema"
    private val rewardSchema = "reward.schema"
    private val taskSchema = "task.schema"

    // data
    val events: MutableMap<Int, MutableList<Event>> = mutableMapOf()
    val garbage: MutableList<Garbage> = mutableListOf()
    val rewards: MutableList<Reward> = mutableListOf()
    val tasks: MutableMap<Int, MutableList<Task>> = mutableMapOf()

    // used for validation
    private val eventIDs = mutableListOf<Int>()
    private val garbageIDs = mutableListOf<Int>()
    private val taskIDs = mutableListOf<Int>()
    private val rewardIDs = mutableListOf<Int>()

    // helper data for later parsing/validation
    val tileXYtoGarbage = mutableMapOf<Pair<Int, Int>, MutableList<Garbage>>()
    var highestGarbageID = 0

    /**
     * Parses the scenario file.
     *
     * @return `true` if parsing is successful, `false` otherwise.
     */
    fun parseScenario(): Boolean {
        // success variable
        var success = true

        var ex1: IOException? = null
        var ex2: JSONException? = null

        // create scenario JSON object
        val scenarioJSONObject = try {
            JSONObject(File(scenarioFilepath).readText())
        } catch (e: IOException) {
            ex1 = e
            return false
        } catch (e: JSONException) {
            ex2 = e
            return false
        }

        // validate scenario JSON against schema
        if (helper.validateSchema(scenarioJSONObject, this.scenarioSchema)) {
            success = false
        }

        // get JSON arrays
        val eventsJSONArray = scenarioJSONObject.getJSONArray("events")
        val garbageJSONArray = scenarioJSONObject.getJSONArray("garbage")
        val tasksJSONArray = scenarioJSONObject.getJSONArray("tasks")
        val rewardsJSONArray = scenarioJSONObject.getJSONArray("rewards")

        // parse events
        success = success && parseEvents(eventsJSONArray)

        // parse garbage
        success = success && parseGarbages(garbageJSONArray)

        // parse tasks
        success = success && parseTasks(tasksJSONArray)

        // parse rewards
        success = success && parseRewards(rewardsJSONArray)

        return crossValidateTasksForRewards() && success
    }

    /**
     * Iterates over a JSONArray and parses all events.
     *
     * @return true if parsing was a success, false otherwise
     */
    private fun parseEvents(eventsJSONArray: JSONArray): Boolean {
        for (index in 0 until eventsJSONArray.length()) {
            // get event JSON
            val eventJSON = eventsJSONArray.getJSONObject(index)

            // validate event JSON against schema
            if (helper.validateSchema(eventJSON, this.eventSchema)) {
                return false
            }

            // get event
            val event = parseEvent(eventJSON)

            // check event is valid and created correctly
            if (event == null || !validateEventProperties(event)) {
                return false
            } else {
                // event is valid, add to list
                this.events.getOrPut(event.tick) { mutableListOf() }.add(event)
                this.eventIDs.add(event.id)
            }
        }
        // success
        return true
    }

    /**
     * Iterates over a JSONArray and parses all garbage.
     *
     * @return true if parsing was a success, false otherwise
     */
    private fun parseGarbages(garbageJSONArray: JSONArray): Boolean {
        for (index in 0 until garbageJSONArray.length()) {
            // get garbage JSON
            val garbageJSON = garbageJSONArray.getJSONObject(index)

            // validate garbage JSON against schema
            if (helper.validateSchema(garbageJSON, this.garbageSchema)) {
                return false
            }

            // get garbage
            val garbage = parseGarbage(garbageJSON)

            // check garbage is valid and created correctly
            if (garbage == null || !validateGarbageProperties(garbage)) {
                return false
            }

            // event is garbage, add to list
            this.garbage.add(garbage)
            this.garbageIDs.add(garbage.id)
            this.highestGarbageID = maxOf(highestGarbageID, garbage.id)

            // get tile XY
            val locationXY = this.idLocationMapping[garbage.tileId]

            if (locationXY != null) {
                // get garbage list or init list if it does not exist.
                val garbageList = this.tileXYtoGarbage.getOrPut(locationXY) { mutableListOf() }
                garbageList.add(garbage)
            } else {
                return false
            }
        }
        // success
        return true
    }

    /**
     * Iterates over a JSONArray and parses all tasks.
     *
     * @return true if parsing was a success, false otherwise
     */
    private fun parseTasks(tasksJSONArray: JSONArray): Boolean {
        for (index in 0 until tasksJSONArray.length()) {
            // get task JSON
            val taskJSON = tasksJSONArray.getJSONObject(index)

            // validate task JSON against schema
            if (helper.validateSchema(taskJSON, this.taskSchema)) {
                return false
            }

            // get task
            val task = parseTask(taskJSON)

            // check task is valid and created correctly
            if (task == null || !validateTaskProperties(task)) {
                return false
            }

            // task is valid, add to list
            /** CORRECT THIS **/
            this.tasks.getOrPut(task.tick) { mutableListOf() }.add(task)
            this.taskIDs.add(task.id)
        }
        // success
        return true
    }

    /**
     * Iterates over a JSONArray and parses all rewards.
     *
     * @return true if parsing was a success, false otherwise
     */
    private fun parseRewards(rewardsJSONArray: JSONArray): Boolean {
        for (index in 0 until rewardsJSONArray.length()) {
            // get reward JSON
            val rewardJSON = rewardsJSONArray.getJSONObject(index)

            // validate reward JSON against schema
            if (helper.validateSchema(rewardJSON, this.rewardSchema)) {
                return false
            }

            // get reward
            val reward = parseReward(rewardJSON)

            // check reward is valid and created correctly
            if (reward == null || !validateRewardProperties(reward)) {
                return false
            }

            // task is valid, add to list
            this.rewards.add(reward)
            this.rewardIDs.add(reward.id)
        }
        // success
        return true
    }

    /**
     * Parses the events file.
     *
     * @return the event object if successfully created, false otherwise
     */
    private fun parseEvent(eventJSON: JSONObject): Event? {
        // parse based on event type
        val type = eventJSON.getString(TYPE)
        val id = eventJSON.getInt(ID)
        val tick = eventJSON.getInt("tick")

        // handle different event types
        return when (type) {
            "STORM" -> {
                // get values
                val location = eventJSON.getInt(LOCATION)
                val radius = eventJSON.getInt(RADIUS)
                val speed = eventJSON.getInt("speed")
                val direction = eventJSON.getInt("direction")

                val locationXY = idLocationMapping[location]
                val dir = helper.makeDirection(direction)

                return if (locationXY == null || dir == null) {
                    null
                } else {
                    StormEvent(id, tick, locationXY, radius, dir, speed)
                }
            }
            "RESTRICTION" -> {
                // get values
                val duration = eventJSON.getInt("duration")
                val location = eventJSON.getInt(LOCATION)
                val radius = eventJSON.getInt(RADIUS)

                val locationXY = idLocationMapping[location]

                return if (locationXY == null) {
                    null
                } else {
                    RestrictionEvent(id, tick, locationXY, radius, duration)
                }
            }
            "OIL_SPILL" -> {
                // get values
                val location = eventJSON.getInt(LOCATION)
                val radius = eventJSON.getInt(RADIUS)
                val amount = eventJSON.getInt("amount")

                val locationXY = idLocationMapping[location]

                return if (locationXY == null) {
                    null
                } else {
                    OilSpillEvent(id, tick, locationXY, radius, amount)
                }
            }
            "PIRATE_ATTACK" -> {
                // get values
                val shipID = eventJSON.getInt("shipID")

                return PirateAttackEvent(id, tick, shipID)
            }
            else -> {
                null
            }
        }
    }

    /**
     * Parses the garbage file.
     *
     * @return the object if successfully created, null otherwise
     */
    private fun parseGarbage(garbageJSON: JSONObject): Garbage? {
        // get values
        val id = garbageJSON.getInt(ID)
        val type = helper.makeGarbageType(garbageJSON.getString(TYPE))
        val location = garbageJSON.getInt(LOCATION)
        val locationXY = idLocationMapping[location]
        val amount = garbageJSON.getInt("amount")

        // return garbage
        return if (type == null || locationXY == null) {
            null
        } else {
            Garbage(id, amount, type, location, locationXY)
        }
    }

    /**
     * Parses the tasks file.
     *
     * @return the object if successfully created, null otherwise
     */
    private fun parseTask(taskJSON: JSONObject): Task? {
        // get attributes
        val id = taskJSON.getInt(ID)
        val type = helper.makeTaskType(taskJSON.getString(TYPE))
        val tick = taskJSON.getInt("tick")
        val shipID = taskJSON.getInt("shipID")
        val targetTileID = taskJSON.getInt("targetTile")
        val targetTileXY = this.idLocationMapping[targetTileID]
        val rewardID = taskJSON.getInt("rewardID")
        val rewardShipID = taskJSON.getInt("rewardShipID")

        // create the task
        return if (targetTileXY == null || type == null) {
            null
        } else {
            Task(id, type, tick, shipID, targetTileID, rewardID, rewardShipID)
        }
    }

    /**
     * Parses the rewards file.
     *
     * @return the object if successfully created, null otherwise
     */
    private fun parseReward(rewardJSON: JSONObject): Reward? {
        // get attributes
        val id = rewardJSON.getInt(ID)
        val type = helper.makeRewardType(rewardJSON.getString(TYPE))

        return when (type) {
            RewardType.TELESCOPE -> {
                val visibilityRange = rewardJSON.getInt("visibilityRange")
                Reward(id, type, visibilityRange, 0, GarbageType.NONE)
            }
            RewardType.RADIO -> {
                Reward(id, type, 0, 0, GarbageType.NONE)
            }
            RewardType.CONTAINER -> {
                val capacity = rewardJSON.getInt("capacity")
                val garbageType = helper.makeGarbageType(rewardJSON.getString("garbageType"))
                if (garbageType == null) {
                    null
                } else {
                    Reward(id, type, 0, capacity, garbageType)
                }
            }
            RewardType.TRACKING -> {
                Reward(id, type, 0, 0, GarbageType.NONE)
            }
            else -> {
                null
            }
        }
    }

    /**
     * Validates the properties of events.
     *
     * @return `true` if validation is successful, `false` otherwise.
     */
    private fun validateEventProperties(event: Event): Boolean {
        // check ID is unique
        return !this.eventIDs.contains(event.id)
    }

    /**
     * Validates the properties of garbage items.
     *
     * @return `true` if validation is successful, `false` otherwise.
     */
    private fun validateGarbageProperties(garbage: Garbage): Boolean {
        // check ID is unique
        return !this.garbageIDs.contains(garbage.id)
    }

    /**
     * Validates the properties of tasks.
     *
     * @return `true` if validation is successful, `false` otherwise.
     */
    private fun validateTaskProperties(task: Task): Boolean {
        // check ID is unique
        return !this.taskIDs.contains(task.id)
    }

    /**
     * Validates the properties of rewards.
     *
     * @return `true` if validation is successful, `false` otherwise.
     */
    private fun validateRewardProperties(reward: Reward): Boolean {
        // check ID is unique
        return !this.rewardIDs.contains(reward.id)
    }

    /**
     * Cross-validates tasks for rewards.
     *
     * @return `true` if cross-validation is successful, `false` otherwise.
     */
    private fun crossValidateTasksForRewards(): Boolean {
        // Cross validate tasks for rewards
        for (task in this.tasks.values.flatten()) {
            if (!isValidTaskReward(task)) {
                return false
            }
        }
        return true
    }

    private fun isValidTaskReward(task: Task): Boolean {
        return when (task.type) {
            TaskType.COLLECT -> isValidCollectTaskReward(task)
            TaskType.COORDINATE -> isValidCoordinateTaskReward(task)
            TaskType.EXPLORE -> isValidExploreTaskReward(task)
            TaskType.FIND -> isValidFindTaskReward(task)
        } && this.rewardIDs.contains(task.rewardId)
    }

    private fun isValidCollectTaskReward(task: Task): Boolean {
        if ((this.rewards.find { it.id == task.rewardId }?.type ?: true) != RewardType.CONTAINER) {
            return false
        }
        return true
    }

    private fun isValidCoordinateTaskReward(task: Task): Boolean {
        if ((this.rewards.find { it.id == task.rewardId }?.type ?: true) != RewardType.RADIO) {
            return false
        }
        return true
    }

    private fun isValidExploreTaskReward(task: Task): Boolean {
        if ((this.rewards.find { it.id == task.rewardId }?.type ?: true) != RewardType.TELESCOPE) {
            return false
        }
        return true
    }

    private fun isValidFindTaskReward(task: Task): Boolean {
        if ((this.rewards.find { it.id == task.rewardId }?.type ?: true) != RewardType.TRACKING) {
            return false
        }
        return true
    }
}
