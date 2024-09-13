package de.unisaarland.cs.se.selab.navigation


import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import java.util.PriorityQueue
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


/**
 * The NavigationManager class, this takes care of routing from location to location,
 * as well as helpful functions on the map
 */
class NavigationManager(
    var tiles: Map<Pair<Int, Int>, Tile>
) {
    lateinit var graph: MutableMap<Int, List<Pair<Int, Pair<Boolean, Boolean>>>>
    companion object {
        private const val DEFAULT_DISTANCE = 10
    }
    private val logger = LogFactory.getLog("exception_info")

    /**
     * Initializes graph structure, goes through each neighbor of a tile by location and adds neighboring tileID's
     * as well as whether the Tile is actually traversable
     */
    fun initializeAndUpdateGraphStructure() {
        for ((location, tile) in tiles) {
            val neighbors = getHexNeighbors(location.first, location.second)
            for ((x, y) in neighbors) {
                val newMutableList: MutableList<Pair<Int, Pair<Boolean, Boolean>>> = mutableListOf()
                val neighborTile = findTile(Pair(x, y))
                if (neighborTile != null) {
                    val isRestricted = neighborTile.isRestricted
                    val isLand = neighborTile.type == TileType.LAND
                    newMutableList.add(Pair(neighborTile.id, Pair(isLand, isRestricted)))
                }
                graph[tile.id] = newMutableList.toList()
            }
        }
    }

    /**
     * Gets list of neighbor locations for a location within radius 1
     * @param x: the x coordinate
     * @param y: the y coordinate
     * @return a list of possible neighbor locations
     */
    fun getHexNeighbors(x: Int, y: Int): List<Pair<Int, Int>> {
        return if (y % 2 == 0) {
            listOf(
                Pair(x + 1, y), // Right
                Pair(x - 1, y), // Left
                Pair(x, y + 1), // Bottom left
                Pair(x, y - 1), // Top left
                Pair(x + 1, y - 1), // Top right
                Pair(x + 1, y + 1)
            ) // Bottom right
        } else {
            listOf(
                Pair(x + 1, y), // Right
                Pair(x - 1, y), // Left
                Pair(x - 1, y + 1), // Bottom left
                Pair(x - 1, y - 1), // Top left
                Pair(x, y - 1), // Top right
                Pair(x, y + 1)
            ) // Bottom right
        }
    }

    /**
     * gets the travel distance between two tiles (maybe useful for returning to Harbor)
     * @param from : the tile from which we want to calculate the distance
     * @param to : the tile to which we want to calculate the distance
     **/
    fun travelDistance(from: Tile, to: Tile): Int {
        val tileIDTo = to.id
        val (distances, _) = dijkstra(graph, from.id)
        return distances[tileIDTo] ?: -1
    }

    /**
     * Find the shortest path between current location and a set of locations
     * and return the coordinates to land on given the travel amount
     * @param from : the current location
     * @param to : the set of locations to reach
     * @param travelAmount : the amount of travel available
     * @return the coordinates to land on and the distance to travel plus the tileId plus the distance to travel
     **/
    fun shortestPathToLocations(
        from: Pair<Int, Int>,
        to: List<Pair<Int, Int>>,
        travelAmount: Int
    ): Pair<Pair<Pair<Int, Int>, Int>, Int> {
        // Run dijkstra from the current location
        val tileIdOfLocation = tiles.getValue(from).id
        val (distances, previousNodes) = dijkstra(graph, tileIdOfLocation)

        // Filter the possible locations by the distances to the origin and return the lowest tileId
        // If there is no location to travel to, return current location
        val tileIDLocationToTravelTo = filterPossibleLocationsByDistancesToOriginAndReturnLowestTileId(to, distances)
        if (tileIDLocationToTravelTo == -1) return Pair(Pair(from, tileIdOfLocation), 0)

        // Get the location of the tile to travel to
        val tileLocationToTravelTo = locationByTileId(tileIDLocationToTravelTo)
            ?: return Pair(Pair(from, tileIdOfLocation), 0)

        // Get the path length to the destination tile
        // We can use !!, as we know that the tileID is in the distances map
        val pathLength = distances[tileIDLocationToTravelTo]!!.div(DEFAULT_DISTANCE)
        // Calculate the amount of tiles we need to go back in the path
        val goBackInPathByAmountOfTile = pathLength.minus(travelAmount)
        // Check if we can reach the destination tile with the given travelAmount
        // If we can reach the destination tile, return the destination tile and the distance to travel
        if (goBackInPathByAmountOfTile <= 0) {
            return Pair(
                Pair(tileLocationToTravelTo, tileIDLocationToTravelTo),
                distances[tileIDLocationToTravelTo] ?: -1
            )
        }

        // Get the node to travel to, in case we cant travel the whole amount
        var node = tileIDLocationToTravelTo
        (0..<goBackInPathByAmountOfTile).forEach { _ ->
            node = previousNodes[node] ?: return Pair(Pair(from, tileIdOfLocation), 0)
        }
        val locationOfDestinationTile = locationByTileId(node) ?: return Pair(Pair(from, tileIdOfLocation), 0)
        return Pair(Pair(locationOfDestinationTile, node), distances[node] ?: 0)
    }

    /**
     * Filter the possible locations by the distances to the origin and return the lowest tileId
     * @param locations: the destinations to filter
     * @param distances: the distances from the origin
     * @return the tileId of the location to travel to
     */
    private fun filterPossibleLocationsByDistancesToOriginAndReturnLowestTileId(
        locations: List<Pair<Int, Int>>,
        distances: Map<Int, Int>
    ): Int {
        val possibleLocations: MutableList<Int> = mutableListOf() // list of tileID's that are possible to travel to
        var minDistanceYet: Int = Int.MAX_VALUE
        // Go through all locations that we should travel to, update the minDistanceYet and possibleLocations according
        // to the distances from the origin
        for (location in locations) {
            val tile = findTile(location) ?: continue
            val distanceFromStartPointToLocationTile = distances.getOrDefault(tile.id, Int.MAX_VALUE)
            if (distanceFromStartPointToLocationTile < minDistanceYet) {
                minDistanceYet = distanceFromStartPointToLocationTile
                possibleLocations.clear()
                possibleLocations.add(tile.id)
            } else if (
                distanceFromStartPointToLocationTile == minDistanceYet &&
                distanceFromStartPointToLocationTile != Int.MAX_VALUE
            ) {
                possibleLocations.add(tile.id)
            }
        }
        // Return the tileID of the location to travel to
        return try {
            val tileIDLocationToTravelTo = possibleLocations.minBy { it }
            tileIDLocationToTravelTo
        } catch (e: NoSuchElementException) {
            logger.debug("No possible location to travel to")
             -1 // None of the locations can be reached
        }
    }

    /**
     * A node class to wrap around a Tile (used for priority queue)
     */
    data class Node(val id: Int, val distance: Int) : Comparable<Node> {
        override fun compareTo(other: Node): Int {
            return if (this.distance == other.distance) {
                this.id - other.id
            } else {
                this.distance - other.distance
            }
        }
    }

    /**
     * The dijkstra algorithm given a graph and a source
     * @param graph: The overall graph structure, mapping from tileId to tildeId of neighbor
     * and whether it is restricted
     * @param source: The tileID of the source node from where the dijkstra will start
     */
    private fun dijkstra(
        graph: Map<Int, List<Pair<Int, Pair<Boolean, Boolean>>>>,
        source: Int,
        outOfRestriction: Boolean = false,
        toSpecificLocation: Pair<Int, Int>? = null
    ): Pair<Map<Int, Int>, Map<Int, Int?>> {
        val distances = mutableMapOf<Int, Int>().withDefault { Int.MAX_VALUE }
        val previousNodes = mutableMapOf<Int, Int?>()
        val priorityQueue = PriorityQueue<Node>()

        distances[source] = 0
        priorityQueue.add(Node(source, 0))

        while (priorityQueue.isNotEmpty()) {
            val currentNode = priorityQueue.poll()
            val currentDistance = currentNode.distance

            if (currentDistance > distances.getValue(currentNode.id)) continue
            // If we are looking for a specific location, break if we found it
            if (toSpecificLocation != null && currentNode.id == toSpecificLocation.first) {
                break
            }

            for ((neighbor, notTraversable) in graph.getValue(currentNode.id)) {
                val isLand = notTraversable.first
                val isRestricted = notTraversable.second
                val isNormalTile = !isLand && !isRestricted
                if (isLand ||
                    (isRestricted && !outOfRestriction) ||
                    (isNormalTile && outOfRestriction)
                    ) continue
                val newDistance = currentDistance + DEFAULT_DISTANCE

                if (newDistance < distances.getValue(neighbor) ||
                    (newDistance == distances.getValue(neighbor) && neighbor < currentNode.id)
                ) {
                    distances[neighbor] = newDistance
                    previousNodes[neighbor] = currentNode.id
                    priorityQueue.add(Node(neighbor, newDistance))
                }
            }
        }
        return distances to previousNodes
    }

    /**
     * Get the furthest tile possible given travelAmount and a location (if there are
     * multiple furthest points we sort by lowest tileId)
     * @param from : the current location
     * @param travelAmount : the amount of travel available
     */
    fun getExplorePoint(
        from: Pair<Int, Int>,
        travelAmount: Int
    ): Pair<Int, Int> {
        var tilesInRadiusOfTravel: List<Pair<Int, Int>> = listOf()
        var currentSearchRadius = travelAmount
        // we always get a ring around the current location and filter by the tiles that are not land and not restricted
        // if there are no such tiles, we shrink the radius and try again
        while (tilesInRadiusOfTravel.isEmpty()) {
            if (currentSearchRadius == 0) {
                return from
            }
            tilesInRadiusOfTravel = getRingOfRadius(from, currentSearchRadius)
            tilesInRadiusOfTravel = tilesInRadiusOfTravel.filter { findTile(it) != null }
            tilesInRadiusOfTravel = tilesInRadiusOfTravel.filter { findTile(it)?.isRestricted == false }
            tilesInRadiusOfTravel = tilesInRadiusOfTravel.filter { findTile(it)?.type != TileType.LAND }
            currentSearchRadius -= 1
        }
        val minTileIdLocationToExplore = tilesInRadiusOfTravel.minByOrNull {
            findTile(it)!!.id
        } ?: return from // again, we can use !! here because we already filtered by tiles that dont exist
        return minTileIdLocationToExplore
    }

    /**
     * Get the destination out of restriction given a location and travelAmount
     * @param from : the current location
     * @param travelAmount : the amount of travel available
     * @return the point that would get the ship out of the restriction the fastest
     * given the travelAmount
     */
    fun getDestinationOutOfRestriction(
        from: Pair<Int, Int>,
        travelAmount: Int
    ): Pair<Int, Int> {
        // Run dijkstra from the current location only considering only restricted tiles
        val tileIDOfLocation = tiles.getValue(from).id
        val (distances, previousNodes) = dijkstra(graph, tileIDOfLocation, true)
        // Filter the found locations by the ones that are next to a non-restricted tile which is not land
        val distancesFilteredByNotMaxValue = distances.filter { it.value < Int.MAX_VALUE }
        val distancesFilteredByTileNextToNonRestrictedTile = distancesFilteredByNotMaxValue.filter {
            val neighbors = graph[it.key] ?: return from
            neighbors.any { neighbor ->
                !neighbor.second.first && !neighbor.second.second // not land and not restricted
            }
        }
        // Check if there are no locations from which we could leave the restriction
        if (distancesFilteredByTileNextToNonRestrictedTile.isEmpty()) return from
        // Get lowest tile id, if there are multiple points with same distance that are next to a non-restricted tile
        val minTileIdLocationToExplore = distancesFilteredByTileNextToNonRestrictedTile.minByOrNull { it.key } ?: return from
        // Get the path length to the destination tile
        val pathLength = minTileIdLocationToExplore.value / DEFAULT_DISTANCE
        // Compute how many tiles we have to go back in the path
        val goBackInPathByAmountOfTile = pathLength.minus(travelAmount)
        // Get the non-restricted tile with lowest tileID from the point of which we want to leave the restriction
        val neighborWithLowestTileID = graph[minTileIdLocationToExplore.key]!!.minByOrNull { it.first } ?: return from

        // If we can reach the destination tile, return the destination tile
        if (goBackInPathByAmountOfTile <= 0) return locationByTileId(neighborWithLowestTileID.first) ?: return from

        // Get the node to travel to, in case we cant travel the whole amount
        var node = neighborWithLowestTileID.first
        (0..<goBackInPathByAmountOfTile).forEach { _ ->
            node = previousNodes[node] ?: return from
        }
        return locationByTileId(node) ?: return from
    }

    /**
     * Get a tile object from a given location
     * @param coords : the coordinates of the tile
     * @return the tile object at the location or null
     */
    fun findTile(coords: Pair<Int, Int>): Tile? {
        return tiles[coords]
    }

    /**
     * Get a tile object from a given tileID
     * @param tileID: the tileID of the tile
     * @return the tile object at the location or null
     */
    fun findTile(tileId: Int): Tile? {
        for ((_, tile) in tiles) {
            if (tile.id == tileId) return tile
        }
        return null
    }

    /**
     * Gets the location of a tile, by its tileid
     * @param tileId : the tileId of the tile
     * @return the location of the tile
     */
    fun locationByTileId(tileId: Int): Pair<Int, Int>? {
        for ((_, tile) in tiles) {
            if (tile.id == tileId) return tile.location
        }
        return null
    }

    /**
     * Checks if a ship has to go back from its current location
     * to the harbor given it could move maxDistance
     * amount of travel in this tick along its path given the available harbors
     * @param shipLocation : the current location of the ship
     * @param maxDistance : the amount of maximum travel available
     * @param homeHarbors : the harbors the ship can go back to
     * @return true if the ship should go back to the harbor
     */
    fun shouldMoveToHarbor(
        shipLocation: Pair<Int, Int>,
        maxDistance: Int,
        homeHarbors: List<Pair<Int, Int>>
    ): Boolean {
        // Run dijkstra from the current location
        val shipTile = findTile(shipLocation) ?: return false
        val (distances, _) = dijkstra(graph, shipTile.id)
        val tileIDLocationToTravelTo =
            filterPossibleLocationsByDistancesToOriginAndReturnLowestTileId(homeHarbors, distances)
        if (tileIDLocationToTravelTo == -1) return false
        // Check if the distance to the destination tile is greater than the maxDistance with current Fuel
        val pathLength = (distances[tileIDLocationToTravelTo]!!)
        return pathLength > maxDistance
    }

    /**
     * Calculate the tile (also return the path in case garbage gets drifted we would
     * need all the tiles in the path) which the drifted object should land on taking
     * into account current
     * @param location : the current location of the object
     * @param direction : the direction of the drift
     * @param speed : the speed of the drift
     * @return the tile (path) where the object should land (path is numbered
     * 0...n, with 0 being the furthest away tile and n being the closest tile)
     */
    fun calculateDrift(
        location: Pair<Int, Int>,
        direction: Direction,
        speed: Int
    ): List<Tile> {
        val pathMap: MutableList<Tile> = mutableListOf()
        var newTile = location
        val maxDistance = speed / DEFAULT_DISTANCE
        // Calculate the path of the drift by going in the direction of the drift
        for (i in 0..<maxDistance) {
            newTile = findTileInDirectionFrom(newTile, direction)
            val tileObject = findTile(newTile) ?: break
            if (tileObject.type == TileType.LAND) break
            pathMap.add(tileObject)
        }
        pathMap.reverse()
        return pathMap
    }

    /**
     * Find the tile in a given direction from a location
     * @param location : the location to start from
     * @param direction : the direction to go to
     * @return the tile in the direction
     */
    private fun findTileInDirectionFrom(location: Pair<Int, Int>, direction: Direction): Pair<Int, Int> {
        when (direction) {
            Direction.EAST -> return Pair(location.first + 1, location.second)
            Direction.WEST -> return Pair(location.first - 1, location.second)
            Direction.NORTH_EAST ->
                return if (location.second % 2 == 0) {
                    Pair(location.first + 1, location.second - 1)
                } else {
                    Pair(location.first, location.second - 1)
                }
            Direction.NORTH_WEST ->
                return if (location.second % 2 == 0) {
                    Pair(location.first, location.second - 1)
                } else {
                    Pair(location.first - 1, location.second - 1)
                }
            Direction.SOUTH_WEST ->
                return if (location.second % 2 == 0) {
                    Pair(location.first, location.second + 1)
                } else {
                    Pair(location.first - 1, location.second + 1)
                }
            Direction.SOUTH_EAST ->
                return if (location.second % 2 == 0) {
                    Pair(location.first + 1, location.second + 1)
                } else {
                    Pair(location.first, location.second + 1)
                }
        }
    }

    /**
     * Get the tiles in a given radius around a location
     * @param location : the location to get the tiles around
     * @param radius : the radius around the location
     * @return the list of tiles in the radius
     */
    fun getTilesInRadius(
        location: Pair<Int, Int>,
        radius: Int
    ): List<Pair<Int, Int>> {
        // We use a set here, as we get the neighbors of neighbors, and we don't want duplicates
        val tilesInRadius: MutableSet<Pair<Int, Int>> = mutableSetOf()
        tilesInRadius.add(location)
        var i = 0
        while (i < radius) {
            val newTiles: MutableSet<Pair<Int, Int>> = mutableSetOf()
            for (tile in tilesInRadius) {
                val neighbors = getHexNeighbors(tile.first, tile.second)
                for (neighbor in neighbors) {
                    newTiles.add(neighbor)
                }
            }
            i += 1
            tilesInRadius.addAll(newTiles)
        }

        return tilesInRadius.toList()
    }

    /**
     * Get the ring of a given radius around a location
     * @param location : the location to get the ring around
     * @param radius : the radius of the ring
     * @return the list of tiles in the ring
     */
    fun getRingOfRadius(location: Pair<Int, Int>, radius: Int): List<Pair<Int, Int>> {
        // We remove the tiles in the radius-1 from the radius to get the ring
        var ring: MutableSet<Pair<Int, Int>> = mutableSetOf()
        val tilesInRadius = getTilesInRadius(location, radius)
        val tilesInRadiusMinusOne = getTilesInRadius(location, radius - 1)
        ring = tilesInRadius.filter { it !in tilesInRadiusMinusOne }.toMutableSet()
        return ring.toList()
    }

    /**
     * Retrieves garbage from all tiles in the correct order for drifting.
     *
     * This function iterates through all tiles, checks if there is any garbage left on each tile,
     * and collects the garbage in a list of pairs. Each pair contains the tile ID and a list of garbage
     * items sorted by their IDs.
     *
     * @return A list of pairs where each pair consists of a tile ID and a list of garbage items.
     *         The list is sorted by tile IDs in ascending order.
     */
    fun getGarbageFromAllTilesInCorrectOrderForDrifting(): List<Pair<Int, List<Garbage>>> {
        val outputList: MutableList<Pair<Int, List<Garbage>>> = mutableListOf()
        val tileList = tiles.values.toList().sortedBy { it.id }
        for (tile in tileList) {
            if (!tile.checkGarbageLeft()) continue
            outputList.add(Pair(tile.id, tile.getGarbageByLowestID()))
        }
        return outputList
    }
}
