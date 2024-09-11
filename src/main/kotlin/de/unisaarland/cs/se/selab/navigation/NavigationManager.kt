package de.unisaarland.cs.se.selab.navigation

import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Tile

class NavigationManager(
    var tiles : Map<Pair<Int,Int>,Tile>
) {
    /**
     * gets the travel distance between two tiles (maybe useful for returning to Harbor)
     * @param t1 : the first tile
     * @param t2 : the second tile
     **/
    fun travelDistance(t1: Tile, t2: Tile): Int {
        return 0
    }

    /**
    * Find the shortest path between current location and a set of locations
     * and return the coordinates to land on given the travel amount
     * @param from : the current location
     * @param to : the set of locations to reach
     * @param travelAmount : the amount of travel available
     * @return the coordinates to land on and the distance to travel plus the tileId
    **/
    fun shortestPathToLocations(
        from: Pair<Int, Int>,
        to: List<Pair<Int, Int>>,
        travelAmount: Int
    ): Pair<Pair<Int, Int>, Int> {
        return Pair(from, 0)
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
        return from
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
        // Implementation here
        return from
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
        return false
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
    ): Map<Int,Pair<Int, Int>> {
        // Implementation here
        return mapOf()
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
        // Implementation here
        return listOf()
    }



}