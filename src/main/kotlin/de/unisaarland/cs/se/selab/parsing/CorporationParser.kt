
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
        private const val MIN_VELOCITY = 10
        private const val MAX_VELOCITY = 100
        private const val MIN_ACCELERATION = 5
        private const val MAX_ACCELERATION = 25
        private const val MIN_FUEL_CAPACITY = 3000
        private const val MAX_FUEL_CAPACITY = 10000
        private const val MIN_FUEL_CONSUMPTION = 7
        private const val MAX_FUEL_CONSUMPTION = 10
        private const val MIN_VISIBILITY_RANGE = 2
        private const val MAX_VISIBILITY_RANGE = 5

        private const val MIN_COORDINATING_VELOCITY = 10
        private const val MAX_COORDINATING_VELOCITY = 50
        private const val MIN_COORDINATING_ACCELERATION = 5
        private const val MAX_COORDINATING_ACCELERATION = 15
        private const val MIN_COORDINATING_FUEL_CAPACITY = 3000
        private const val MAX_COORDINATING_FUEL_CAPACITY = 5000
        private const val MIN_COORDINATING_FUEL_CONSUMPTION = 5
        private const val MAX_COORDINATING_FUEL_CONSUMPTION = 7
        private const val MAX_COORDINATING_VISIBILITY_RANGE = 1

        private const val MIN_COLLECTING_VELOCITY = 10
        private const val MAX_COLLECTING_VELOCITY = 50
        private const val MIN_COLLECTING_ACCELERATION = 5
        private const val MAX_COLLECTING_ACCELERATION = 10
        private const val MIN_COLLECTING_FUEL_CAPACITY = 3000
        private const val MAX_COLLECTING_FUEL_CAPACITY = 5000
        private const val MIN_COLLECTING_FUEL_CONSUMPTION = 5
        private const val MAX_COLLECTING_FUEL_CONSUMPTION = 9
        private const val MIN_PLASTIC_CAPACITY = 1000
        private const val MAX_PLASTIC_CAPACITY = 5000
        private const val MIN_CHEMICALS_CAPACITY = 1000
        private const val MAX_CHEMICALS_CAPACITY = 10000
        private const val MIN_OIL_CAPACITY = 50000
        private const val MAX_OIL_CAPACITY = 100000
    }
    private val log: Log = LogFactory.getLog("debugger")
    val corporations = mutableListOf<Corporation>()
    val ships = mutableListOf<Ship>()
    private val corporationIds = mutableSetOf<Int>()
    private val corporationNames = mutableSetOf<String>()
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
        // Validate that all ships belong to valid corporations
        val shipsWithoutCorporations = checkForShipsWithoutCorporations(shipsList)
        return success && shipsWithoutCorporations
    }

    // Validates that all ships belong to valid corporations
    private fun checkForShipsWithoutCorporations(shipsList: List<Ship>): Boolean {
        for (ship in shipsList) {
            if (!corporationIds.contains(ship.corporation)) {
                log.error("MAP PARSER: Ship with ID ${ship.id} has an invalid corporation ID ${ship.corporation}.")
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
                if (!checkCollectingShip(ship)) {
                    return false
                }
                collectingShips.add(ship)
            }
            if (ship.type == ShipType.SCOUTING_SHIP && !checkScoutingShip(ship)) {
                return false
            }
            if (ship.type == ShipType.COORDINATING_SHIP && !checkCoordinatingShip(ship)) {
                return false
            }
        }
        return shipsList.isNotEmpty()
    }

    private fun checkScoutingShip(ship: Ship): Boolean {
        return ship.maxVelocity in MIN_VELOCITY..MAX_VELOCITY &&
            ship.acceleration in MIN_ACCELERATION..MAX_ACCELERATION &&
            ship.maxFuelCapacity >= MIN_FUEL_CAPACITY &&
            ship.currentFuel <= MAX_FUEL_CAPACITY &&
            ship.fuelConsumptionRate in MIN_FUEL_CONSUMPTION..MAX_FUEL_CONSUMPTION &&
            ship.visibilityRange in MIN_VISIBILITY_RANGE..MAX_VISIBILITY_RANGE
    }

    private fun checkCoordinatingShip(ship: Ship): Boolean {
        return ship.maxVelocity in MIN_COORDINATING_VELOCITY..MAX_COORDINATING_VELOCITY &&
            ship.acceleration in MIN_COORDINATING_ACCELERATION..MAX_COORDINATING_ACCELERATION &&
            ship.maxFuelCapacity >= MIN_COORDINATING_FUEL_CAPACITY &&
            ship.currentFuel <= MAX_COORDINATING_FUEL_CAPACITY &&
            ship.fuelConsumptionRate in MIN_COORDINATING_FUEL_CONSUMPTION..MAX_COORDINATING_FUEL_CONSUMPTION &&
            ship.visibilityRange == MAX_COORDINATING_VISIBILITY_RANGE
    }

    private fun checkCollectingShip(ship: Ship): Boolean {
        var success = true
        if (ship.capacityInfo.isEmpty()) {
            return false
        }
        if (ship.capacityInfo[GarbageType.PLASTIC] != null) {
            success = success &&
                ship.capacityInfo[GarbageType.PLASTIC]?.first in MIN_PLASTIC_CAPACITY..MAX_PLASTIC_CAPACITY
        }
        if (ship.capacityInfo[GarbageType.CHEMICALS] != null) {
            success = success &&
                ship.capacityInfo[GarbageType.CHEMICALS]?.first in MIN_CHEMICALS_CAPACITY..MAX_CHEMICALS_CAPACITY
        }
        if (ship.capacityInfo[GarbageType.OIL] != null) {
            success = success &&
                ship.capacityInfo[GarbageType.OIL]?.first in MIN_OIL_CAPACITY..MAX_OIL_CAPACITY
        }
        return ship.maxVelocity in MIN_COLLECTING_VELOCITY..MAX_COLLECTING_VELOCITY &&
            ship.acceleration in MIN_COLLECTING_ACCELERATION..MAX_COLLECTING_ACCELERATION &&
            ship.maxFuelCapacity >= MIN_COLLECTING_FUEL_CAPACITY &&
            ship.currentFuel <= MAX_COLLECTING_FUEL_CAPACITY &&
            ship.fuelConsumptionRate in MIN_COLLECTING_FUEL_CONSUMPTION..MAX_COLLECTING_FUEL_CONSUMPTION &&
            ship.visibilityRange == 0 && success
    }

    /**
     * Validates that all ships listed in the corporation's JSON array belong to the specified corporation.
     *
     * This function iterates through each ship ID in the provided
     * `corporationShips` JSON array and performs two checks:
     * 1. Ensures that each ship ID exists in the `shipsList`.
     * 2. Ensures that each ship with the given ship ID belongs to the specified `corporationID`.
     *
     * If any ship ID does not exist in the `shipsList` or
     * belongs to a different corporation, the function returns `false`.
     * Otherwise, it returns `true`.
     *
     * @param corporationShips A JSON array containing the ship IDs associated with the corporation.
     * @param shipsList A list of `Ship` objects representing all parsed ships.
     * @param corporationID The ID of the corporation to which the ships should belong.
     * @return `true` if all ships in the
     * `corporationShips` array belong to the specified corporation, `false` otherwise.
     */
    private fun validateShipsOfCorporation(
        corporationShips: JSONArray,
        shipsList: List<Ship>,
        corporationID: Int
    ): Boolean {
        val corporationShipIds = mutableSetOf<Int>()
        for (index in 0 until corporationShips.length()) {
            val shipId = corporationShips.getInt(index)
            // The line checks if there are no ships in the shipsList (ships we parsed) with the given
            // shipId (ships owned by corporation).
            val shipWithIdDoesNotExistInCorporation = shipsList.none { it.id == shipId }

            // checks if there is any ship in the shipsList with the given
            // shipId that does not belong to the specified corporationID.
            val shipWithIdIsOfCorrectCorporation = shipsList.any { it.id == shipId && it.corporation != corporationID }

            if (shipWithIdDoesNotExistInCorporation || shipWithIdIsOfCorrectCorporation) {
                return false
            }
            corporationShipIds.add(shipId)
        }

        for (ship in shipsList) {
            if (ship.corporation == corporationID && !corporationShipIds.contains(ship.id)) {
                return false
            }
        }
        return true
    }

    /**
     * Validates that the collecting ships of a corporation can handle the specified garbage types.
     *
     * This function performs two main checks:
     * 1. Ensures that for each garbage type in the `garbageList`, there is at least one collecting ship
     *    in the `collectingShips` list that can handle that garbage type.
     * 2. Ensures that each collecting ship in the `collectingShips` list does not handle any garbage type
     *    that is not listed in the `garbageList` for the specified corporation.
     *
     * @param collectingShips A list of `Ship` objects representing the collecting ships.
     * @param garbageList A list of `GarbageType` objects representing the garbage types the corporation can handle.
     * @param corporationId The ID of the corporation to which the ships should belong.
     * @return `true` if all collecting ships can handle the specified garbage types
     * and do not handle any unspecified types, `false` otherwise.
     */
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
        if (corporationNames.contains(corporationName)) {
            log.error("CORPORATION PARSER: Duplicate corporation name found: $corporationName")
            return false
        }
        corporationNames.add(corporationName)
        return !corporationIds.contains(corporationId) && corporationName.isNotEmpty()
    }
}
