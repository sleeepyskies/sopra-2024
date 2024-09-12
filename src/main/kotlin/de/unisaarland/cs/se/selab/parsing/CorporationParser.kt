
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
    companion object {
        private const val ID = "id"
        private const val NAME = "name"
    }
    private val corporations = mutableListOf<Corporation>()
    private val ships = mutableListOf<Ship>()
    private var validator = initCorporationsSchemaValidator()
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
            println("Error reading file: ${e.message}")
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
        // parse and validate all corporations
        for (index in 0 until corporationsJSON.length()) {
            val successfullyParsed = parseCorporation(corporationsJSON.getJSONObject(index))
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
    private fun parseCorporation(corporationJsonObject: JSONObject): Boolean {
        // validate corporation
        if (!validateCorporation(corporationJsonObject)) return false
        // adding ids to check for duplicates
        val corporationId = corporationJsonObject.getInt(ID)
        corporationIds.add(corporationId)
        val shipsList = mutableListOf<Ship>()
        val collectingShips = mutableListOf<Ship>()
        val shipParse = !parseShips(corporationJsonObject.getJSONArray("ships"), shipsList, collectingShips)

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
    private fun parseShips(shipJsonObject: JSONObject): Ship? {
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
        val shipVisibilityRange = shipJsonObject.getInt("visibilityRange")
        val shipGarbageType = convertGarbageTypeToEnum(shipJsonObject.getString("garbageType"))
        val shipGarbageCapacity = shipJsonObject.getInt("garbageCapacity")

        // for correct type conversion

        val garbageTypeMap = mutableMapOf<GarbageType, Pair<Int, Int>>()
        // fix the nullable type
        if (shipType == null || shipGarbageType == null) {
            return null
        }
        garbageTypeMap[shipGarbageType] = Pair(shipGarbageCapacity, shipGarbageCapacity)
        val shipLocation = idLocationMapping[shipTileId] ?: return null
        val ship = Ship(
            shipId,
            shipName,
            shipCorporationId,
            garbageTypeMap,
            shipVisibilityRange,
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
            val ship = parseShips(corporationShips.getJSONObject(index)) ?: return false
            ships.add(ship)
            shipsList.add(ship)
            if (ship.type == ShipType.COLLECTING_SHIP) {
                collectingShips.add(ship)
            }
        }
        return shipsList.isNotEmpty()
    }
    private fun initCorporationsSchemaValidator(): Validator {
        // read content of JSON schema
        val schemaContent = File(corporationsSchema).readText()
        // Parse JSON schema as string
        val schemaJSON = JsonParser(schemaContent).parse()
        // create schema instance
        val schema = SchemaLoader(schemaJSON).load()
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
        val schemaContent = File(corporationSchema).readText()
        // Parse JSON schema as string
        val schemaJSON = JsonParser(schemaContent).parse()
        // create schema instance
        val schema = SchemaLoader(schemaJSON).load()
        // create and return validator
        return Validator.create(schema, ValidatorConfig(FormatValidationPolicy.ALWAYS))
    }
    private fun initShipSchemaValidator(): Validator {
        // read content of JSON schema
        val schemaContent = File(shipSchema).readText()
        // Parse JSON schema as string
        val schemaJSON = JsonParser(schemaContent).parse()
        // create schema instance
        val schema = SchemaLoader(schemaJSON).load()
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
