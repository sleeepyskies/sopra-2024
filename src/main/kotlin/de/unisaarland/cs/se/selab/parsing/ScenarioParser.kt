package de.unisaarland.cs.se.selab.parsing

import com.github.erosb.jsonsKema.*
import de.unisaarland.cs.se.selab.assets.Direction
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
import org.json.JSONObject
import java.io.File

/**
 * A parser for scenarios, which includes parsing and validating various components
 * such as events, garbage, rewards, and tasks.
 *
 * @property scenarioFilepath The path to the scenario file.
 * @property events A list of events to be parsed.
 * @property garbage A list of garbage items to be parsed.
 * @property rewards A list of rewards to be parsed.
 * @property tasks A list of tasks to be parsed.
 */
class ScenarioParser(
    private val scenarioFilepath: String,
    private val idLocationMapping: Map<Int, Pair<Int, Int>>
) {
    // schemas
    private val scenarioSchema = "scenario.schema"
    private val eventSchema = "event.schema"
    private val garbageSchema = "garbage.schema"
    private val rewardSchema = "reward.schema"
    private val taskSchema = "task.schema"

    // data
    private val events: MutableMap<Int, Event> = mutableMapOf() // tick to event map
    private val garbage: MutableList<Garbage> = mutableListOf()
    private val rewards: MutableList<Reward> = mutableListOf()
    private val tasks: MutableMap<Int, Task> = mutableMapOf()

    // used for validation
    private val eventIDs = mutableListOf<Int>()
    private val garbageIDs = mutableListOf<Int>()
    private val taskIDs = mutableListOf<Int>()
    private val rewardIDs = mutableListOf<Int>()

    /**
     * Parses the scenario file.
     *
     * @return `true` if parsing is successful, `false` otherwise.
     */
    fun parseScenario(): Boolean {
        // create scenario JSON object
        val scenarioJSONObject = JSONObject(File(scenarioFilepath).readText())

        // validate scenario JSON against schema
        if (!validateSchema(scenarioJSONObject, this.scenarioSchema)) {
            return false
        }

        // get JSON arrays
        val eventsJSONArray = scenarioJSONObject.getJSONArray("events")
        val garbageJSONArray = scenarioJSONObject.getJSONArray("garbage")
        val tasksJSONArray = scenarioJSONObject.getJSONArray("tasks")
        val rewardsJSONArray = scenarioJSONObject.getJSONArray("rewards")

        // parse events
        for (index in 0 until eventsJSONArray.length()) {
            // get event JSON
            val eventJSON = eventsJSONArray.getJSONObject(index)

            // validate event JSON against schema
            if (!validateSchema(eventJSON, this.eventSchema)) {
                return false
            }

            // get event
            val event = parseEvent(eventJSON)

            // check event is valid and created correctly
            if (event == null || !validateEventProperties(event)) {
                return false
            }

            // event is valid, add to list
            this.events[event.tick] = event
            this.eventIDs.add(event.id)
        }

        // parse garbage
        for (index in 0 until garbageJSONArray.length()) {
            // get garbage JSON
            val garbageJSON = garbageJSONArray.getJSONObject(index)

            // validate garbage JSON against schema
            if (!validateSchema(garbageJSON, this.garbageSchema)) {
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
        }

        // parse tasks
        for (index in 0 until tasksJSONArray.length()) {
            // get task JSON
            val taskJSON = tasksJSONArray.getJSONObject(index)

            // validate task JSON against schema
            if (!validateSchema(taskJSON, this.taskSchema)) {
                return false
            }

            // get task
            val task = parseTask(taskJSON)

            // check task is valid and created correctly
            if (task == null || !validateTaskProperties(task)) {
                return false
            }

            // task is valid, add to list
            this.tasks[task.tick] = task
            this.taskIDs.add(task.id)
        }

        // parse rewards
        for (index in 0 until rewardsJSONArray.length()) {
            // get reward JSON
            val rewardJSON = tasksJSONArray.getJSONObject(index)

            // validate reward JSON against schema
            if (!validateSchema(rewardJSON, this.rewardSchema)) {
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

        return crossValidateTasksForRewards()
    }

    /**
     * Parses the events file.
     *
     * @return the event object if successfully created, false otherwise
     */
    fun parseEvent(eventJSON: JSONObject): Event? {
        // parse based on event type
        val type = eventJSON.getString("type")

        return when (type) {
            "STORM" -> {
                // get values
                val id = eventJSON.getInt("id")
                val tick = eventJSON.getInt("tick")
                val location = eventJSON.getInt("location")
                val radius = eventJSON.getInt("radius")
                val speed = eventJSON.getInt("speed")
                val direction = eventJSON.getInt("direction")

                val locationXY = idLocationMapping[location]
                val dir = makeDirection(direction)

                return if (locationXY == null || dir == null) {
                    null
                } else {
                    StormEvent(id, tick, locationXY, radius, dir, speed)
                }
            }
            "RESTRICTION" -> {
                // get values
                val id = eventJSON.getInt("id")
                val tick = eventJSON.getInt("tick")
                val duration = eventJSON.getInt("duration")
                val location = eventJSON.getInt("location")
                val radius = eventJSON.getInt("radius")

                val locationXY = idLocationMapping[location]

                return if (locationXY == null) {
                    null
                } else {
                    RestrictionEvent(id, tick, locationXY, radius, duration)
                }
            }
            "OIL_SPILL" -> {
                // get values
                val id = eventJSON.getInt("id")
                val tick = eventJSON.getInt("tick")
                val location = eventJSON.getInt("location")
                val radius = eventJSON.getInt("radius")
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
                val id = eventJSON.getInt("id")
                val tick = eventJSON.getInt("tick")
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
    fun parseGarbage(garbageJSON: JSONObject): Garbage? {
        // get values
        val id = garbageJSON.getInt("id")
        val type = makeGarbageType(garbageJSON.getString("type"))
        val location = garbageJSON.getInt("location")
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
    fun parseTask(taskJSON: JSONObject): Task? {
        // get attributes
        val id = taskJSON.getInt("id")
        val type = makeTaskType(taskJSON.getString("type"))
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
    fun parseReward(rewardJSON: JSONObject): Reward? {
        // get attributes
        val id = rewardJSON.getInt("id")
        val type = makeRewardType(rewardJSON.getString("type"))


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
                val garbageType = makeGarbageType(rewardJSON.getString("garbageType"))
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
    fun validateEventProperties(event: Event): Boolean {
        // check ID is unique
        return !this.eventIDs.contains(event.id)
    }

    /**
     * Validates the properties of garbage items.
     *
     * @return `true` if validation is successful, `false` otherwise.
     */
    fun validateGarbageProperties(garbage: Garbage): Boolean {
        // check ID is unique
        return !this.garbageIDs.contains(garbage.id)
    }

    /**
     * Validates the properties of tasks.
     *
     * @return `true` if validation is successful, `false` otherwise.
     */
    fun validateTaskProperties(task: Task): Boolean {
        // check ID is unique
        return !this.taskIDs.contains(task.id)
    }

    /**
     * Validates the properties of rewards.
     *
     * @return `true` if validation is successful, `false` otherwise.
     */
    fun validateRewardProperties(reward: Reward): Boolean {
        // check ID is unique
        return !this.rewardIDs.contains(reward.id)
    }

    /**
     * Cross-validates tasks for rewards.
     *
     * @return `true` if cross-validation is successful, `false` otherwise.
     */
    fun crossValidateTasksForRewards(): Boolean {
        // Cross validate tasks for rewards
        // go over tasks, and check reward actaully exists
        for (task in this.tasks.values) {
            if (!this.rewardIDs.contains(task.rewardId)) {
                return false
            }
        }
        return true
    }

    /**
     * Validates the given JSONObject against the given schema filepath.
     *
     * @return true if object is valid, false otherwise
     */
    private fun validateSchema(itemJSON: JSONObject, schemaPath: String): Boolean {
        // create validator from schema
        val schemaContent = File(schemaPath).readText()
        val schemaJSON = JsonParser(schemaContent).parse()
        val schema = SchemaLoader(schemaJSON).load()
        val validator = Validator.create(schema, ValidatorConfig(FormatValidationPolicy.ALWAYS))

        // validate JSONObject
        val itemJSONValue = JsonParser(itemJSON.toString()).parse()
        return validator.validate(itemJSONValue) != null
    }

    private fun makeDirection(direction: Int): Direction? {
        return when (direction) {
            0 -> Direction.EAST
            60 -> Direction.SOUTH_EAST
            120 -> Direction.SOUTH_WEST
            180 -> Direction.WEST
            240 -> Direction.NORTH_WEST
            300 -> Direction.NORTH_EAST
            else -> null
        }
    }

    private fun makeGarbageType(type: String): GarbageType? {
        return when (type) {
            "PLASTIC" -> GarbageType.PLASTIC
            "OIL" -> GarbageType.OIL
            "CHEMICALS" -> GarbageType.CHEMICALS
            else -> null
        }
    }

    private fun makeTaskType(type: String): TaskType? {
        return when (type) {
            "COLLECT" -> TaskType.COLLECT
            "EXPLORE" -> TaskType.EXPLORE
            "FIND" -> TaskType.FIND
            "COOPERATE" -> TaskType.COORDINATE
            else -> null
        }
    }

    private fun makeRewardType(type: String): RewardType? {
        return when (type) {
            "TELESCOPE" -> RewardType.TELESCOPE
            "RADIO" -> RewardType.RADIO
            "CONTAINER" -> RewardType.CONTAINER
            "TRACKER" -> RewardType.TRACKING
            else -> null
        }
    }
}
