
package de.unisaarland.cs.se.selab.parsing

import com.github.erosb.jsonsKema.FormatValidationPolicy
import com.github.erosb.jsonsKema.JsonParser
import com.github.erosb.jsonsKema.SchemaLoader
import com.github.erosb.jsonsKema.Validator
import com.github.erosb.jsonsKema.ValidatorConfig
import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipState
import de.unisaarland.cs.se.selab.assets.ShipType
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Parses corporation data from a file.
 *
 * @property fileName The name of the file to parse.
 * @property corporations The list of corporations to update.
 * @property ships The list of ships to update.
 */
class CorporationParser(
    private val fileName: String,
    private val idLocationMapping: Map<Int, Pair<Int, Int>>
) {
    /**
     * The maximum number of ships a corporation can have.
     */
    companion object {
        private const val ID = "id"
        private const val NAME = "name"
    }
    private val log: Log = LogFactory.getLog("debugger")
    val corporations = mutableListOf<Corporation>()
    val ships = mutableListOf<Ship>()
    private val validator = initCorporationsSchemaValidator()
    private val corporationsSchema: String = "corporations.schema"
    private val corporationSchema = "corporation.schema"
    private val shipSchema = "ships.schema"
    private val corporationIds = mutableSetOf<Int>()
    private val shipIds = mutableSetOf<Int>()
    private val harborLocationsSet = mutableSetOf<Pair<Int, Int>>()

    /**
     * Parses corporation data from a file.
     *
     * @return True if the parsing was successful, false otherwise.
     */
    fun parseAllCorporations(): Boolean {
        // parse input file and check if path is valid
        val corporationsFileContent = try {
            File(fileName).readText()
        } catch (e: IOException) {
            log.error("CORPORATION PARSER: The file could not be read.", e)
            return false
        }
        val corporationsJson = JsonParser(corporationsFileContent).parse()
        val schemaValidation = validator.validate(corporationsJson)
        // create JSONObject from file
        val corporationsJSONObject = JSONObject(File(fileName).readText())
        // Get Corporations array from file
        val corporationsJSON = corporationsJSONObject.getJSONArray("corporations")
        // checking for invalid schema and empty corporations array
        if (schemaValidation != null || corporationsJSON.length() == 0) {
            return false
        }

        val shipsJSON = corporationsJSONObject.getJSONArray("ships")
        val garbageCollectingShips = mutableListOf<Ship>()
        val shipsList = mutableListOf<Ship>()
        val shipParse = !parseShips(shipsJSON, shipsList, garbageCollectingShips)

        // parse and validate all corporations
        for (index in 0 until corporationsJSON.length()) {
            val successfullyParsed = parseCorporation(
                corporationsJSON.getJSONObject(index),
                shipsList,
                garbageCollectingShips,
                shipParse
            )
            if (!successfullyParsed) {
                return false
            }
        }
        return true
    }

    /**
     * Parses a single corporation from a JSON object.
     *
     * @param corporationJsonObject The JSON object representing the corporation.
     */
    private fun parseCorporation(
        corporationJsonObject: JSONObject,
        shipsList: MutableList<Ship>,
        collectingShips: MutableList<Ship>,
        shipParse: Boolean
    ): Boolean {
        // validate corporation
        if (!validateCorporation(corporationJsonObject)) return false
        // adding ids to check for duplicates
        val corporationId = corporationJsonObject.getInt(ID)
        corporationIds.add(corporationId)
        val corporationShips = corporationJsonObject.getJSONArray("ships")

        val homeHarborsList = mutableListOf<Pair<Int, Int>>()
        val homeHarborParse = !parseHomeHarbors(corporationJsonObject.getJSONArray("homeHarbors"), homeHarborsList)

        val garbageList = mutableListOf<GarbageType>()
        val garbageParse = !parseGarbageTypes(corporationJsonObject.getJSONArray("garbageTypes"), garbageList)
        if (shipParse || homeHarborParse || garbageParse) return false

        if (!validateGarbageCollection(collectingShips, garbageList)) return false
        val corporation = Corporation(
            corporationJsonObject.optString(NAME, ""),
            corporationId,
            homeHarborsList,
            shipsList,
            garbageList
        )
        corporations.add(corporation)
        return true
    }
    private fun parseShip(shipJsonObject: JSONObject): Ship? {
        // validate ship
        val shipValidator = initShipSchemaValidator()
        val shipJSONValue = JsonParser(shipJsonObject.toString()).parse()
        val schemaValidation = shipValidator.validate(shipJSONValue)
        if (schemaValidation != null) {
            return null
        }
        // ensures that ship ids are unique
        val shipId = shipJsonObject.getInt(ID)
        if (shipIds.contains(shipId)) {
            return null
        }
        shipIds.add(shipId)
        val shipName = shipJsonObject.getString(NAME)
        val shipType = convertShipTypeToEnum(shipJsonObject.getString("type"))
        val shipCorporationId = shipJsonObject.getInt("corporation")
        val shipSpeed = shipJsonObject.getInt("maxVelocity")
        val shipTileId = shipJsonObject.getInt("location")
        val shipAcceleration = shipJsonObject.getInt("acceleration")
        val shipFuelCapacity = shipJsonObject.getInt("fuelCapacity")
        val shipFuelConsumption = shipJsonObject.getInt("fuelConsumption")
        var shipVisibilityRange: Int? = null
        if (shipType != ShipType.COLLECTING_SHIP) {
            shipVisibilityRange = shipJsonObject.getInt("visibilityRange")
        }
        var shipGarbageType: GarbageType? = null
        var shipGarbageCapacity: Int? = null
        if (shipType == ShipType.COLLECTING_SHIP) {
            shipGarbageType = convertGarbageTypeToEnum(shipJsonObject.getString("garbageType"))
            shipGarbageCapacity = shipJsonObject.getInt("garbageCapacity")
        }

        // for correct type conversion

        val garbageTypeMap = mutableMapOf<GarbageType, Pair<Int, Int>>()
        // fix the nullable type
        if (shipType == null || shipGarbageType == null) {
            return null
        }

        garbageTypeMap[shipGarbageType] = Pair(shipGarbageCapacity ?: 0, shipGarbageCapacity ?: 0)
        val shipLocation = idLocationMapping[shipTileId] ?: return null
        val ship = Ship(
            shipId,
            shipName,
            shipCorporationId,
            garbageTypeMap,
            shipVisibilityRange ?: 0,
            shipLocation,
            Direction.EAST,
            shipTileId,
            shipSpeed,
            0,
            shipAcceleration,
            shipFuelCapacity,
            shipFuelConsumption,
            shipFuelCapacity,
            -1,
            ShipState.DEFAULT,
            shipType
        )
        // parse and validate all garbage
        return ship
    }
    private fun parseGarbageTypes(corporationGarbageTypes: JSONArray, garbageList: MutableList<GarbageType>): Boolean {
        for (index in 0 until corporationGarbageTypes.length()) {
            val garbageType = convertGarbageTypeToEnum(corporationGarbageTypes.getString(index)) ?: return false
            garbageList.add(garbageType)
        }
        return true
    }
    private fun parseHomeHarbors(
        corporationHomeHarbors: JSONArray,
        homeHarborsList: MutableList<Pair<Int, Int>>
    ): Boolean {
        for (index in 0 until corporationHomeHarbors.length()) {
            val tileId = corporationHomeHarbors.getInt(index)
            val harborLocation = idLocationMapping[tileId]
            if (harborLocationsSet.contains(harborLocation) || harborLocation == null) {
                return false
            }
            harborLocationsSet.add(harborLocation)
            homeHarborsList.add(harborLocation)
        }
        return homeHarborsList.isNotEmpty()
    }
    private fun parseShips(
        corporationShips: JSONArray,
        shipsList: MutableList<Ship>,
        collectingShips: MutableList<Ship>
    ): Boolean {
        for (index in 0 until corporationShips.length()) {
            val ship = parseShip(corporationShips.getJSONObject(index)) ?: return false
            ships.add(ship)
            shipsList.add(ship)
            if (ship.type == ShipType.COLLECTING_SHIP) {
                collectingShips.add(ship)
            }
        }
        return shipsList.isNotEmpty()
    }
    private fun initCorporationsSchemaValidator(): Validator {
        val schema = SchemaLoader.forURL("classpath://schema/corporations.schema").load()

        // create and return validator
        return Validator.create(schema, ValidatorConfig(FormatValidationPolicy.ALWAYS))
    }
    private fun validateGarbageCollection(collectingShips: List<Ship>, garbageList: List<GarbageType>): Boolean {
        for (garbageType in garbageList) {
            if (collectingShips.none { it.capacityInfo.containsKey(garbageType) }) {
                return false
            }
        }
        for (ship in collectingShips) {
            for (garbageType in ship.capacityInfo.keys) {
                if (!garbageList.contains(garbageType)) {
                    return false
                }
            }
        }
        return true
    }
    private fun initCorporationSchemaValidator(): Validator {
        // read content of JSON schema
        val schema = SchemaLoader.forURL("classpath://schema/corporation.schema").load()
        // create and return validator
        return Validator.create(schema, ValidatorConfig(FormatValidationPolicy.ALWAYS))
    }
    private fun initShipSchemaValidator(): Validator {
        // read content of JSON schema
        val schema = SchemaLoader.forURL("classpath://schema/ships.schema").load()
        // create and return validator
        return Validator.create(schema, ValidatorConfig(FormatValidationPolicy.ALWAYS))
    }
    private fun convertShipTypeToEnum(type: String): ShipType? {
        return when (type) {
            "SCOUTING" -> ShipType.SCOUTING_SHIP
            "COLLECTING" -> ShipType.COLLECTING_SHIP
            "COORDINATING" -> ShipType.COORDINATING_SHIP
            else -> null
        }
    }
    private fun convertGarbageTypeToEnum(type: String): GarbageType? {
        return when (type) {
            "PLASTIC" -> GarbageType.PLASTIC
            "CHEMICALS" -> GarbageType.CHEMICALS
            "OIL" -> GarbageType.OIL
            else -> null
        }
    }
    private fun validateCorporation(corporationJsonObject: JSONObject): Boolean {
        val corporationValidator = initCorporationSchemaValidator()
        val corporationJSONValue = JsonParser(corporationJsonObject.toString()).parse()
        val schemaValidation = corporationValidator.validate(corporationJSONValue)
        val corporationName = corporationJsonObject.optString(NAME, "")
        val corporationId = corporationJsonObject.getInt(ID)
        return schemaValidation == null && !corporationIds.contains(corporationId) && corporationName.isNotEmpty()
    }
}
