package de.unisaarland.cs.se.selab.parsing

import com.github.erosb.jsonsKema.FormatValidationPolicy
import com.github.erosb.jsonsKema.JsonParser
import com.github.erosb.jsonsKema.SchemaLoader
import com.github.erosb.jsonsKema.Validator
import com.github.erosb.jsonsKema.ValidatorConfig
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.RewardType
import de.unisaarland.cs.se.selab.assets.TaskType
import de.unisaarland.cs.se.selab.assets.TileType
import org.json.JSONObject

/**
 * A Parser Helper class containing useful shared methods and constants
 */
class ParserHelper {
    /**
     * The maximum number of ships a corporation can have.
     */
    companion object {
        // numbers
        const val EAST = 0
        const val SOUTH_EAST = 60
        const val SOUTH_WEST = 120
        const val WEST = 180
        const val NORTH_WEST = 240
        const val NORTH_EAST = 300
    }

    /**
     * Validates the given JSONObject against the given schema filepath.
     *
     * @return true if object is valid, false otherwise
     */
    public fun validateSchema(itemJSON: JSONObject, schemaPath: String): Boolean {
        // create validator from schema
        // val schemaContent = File(schemaPath).readText()
        // val schemaJSON = JsonParser(schemaContent).parse()
        val schema = SchemaLoader.forURL("classpath://schema/$schemaPath").load()
        val validator = Validator.create(schema, ValidatorConfig(FormatValidationPolicy.ALWAYS))

        // validate JSONObject
        val itemJSONValue = JsonParser(itemJSON.toString()).parse()
        return validator.validate(itemJSONValue) != null
    }

    /**
     * Converts a number into a direction.
     */
    fun makeDirection(direction: Int): Direction? {
        return when (direction) {
            EAST -> Direction.EAST
            SOUTH_EAST -> Direction.SOUTH_EAST
            SOUTH_WEST -> Direction.SOUTH_WEST
            WEST -> Direction.WEST
            NORTH_WEST -> Direction.NORTH_WEST
            NORTH_EAST -> Direction.NORTH_EAST
            else -> null
        }
    }

    /**
     * Converts a string into a GarbageType
     */
    fun makeGarbageType(type: String): GarbageType? {
        return when (type) {
            "PLASTIC" -> GarbageType.PLASTIC
            "OIL" -> GarbageType.OIL
            "CHEMICALS" -> GarbageType.CHEMICALS
            else -> null
        }
    }

    /**
     * Converts a string into a TaskType
     */
    fun makeTaskType(type: String): TaskType? {
        return when (type) {
            "COLLECT" -> TaskType.COLLECT
            "EXPLORE" -> TaskType.EXPLORE
            "FIND" -> TaskType.FIND
            "COOPERATE" -> TaskType.COORDINATE
            else -> null
        }
    }

    /**
     * Converts a string into a RewardType
     */
    fun makeRewardType(type: String): RewardType? {
        return when (type) {
            "TELESCOPE" -> RewardType.TELESCOPE
            "RADIO" -> RewardType.RADIO
            "CONTAINER" -> RewardType.CONTAINER
            "TRACKER" -> RewardType.TRACKING
            else -> null
        }
    }

    /**
     * Converts a string into a TileType
     */
    fun makeTileType(categoryString: String): TileType? {
        return when (categoryString) {
            "LAND" -> TileType.LAND
            "SHORE" -> TileType.SHORE
            "SHALLOW_OCEAN" -> TileType.SHALLOW_OCEAN
            "DEEP_OCEAN" -> TileType.DEEP_OCEAN
            else -> null
        }
    }
}
