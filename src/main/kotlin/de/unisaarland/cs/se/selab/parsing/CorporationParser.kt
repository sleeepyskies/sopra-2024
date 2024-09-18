
package de.unisaarland.cs.se.selab.parsing

import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipState
import de.unisaarland.cs.se.selab.assets.ShipType
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Parses corporation data from a file.
 *
 * @property corporationFilePath The name of the file to parse.
 * @property corporations The list of corporations to update.
 * @property ships The list of ships to update.
 */
class CorporationParser(
    private val corporationFilePath: String,
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
    private val corporationIds = mutableSetOf<Int>()
    private val shipIds = mutableSetOf<Int>()
    private val harborLocationsSet = mutableSetOf<Pair<Int, Int>>()

    // parser helper
    private val helper = ParserHelper()
    private val corporationsSchema = "corporations.schema"

    /**
     * Parses corporation data from a file.
     *
     * @return True if the parsing was successful, false otherwise.
     */
    fun parseAllCorporations(): Boolean {
        // parse input file and check if path is valid
        // create corp JSON object
        var success = true

        val corpJSONObject = try {
            JSONObject(File(corporationFilePath).readText())
        } catch (e: IOException) {
            log.error("CORPORATION PARSER: The file could not be read.", e)
            return false
        } catch (e: JSONException) {
            log.error("CORPORATION PARSER: The file is not a valid JSON.", e)
            return false
        }

        // validate corporation JSON against schema
        if (helper.validateSchema(corpJSONObject, this.corporationsSchema)) {
            log.error("MAP PARSER: The file does not match the schema.")
            success = false
        }

        // get the ships array
        val shipsJSON = corpJSONObject.getJSONArray("ships")

        // get the corporations array
        val corporationsJSON = corpJSONObject.getJSONArray("corporations")

        val garbageCollectingShips = mutableListOf<Ship>()
        val shipsList = mutableListOf<Ship>()

        if (!parseShips(shipsJSON, shipsList, garbageCollectingShips)) return false

        // parse and validate all corporations
        for (index in 0 until corporationsJSON.length()) {
            val successfullyParsed = parseCorporation(
                corporationsJSON.getJSONObject(index),
                shipsList,
                garbageCollectingShips
            )
            if (!successfullyParsed) {
                return false
            }
        }
        return success
    }

    /**
     * Parses a single corporation from a JSON object.
     *
     * @param corporationJsonObject The JSON object representing the corporation.
     */
    private fun parseCorporation(
        corporationJsonObject: JSONObject,
        shipsList: MutableList<Ship>,
        collectingShips: MutableList<Ship>
    ): Boolean {
        // validate corporation
        if (helper.validateSchema(corporationJsonObject, "corporation.schema")) {
            log.error("CORPORATION PARSER: One of the corporations does not match the corporation schema.")
            return false
        }
        if (!validateCorporation(corporationJsonObject)) return false
        // adding ids to check for duplicates
        val corporationId = corporationJsonObject.getInt(ID)
        corporationIds.add(corporationId)
        val corporationShips = corporationJsonObject.getJSONArray("ships")
        val shipValidate = !validateShipsOfCorporation(corporationShips, shipsList, corporationId)

        val homeHarborsList = mutableListOf<Pair<Int, Int>>()
        val homeHarborParse = !parseHomeHarbors(corporationJsonObject.getJSONArray("homeHarbors"), homeHarborsList)

        val garbageList = mutableListOf<GarbageType>()
        val garbageParse = !parseGarbageTypes(corporationJsonObject.getJSONArray("garbageTypes"), garbageList)
        if (shipValidate || homeHarborParse || garbageParse) return false

        if (!validateGarbageCollection(collectingShips, garbageList, corporationId)) return false
        val corporation = Corporation(
            corporationJsonObject.optString(NAME, ""),
            corporationId,
            homeHarborsList,
            shipsList.filter { it.corporation == corporationId }.toMutableList(),
            garbageList
        )
        corporations.add(corporation)
        return true
    }
    private fun parseShip(shipJsonObject: JSONObject): Ship? {
        // validate ship
        if (helper.validateSchema(shipJsonObject, "ships.schema")) {
            log.error("MAP PARSER: The file does not match the schema.")
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
        val shipVisibilityRange: Int = if (shipType == ShipType.COLLECTING_SHIP) {
            0
        } else {
            shipJsonObject.getInt("visibilityRange")
        }

        var shipGarbageType: GarbageType? = null
        var shipGarbageCapacity: Int? = null
        if (shipType == ShipType.COLLECTING_SHIP) {
            shipGarbageType = convertGarbageTypeToEnum(shipJsonObject.getString("garbageType"))
            shipGarbageCapacity = shipJsonObject.getInt("capacity")
        }

        // for correct type conversion

        val garbageTypeMap = mutableMapOf<GarbageType, Pair<Int, Int>>()
        // fix the nullable type
        if (shipType == null) {
            return null
        }

        garbageTypeMap[shipGarbageType ?: GarbageType.NONE] = Pair(shipGarbageCapacity ?: 0, shipGarbageCapacity ?: 0)
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
        if (corporationGarbageTypes.length() == 0) {
            return true
        }
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
            val harborLocation = idLocationMapping[tileId] ?: return false
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

    private fun validateShipsOfCorporation(
        corporationShips: JSONArray,
        shipsList: List<Ship>,
        corporationID: Int
    ): Boolean {
        for (index in 0 until corporationShips.length()) {
            val shipId = corporationShips.getInt(index)
            println(shipId)
            println(shipsList)
            println(corporationID)
            val shipWithIdExistInCorporation = shipsList.none { it.id == shipId }
            val shipWithIdIsOfCorrectCorporation = shipsList.any { it.id == shipId && it.corporation != corporationID }
            println(shipWithIdExistInCorporation)
            println(shipWithIdIsOfCorrectCorporation)
            if (shipWithIdExistInCorporation || shipWithIdIsOfCorrectCorporation) {
                return false
            }
        }
        return true
    }

    private fun validateGarbageCollection(
        collectingShips: List<Ship>,
        garbageList: List<GarbageType>,
        corporationId: Int
    ): Boolean {
        for (garbageType in garbageList) {
            if (collectingShips.none { it.capacityInfo.containsKey(garbageType) }) {
                return false
            }
        }
        for (ship in collectingShips) {
            for (garbageType in ship.capacityInfo.keys) {
                if (!garbageList.contains(garbageType) && ship.corporation == corporationId) {
                    return false
                }
            }
        }
        return true
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
        val corporationName = corporationJsonObject.optString(NAME, "")
        val corporationId = corporationJsonObject.getInt(ID)
        return !corporationIds.contains(corporationId) && corporationName.isNotEmpty()
    }
}
