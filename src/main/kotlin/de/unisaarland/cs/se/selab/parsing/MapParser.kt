
package de.unisaarland.cs.se.selab.parsing
import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Parses map data from a file.
 *
 * @property mapFilePath The file path to the provided map file.
 */
class MapParser(
    private val mapFilePath: String
) {
    // debug logger
    private val log: Log = LogFactory.getLog("debugger")

    // parser helper
    private val helper = ParserHelper()

    // schemas
    private val mapSchema = "C:\\Users\\Asus\\Desktop\\Saarland CS\\Saarland CS 2\\Saarland CS 2.2\\SOPRA\\group12\\src\\main\\resources\\schema\\map.schema"
    private val tileSchema = "tile.schema"

    // Map data
    private val map: MutableMap<Pair<Int, Int>, Tile> = mutableMapOf()

    // used for validation
    private val tileIDs = mutableListOf<Int>()
    private val tileLocs = mutableListOf<Pair<Int, Int>>()

    // Map of tile id to location used in corporation parser
    val idLocationMapping: MutableMap<Int, Pair<Int, Int>> = mutableMapOf()

    /**
     * Parses map data from a file.
     *
     * @return True if the parsing was successful, false otherwise.
     */
    fun parseMap(): Boolean {
        // success variable
        var success = true

        // create map JSON object
        val mapJSONObject = try {
            JSONObject(File(mapFilePath).readText())
        } catch (e: IOException) {
            log.error("MAP PARSER: The file could not be read.", e)
            return false
        } catch (e: JSONException) {
            log.error("MAP PARSER: The file is not a valid JSON.", e)
            return false
        }

        // validate map JSON against schema
        if (!helper.validateSchema(mapJSONObject, this.mapSchema)) {
            log.error("MAP PARSER: The file does not match the schema.")
            success = false
        }

        // Get Tile array from file
        val tileJSONArray = mapJSONObject.getJSONArray("tiles")

        // parse and validate all tiles
        success = success && parseTiles(tileJSONArray)

        // validate the map
        success = success && validateMapProperties()

        return success
    }

    /**
     * Iterates over a JSONArray and parses all tiles.
     * @return true if parsing was a success, false otherwise.
     */
    private fun parseTiles(tileJSONArray: JSONArray): Boolean {
        for (index in 0 until tileJSONArray.length()) {
            // get tile JSON
            val tileJSON = tileJSONArray.getJSONObject(index)

            // validate garbage JSON against schema
            if (!helper.validateSchema(tileJSON, this.tileSchema)) {
                log.error("MAP PARSER: The tile do not match the schema.")
                return false
            }

            // get tile
            val tile = parseTile(tileJSON)

            // check garbage is valid and created correctly
            if (tile == null || !validateTileProperties(tile)) {
                log.error(
                    "MAP PARSER: A tile does not have a unique ID" +
                        " or could not be correctly instantiated."
                )
                return false
            }

            // add to map
            this.map[tile.location] = tile

            // add validation data
            this.idLocationMapping[tile.id] = tile.location
            this.tileIDs.add(tile.id)
            this.tileLocs.add(tile.location)
        }

        return true
    }

    /**
     * Parses a tile from a string and adds it to the map data structure
     * @return the object if parsed correctly, null otherwise.
     */
    private fun parseTile(tileJSON: JSONObject): Tile? {
        // get values
        val id = tileJSON.getInt("id")
        val coordinatesJSON = tileJSON.getJSONObject("coordinates")
        val cordX = coordinatesJSON.getInt("x")
        val cordY = coordinatesJSON.getInt("y")
        val category = helper.makeTileType(tileJSON.getString("category"))

        // useless current *spits*
        val mockCurrent = Current(Direction.EAST, 0, 0)

        // handle different tile types
        return when (category) {
            TileType.LAND -> {
                Tile(id, Pair(cordX, cordY), TileType.LAND, false, mockCurrent, false, 0)
            }
            TileType.SHALLOW_OCEAN -> {
                Tile(id, Pair(cordX, cordY), TileType.SHALLOW_OCEAN, false, mockCurrent, false)
            }
            TileType.DEEP_OCEAN -> {
                val current = tileJSON.getBoolean("current")
                if (current) {
                    // has current
                    val direction = helper.makeDirection(tileJSON.getInt("direction"))
                    val speed = tileJSON.getInt("speed")
                    val intensity = tileJSON.getInt("intensity")
                    val realCurrent = direction?.let { Current(it, intensity, speed) }
                    if (realCurrent != null) {
                        Tile(id, Pair(cordX, cordY), TileType.SHALLOW_OCEAN, false, realCurrent, true)
                    } else {
                        null
                    }
                } else {
                    // has no current
                    Tile(id, Pair(cordX, cordY), TileType.SHALLOW_OCEAN, false, mockCurrent, false)
                }
            }
            TileType.SHORE -> {
                val harbor = tileJSON.getBoolean("harbor")
                Tile(id, Pair(cordX, cordY), TileType.SHORE, harbor, mockCurrent, false, 0)
            }
            else -> {
                null
            }
        }
    }

    /**
     * Checks if each map tile is correctly placed next to other tiles.
     *
     * @return True if valid, false otherwise
     */
    private fun validateMapProperties(): Boolean {
        for ((location, tile) in this.map) {
            val neighbors = getTilesNeighbors(location)
            if (!validateNeighbors(tile, neighbors)) {
                log.error("MAP PARSER: Tile ${tile.id} does not have correct neighbor tile types.")
                return false
            }
        }
        return true
    }

    /**
     * Checks if the given tile type is valid against its neighbors tile types.
     * @param tile The center tile
     * @param neighbors The neighboring tiles
     * @return true if this is a match, false otherwise
     */
    private fun validateNeighbors(tile: Tile, neighbors: List<Tile>): Boolean {
        return when (tile.type) {
            TileType.LAND -> neighbors.all {
                it.type == TileType.LAND || it.type == TileType.SHORE
            }
            TileType.SHORE -> neighbors.all {
                it.type == TileType.LAND || it.type == TileType.SHORE || it.type == TileType.SHALLOW_OCEAN
            }
            TileType.SHALLOW_OCEAN -> neighbors.all {
                it.type == TileType.SHORE || it.type == TileType.SHALLOW_OCEAN || it.type == TileType.DEEP_OCEAN
            }
            TileType.DEEP_OCEAN -> neighbors.all { it.type == TileType.SHALLOW_OCEAN || it.type == TileType.DEEP_OCEAN }
        }
    }

    /**
     * Provides all direct neighbors of the given tile
     * @param location The location of the tile
     * @return a list of neighbors, may be empty if there are no neighbors
     */
    private fun getTilesNeighbors(location: Pair<Int, Int>): List<Tile> {
        // return list
        val neighbors = mutableListOf<Tile>()

        // get cords
        val (x, y) = location
        val directions = listOf(
            Pair(x, y - 1), // NorthWest
            Pair(x, y - 1), // NorthEast
            Pair(x + 1, y), // East
            Pair(x + 1, y + 1), // SouthEast
            Pair(x, y + 1), // SouthWest
            Pair(x - 1, y) // West
        )

        // only add to neighbors if tile exists
        for (direction in directions) {
            this.map[direction]?.let { neighbors.add(it) }
        }

        return neighbors.toList()
    }

    /**
     * Validates the parameters in the given tile.
     *
     * @return True if valid, false otherwise
     */
    private fun validateTileProperties(tile: Tile): Boolean {
        // check ID is unique and location is unique
        return !this.tileIDs.contains(tile.id) && !this.tileLocs.contains(tile.location)
    }

    /**
     * Returns the parsed NavigationManager.
     */
    fun getNavManager(): NavigationManager {
        return NavigationManager(map)
    }
}
