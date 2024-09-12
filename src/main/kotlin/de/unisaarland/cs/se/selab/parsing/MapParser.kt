
package de.unisaarland.cs.se.selab.parsing
import com.github.erosb.jsonsKema.*
import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.json.JSONObject
import java.io.File

/**
 * Parses map data from a file.
 *
 * @property mapFilePath The file path to the provided map file.
 * @property navigationManager The navigation manager to use.
 */
class MapParser(
    private val mapFilePath: String
) {
    // schemas
    private val mapSchema = "map.schema"
    private val tileSchema = "tile.schema"

    // validator
    private var validator = initMapSchemaValidator()

    // Map data
    private val map: MutableMap<Pair<Int, Int>, Tile> = mutableMapOf()

    // Map of tile id to location used in corporation parser
    val idLocationMapping: MutableMap<Int, Pair<Int, Int>> = mutableMapOf()

    /**
     * Parses map data from a file.
     *
     * @return True if the parsing was successful, false otherwise.
     */
    public fun parseMap(): Boolean {

        // parse input file
        val mapFileContent = File(mapFilePath).readText()
        val mapJSON = JsonParser(mapFileContent).parse()

        // validate map file according to schema
        // still need to properly handle this
        val schemaValidation = validator.validate(mapJSON)

        // create JSONObject from file
        val mapJSONObject = JSONObject(File(mapFilePath).readText())

        // Get Tile array from file
        val tilesJSON = mapJSONObject.getJSONArray("tiles")

        // parse and validate all tiles
        for (index in 0 until tilesJSON.length()) {
            parseTile(tilesJSON.getJSONObject(index))
        }

        // validate the map
        validateMapProperties()

        return true
    }

    /**
     * Parses a tile from a string and adds it to the map data structure
     */
    private fun parseTile(tileJSON: JSONObject) {
        // validate tile according to schema file
        val tileValidator = makeTileSchemaValidator()
        val tileJSONValue = JsonParser(tileJSON.toString()).parse()
        val parseResult = tileValidator.validate(tileJSONValue)

        // get values
        val id = tileJSON.getInt("id")
        val coordinatesJSON = tileJSON.getJSONObject("coordinates")
        val coordX = coordinatesJSON.getInt("x")
        val coordY = coordinatesJSON.getInt("y")
        val category = makeCategory(tileJSON.getString("category"))
        val harbor = tileJSON.getBoolean("harbor")
        // for current
        val current = tileJSON.getBoolean("current")
        val direction = makeDirection(tileJSON.getInt("direction"))
        val speed = tileJSON.getInt("speed")
        val intensity = tileJSON.getInt("intensity")

        if (
            validateTileProperties(id, coordX, coordY, category, harbor, current, direction, speed, intensity) &&
            category != null &&
            direction != null
        ) {
            val tileCurrent = Current(direction, intensity, speed)
            // add to map structure
            this.map[Pair(coordX, coordY)] = Tile(id, Pair(coordX, coordY), category, harbor, tileCurrent, current)
            // add to id location mapping for corporation parser
            this.idLocationMapping[id] = Pair(coordX, coordY)
        }
    }

    /**
     * Validates the parameters in the map.
     *
     * @return True if valid, false otherwise
     */
    private fun validateMapProperties(): Boolean {
        return true
    }

    /**
     * Validates the parameters in the given tile.
     *
     * @return True if valid, false otherwise
     */
    private fun validateTileProperties (
        id: Int,
        coordX: Int,
        coordY: Int,
        categoryString: TileType?,
        harbor: Boolean,
        current: Boolean,
        direction: Direction?,
        speed: Int,
        intensity: Int
    ): Boolean {
        return true
    }

    /**
     * Returns the parsed NavigationManager.
     */
    public fun getNavManager(): NavigationManager {
        return NavigationManager(map)
    }

    /**
     * Returns a new schema validator using the tile schema
     */
    private fun makeTileSchemaValidator(): Validator {
        // read content of JSON schema
        val schemaContent = File(tileSchema).readText()
        // Parse JSON schema as string
        val schemaJSON = JsonParser(schemaContent).parse()
        // create schema instance
        val schema = SchemaLoader(schemaJSON).load()
        // create and return validator
        return Validator.create(schema, ValidatorConfig(FormatValidationPolicy.ALWAYS))
    }

    /**
     * Sets up the map schema validator.
     */
    private fun initMapSchemaValidator(): Validator {
        // read content of JSON schema
        val schemaContent = File(mapSchema).readText()
        // Parse JSON schema as string
        val schemaJSON = JsonParser(schemaContent).parse()
        // create schema instance
        val schema = SchemaLoader(schemaJSON).load()
        // create and return validator
        return Validator.create(schema, ValidatorConfig(FormatValidationPolicy.ALWAYS))
    }

    private fun makeCategory(categoryString: String): TileType? {
        return when (categoryString) {
            "LAND" -> TileType.LAND
            "SHORE" -> TileType.SHORE
            "SHALLOW_OCEAN" -> TileType.SHALLOW_OCEAN
            "DEEP_OCEAN" -> TileType.DEEP_OCEAN
            else -> null
        }
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
}
