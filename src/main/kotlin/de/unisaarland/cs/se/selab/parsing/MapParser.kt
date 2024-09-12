
package de.unisaarland.cs.se.selab.parsing
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.json.JSONObject
import java.io.File
import com.github.erosb.jsonsKema.*
import de.unisaarland.cs.se.selab.assets.TileType

/**
 * Parses map data from a file.
 *
 * @property mapFilePath The file path to the provided map file.
 * @property navigationManager The navigation manager to use.
 */
class MapParser(
    private val mapFilePath: String
) {
    // map schema file path
    private val mapSchema = "map.schema"
    // tile schema file path
    private val tileSchema = "tile.schema"
    // create schema validator
    private var validator = initMapSchemaValidator()
    // Map data
    private lateinit var map : Map<Pair<Int, Int>, Int>
    private lateinit var navigationManager: NavigationManager

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
     * Parses a tile from a string
     */
    private fun parseTile(tileJSON: JSONObject): Tile {
        // validate tile according to schema file
        val tileValidator = makeTileSchemaValidator()
        val tileJSONValue = JsonParser(tileJSON.toString()).parse()
        val parseResult = tileValidator.validate(tileJSONValue)

        // get values
        val id = tileJSON.getInt("id")
        val coordinatesJSON = tileJSON.getJSONObject("coordinates")
        val coordX = coordinatesJSON.getInt("x")
        val coordY = coordinatesJSON.getInt("y")
        val categoryString = tileJSON.getString("category")
        val harbor = tileJSON.getBoolean("harbor")

        val category = makeCategory(categoryString)

        if (validateTileProperties(id, coordX, coordY, categoryString, harbor)) {
            return Tile()
        }

    }

    /**
     * Validates the parameters in the map.
     *
     * @return True if valid, false otherwise
     */
    private fun validateMapProperties() : Boolean {
        TODO()
    }

    /**
     * Validates the parameters in the given tile.
     *
     * @return True if valid, false otherwise
     */
    private fun validateTileProperties() : Boolean {
        TODO()
    }

    /**
     * Returns the parsed NavigationManager.
     */
    public fun getNavManager() : NavigationManager {
        return NavigationManager(map)
    }

    /**
     * Returns a new schema validator using the tile schema
     */
    private fun makeTileSchemaValidator() : Validator {
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
    private fun initMapSchemaValidator() : Validator {
        // read content of JSON schema
        val schemaContent = File(mapSchema).readText()
        // Parse JSON schema as string
        val schemaJSON = JsonParser(schemaContent).parse()
        // create schema instance
        val schema = SchemaLoader(schemaJSON).load()
        // create and return validator
        return Validator.create(schema, ValidatorConfig(FormatValidationPolicy.ALWAYS))
    }

    private fun makeCategory(categoryString : String) : TileType {
        when (categoryString) {
            "SHORE" -> TileType.SHORE
        }
    }
}
