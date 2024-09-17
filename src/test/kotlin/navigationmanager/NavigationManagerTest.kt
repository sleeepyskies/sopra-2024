package navigationmanager

import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.times
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Class holding the tests for the NavigationManager class.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NavigationManagerTest {
    // navigation manager for testing
    private lateinit var nm: NavigationManager

    // mock tile for - REDACTED
    // used as default value
    private val mockTile = Tile(-1, Pair(-1, -1), TileType.LAND, false, Current(Direction.EAST, -1, -1), false, -1)

    /**
     * Sets up the map for the tests. Map is based on the map on page 36 in the specification.
     */
    @BeforeEach
    fun setup() {
        // mock current for tiles with no current
        val current = Current(Direction.EAST, 0, 0)

        // current for t1
        val currentT1 = Current(Direction.SOUTH_EAST, 5, 1)

        // current for t6
        val currentT6 = Current(Direction.EAST, 5, 3)

        // row 0
        val t1 = Tile(1, Pair(0, 0), TileType.DEEP_OCEAN, false, currentT1, true, 1000)
        val t2 = Tile(2, Pair(1, 0), TileType.SHALLOW_OCEAN, false, current, false, 1000)
        val t3 = Tile(3, Pair(2, 0), TileType.SHORE, false, current, false, 1000)
        val t4 = Tile(4, Pair(3, 0), TileType.SHORE, false, current, false, 1000)
        val t5 = Tile(5, Pair(4, 0), TileType.SHORE, true, current, false, 1000)

        // row 1
        val t6 = Tile(6, Pair(0, 1), TileType.DEEP_OCEAN, false, currentT6, true, 1000)
        val t7 = Tile(7, Pair(1, 1), TileType.SHALLOW_OCEAN, false, current, false, 1000)
        val t8 = Tile(8, Pair(2, 1), TileType.SHORE, false, current, false, 1000)
        val t9 = Tile(9, Pair(3, 1), TileType.LAND, false, current, false, 1000)
        val t10 = Tile(10, Pair(4, 1), TileType.LAND, false, current, false, 1000)

        // row 2
        val t11 = Tile(11, Pair(0, 2), TileType.SHALLOW_OCEAN, false, current, false, 1000)
        val t12 = Tile(12, Pair(1, 2), TileType.SHORE, false, current, false, 1000)
        val t13 = Tile(13, Pair(2, 2), TileType.LAND, false, current, false, 1000)
        val t14 = Tile(14, Pair(3, 2), TileType.SHORE, false, current, false, 1000)
        val t15 = Tile(15, Pair(4, 2), TileType.SHORE, true, current, false, 1000)

        // row 3
        val t16 = Tile(16, Pair(0, 3), TileType.SHALLOW_OCEAN, false, current, false, 1000)
        val t17 = Tile(17, Pair(1, 3), TileType.SHORE, false, current, false, 1000)
        val t18 = Tile(18, Pair(2, 3), TileType.LAND, false, current, false, 1000)
        val t19 = Tile(19, Pair(3, 3), TileType.SHORE, false, current, false, 1000)
        val t20 = Tile(20, Pair(4, 3), TileType.LAND, false, current, false, 1000)

        // row 4
        val t21 = Tile(21, Pair(0, 4), TileType.SHORE, false, current, false, 1000)
        val t22 = Tile(22, Pair(1, 4), TileType.LAND, false, current, false, 1000)
        val t23 = Tile(23, Pair(2, 4), TileType.SHORE, false, current, false, 1000)
        val t24 = Tile(24, Pair(3, 4), TileType.LAND, false, current, false, 1000)
        val t25 = Tile(25, Pair(4, 4), TileType.SHORE, false, current, false, 1000)

        val map = mapOf(
            // row 0
            Pair(0, 0) to t1,
            Pair(1, 0) to t2,
            Pair(2, 0) to t3,
            Pair(3, 0) to t4,
            Pair(4, 0) to t5,

            // row 1
            Pair(0, 1) to t6,
            Pair(1, 1) to t7,
            Pair(2, 1) to t8,
            Pair(3, 1) to t9,
            Pair(4, 1) to t10,

            // row 2
            Pair(0, 2) to t11,
            Pair(1, 2) to t12,
            Pair(2, 2) to t13,
            Pair(3, 2) to t14,
            Pair(4, 2) to t15,

            // row 3
            Pair(0, 3) to t16,
            Pair(1, 3) to t17,
            Pair(2, 3) to t18,
            Pair(3, 3) to t19,
            Pair(4, 3) to t20,

            // row 4
            Pair(0, 4) to t21,
            Pair(1, 4) to t22,
            Pair(2, 4) to t23,
            Pair(3, 4) to t24,
            Pair(4, 4) to t25
        )
        this.nm = NavigationManager(map)

        // init the graph
        this.nm.initializeAndUpdateGraphStructure()
    }

    /**
     * Sets the restriction status of all tiles in map to false
     */
    @AfterEach
    fun resetRestrictions() {
        for (tile in this.nm.tiles.values) {
            tile.isRestricted = false
        }
        this.nm.initializeAndUpdateGraphStructure()
    }

    /**
     * Helper method to restrict the given tile. Updates the graph accordingly
     */
    private fun restrictTile(tileLocation: Pair<Int, Int>) {
        val tile = this.nm.tiles[tileLocation] ?: this.mockTile
        tile.isRestricted = true
        this.nm.initializeAndUpdateGraphStructure()
    }

    /**
     * Accesses the NavigationManagers private field graph and returns the neighbor list for the given tile.
     * @param tileID the ID of the tile to get the neighbors from
     * @return the list of neighbors for the given tile
     */
    private fun getNeighborListForTile(tileID: Int): List<Pair<Int, Pair<Boolean, Boolean>>> {
        // get access to the private graph field
        val graphField = this.nm::class.java.getDeclaredField("graph")
        graphField.isAccessible = true
        val graph = graphField.get(this.nm) as Map<Int, List<Pair<Int, Pair<Boolean, Boolean>>>>
        return graph[tileID] ?: emptyList()
    }

    /**
     * Calls the dijkstra method of the NavigationManager class for the given tileID.
     */
    private fun callDijkstraForTileID(tileID: Int): Pair<Map<Int, Int>, Map<Int, Int?>> {
        // access dijkstra method
        val dijkstra = nm::class.java.getDeclaredMethod(
            "dijkstra",
            Map::class.java,
            Int::class.java,
            Boolean::class.java,
            Pair::class.java
        )
        dijkstra.isAccessible = true

        // get the navigation managers graph
        // get access to the private graph field
        val graphField = this.nm::class.java.getDeclaredField("graph")
        graphField.isAccessible = true
        val graph = graphField.get(this.nm) as Map<Int, List<Pair<Int, Pair<Boolean, Boolean>>>>

        // call dijkstra method
        return dijkstra.invoke(
            nm,
            graph,
            tileID,
            false,
            null
        ) as Pair<Map<Int, Int>, Map<Int, Int?>>
    }

    // ----------------------------------- initializeAndUpdateGraphStructure() tests -----------------------------------

    @Test
    fun initializeAndUpdateGraphStructureTest1() {
        // tile 1 - (0, 0)
        val checkValue = listOf(
            Pair(2, Pair(false, false)),
            Pair(6, Pair(false, false)),
            Pair(7, Pair(false, false))
        )

        // find the neighbors of tile 1
        val t1neighbors = getNeighborListForTile(1)

        assertEquals<List<Pair<Int, Pair<Boolean, Boolean>>>>(checkValue, t1neighbors)
    }

    @Test
    fun initializeAndUpdateGraphStructureTest2() {
        // tile 25 - (4, 4)
        val checkValue = listOf(
            Pair(20, Pair(true, false)),
            Pair(24, Pair(true, false))
        )

        // find the neighbors of tile 1
        val t1neighbors = getNeighborListForTile(25)

        assertEquals<List<Pair<Int, Pair<Boolean, Boolean>>>>(checkValue, t1neighbors)
    }

    @Test
    fun initializeAndUpdateGraphStructureTest3() {
        // tile 14 - (3, 2)
        val checkValue = listOf(
            Pair(9, Pair(true, false)),
            Pair(10, Pair(true, false)),
            Pair(13, Pair(true, false)),
            Pair(15, Pair(false, false)),
            Pair(19, Pair(false, false)),
            Pair(20, Pair(true, false))
        )

        // find the neighbors of tile 1
        val t1neighbors = getNeighborListForTile(14)

        assertEquals<List<Pair<Int, Pair<Boolean, Boolean>>>>(checkValue, t1neighbors)
    }

    @Test
    fun initializeAndUpdateGraphStructureTest4() {
        // tile 14 - (3, 2)
        val checkValue = listOf(
            Pair(9, Pair(true, true)),
            Pair(10, Pair(true, false)),
            Pair(13, Pair(true, false)),
            Pair(15, Pair(false, true)),
            Pair(19, Pair(false, true)),
            Pair(20, Pair(true, false))
        )

        // restrict some neighbors
        restrictTile(Pair(3, 1)) // tile 9
        restrictTile(Pair(4, 2)) // tile 15
        restrictTile(Pair(3, 3)) // tile 19

        // find the neighbors of tile 1
        val t1neighbors = getNeighborListForTile(14)

        assertEquals<List<Pair<Int, Pair<Boolean, Boolean>>>>(checkValue, t1neighbors)
    }

    // ------------------------------------------ getHexNeighbors() tests ------------------------------------------
    @Test
    fun getHexNeighborsTest1() {
        val x = 1
        val y = 2
        val validResult = listOf(
            Pair(2, 2),
            Pair(0, 2),
            Pair(1, 3),
            Pair(1, 1),
            Pair(2, 1),
            Pair(2, 3)
        )
        assertEquals(validResult, nm.getHexNeighbors(x, y))
    }

    @Test
    fun getHexNeighborsTest2() {
        val x = 1
        val y = 3
        val validResult = listOf(
            Pair(2, 3),
            Pair(0, 3),
            Pair(0, 4),
            Pair(0, 2),
            Pair(1, 2),
            Pair(1, 4)
        )
        assertEquals(validResult, nm.getHexNeighbors(x, y))
    }

    // ------------------------------------------ travelDistance() tests ------------------------------------------

    @Test
    fun travelDistanceTest1() {
        // get 2 neighboring tiles
        val t1 = this.nm.tiles[Pair(0, 0)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(1, 0)] ?: this.mockTile

        // call travelDistance()
        val result = nm.travelDistance(t1, t2)

        assertEquals(10, result)
    }

    @Test
    fun travelDistanceTest2() {
        // get 2 tiles that are not neighbors
        val t1 = this.nm.tiles[Pair(0, 0)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(4, 0)] ?: this.mockTile

        // call travelDistance()
        val result = nm.travelDistance(t1, t2)

        assertEquals(40, result)
    }

    @Test
    fun travelDistanceTest3() {
        // get tiles that are far away
        val t1 = this.nm.tiles[Pair(2, 4)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(4, 2)] ?: this.mockTile

        // call travelDistance()
        val result = nm.travelDistance(t1, t2)

        assertEquals(30, result)
    }

    @Test
    fun travelDistanceTest4() {
        // get tiles enclosed in LAND tiles
        val t1 = this.nm.tiles[Pair(0, 3)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(4, 0)] ?: this.mockTile

        // call travelDistance()
        val result = nm.travelDistance(t1, t2)

        assertEquals(60, result)
    }

    @Test
    fun travelDistanceTest5() {
        // get 2 tiles with no route due to LAND tiles
        val t1 = this.nm.tiles[Pair(4, 4)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(3, 3)] ?: this.mockTile

        // call travelDistance()
        val result = nm.travelDistance(t1, t2)

        assertEquals(-1, result)
    }

    @Test
    fun travelDistanceTest6() {
        // get tiles with no route due to restrictions
        val t1 = this.nm.tiles[Pair(2, 4)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(4, 2)] ?: this.mockTile

        // set restriction
        restrictTile(Pair(3, 3))

        // call travelDistance()
        val result = nm.travelDistance(t1, t2)

        assertEquals(-1, result)
    }

    @Test
    fun travelDistanceTest7() {
        // get tiles, t1 is a land tile
        val t1 = this.nm.tiles[Pair(1, 4)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(4, 2)] ?: this.mockTile

        // call travelDistance()
        val result = nm.travelDistance(t1, t2)

        assertEquals(-1, result)
    }

    @Test
    fun travelDistanceTest8() {
        // get tiles, t2 is a land tile
        val t1 = this.nm.tiles[Pair(1, 4)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(3, 4)] ?: this.mockTile

        // call travelDistance()
        val result = nm.travelDistance(t1, t2)

        assertEquals(-1, result)
    }

    @Test
    fun travelDistanceTest9() {
        // get tiles, t1 is restricted
        val t1 = this.nm.tiles[Pair(2, 4)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(4, 2)] ?: this.mockTile

        // set restriction
        restrictTile(Pair(2, 4))

        // call travelDistance()
        val result = nm.travelDistance(t1, t2)

        assertEquals(-1, result)
    }

    @Test
    fun travelDistanceTest10() {
        // get the same tile
        val t1 = this.nm.tiles[Pair(1, 4)] ?: this.mockTile

        // call travelDistance()
        val result = nm.travelDistance(t1, t1)

        assertEquals(0, result)
    }

    // --------------------------------------- shortestPathToLocations() tests ---------------------------------------

    @Test
    fun shortestPathToLocationsTest1() {
        // define check value
        val checkValue = Pair(Pair(Pair(1, 0), 2), 10)

        // get start location and list of one neighbor
        val start = Pair(0, 0)
        val t1 = Pair(1, 0)
        val destinations = listOf(t1)

        // call shortestPathToLocations(), enough travelAmount to reach destination
        val result = nm.shortestPathToLocations(start, destinations, 1)

        assertEquals(checkValue, result)
    }

    @Test
    fun shortestPathToLocationsTest2() {
        // define check value
        val checkValue = Pair(Pair(Pair(0, 0), 1), 0)

        // get start location and list of one neighbor
        val start = Pair(0, 0)
        val t1 = Pair(1, 0)
        val destinations = listOf(t1)

        // call shortestPathToLocations() - not enough travel amount to reach destination
        val result = nm.shortestPathToLocations(start, destinations, 0)

        assertEquals(checkValue, result)
    }

    @Test
    fun shortestPathToLocationsTest3() {
        // define check value
        val checkValue = Pair(Pair(Pair(1, 0), 2), 10)

        // get start location and list of one neighbor
        val start = Pair(0, 0)
        val t1 = Pair(1, 0)
        val destinations = listOf(t1)

        // call shortestPathToLocations() - more than enough travel amount to reach destination
        val result = nm.shortestPathToLocations(start, destinations, 10)

        assertEquals(checkValue, result)
    }

    @Test
    fun shortestPathToLocationsTest4() {
        // define check value
        val checkValue = Pair(Pair(Pair(1, 0), 2), 10)

        // get start location and list of three neighbors
        val start = Pair(0, 0)
        val t1 = Pair(1, 0)
        val t2 = Pair(0, 1)
        val t3 = Pair(1, 1)
        val destinations = listOf(t1, t2, t3)

        // call shortestPathToLocations() - just enough travel amount to reach destination
        val result = nm.shortestPathToLocations(start, destinations, 1)

        assertEquals(checkValue, result)
    }

    @Test
    fun shortestPathToLocationsTest5() {
        // define check value
        val checkValue = Pair(Pair(Pair(0, 1), 6), 10)

        // get start location and list of three neighbors
        val start = Pair(0, 0)
        val t1 = Pair(4, 0)
        val t2 = Pair(0, 4)
        val t3 = Pair(1, 3)
        val destinations = listOf(t1, t2, t3)

        // call shortestPathToLocations() - not enough travel amount to reach destination
        val result = nm.shortestPathToLocations(start, destinations, 1)

        assertEquals(checkValue, result)
    }

    @Test
    fun shortestPathToLocationsTest6() {
        // define check value
        val checkValue = Pair(Pair(Pair(0, 0), 1), 0)

        // get start location and list of two neighbors
        val start = Pair(0, 0)
        val t1 = Pair(3, 3)
        val t2 = Pair(3, 2)
        val destinations = listOf(t1, t2)

        // call shortestPathToLocations() - enough travel amount to reach destination, no path to destination
        val result = nm.shortestPathToLocations(start, destinations, 7)

        assertEquals(checkValue, result)
    }

    @Test
    fun shortestPathToLocationsTest7() {
        // define check value
        val checkValue = Pair(Pair(Pair(2, 1), 8), 20)

        // get start location and list two tiles
        val start = Pair(0, 0)
        val t1 = Pair(2, 0)
        val t2 = Pair(4, 0)
        val destinations = listOf(t1, t2)

        // set restriction on tile with id 2
        restrictTile(Pair(1, 0))

        // call shortestPathToLocations() - enough travel amount to move
        val result = nm.shortestPathToLocations(start, destinations, 2)

        assertEquals(checkValue, result)
    }

    @Test
    fun shortestPathToLocationsTest8() {
        // define check value
        val checkValue = Pair(Pair(Pair(1, 2), 12), 10)

        // get start location and list two tiles
        val start = Pair(2, 1)
        val t1 = Pair(2, 0) // neighboring restricted tile
        val t2 = Pair(1, 3)
        val destinations = listOf(t1, t2)

        // set restriction on tile with id 3
        restrictTile(Pair(2, 0))

        // call shortestPathToLocations() - enough travel amount to move, no path to destination
        val result = nm.shortestPathToLocations(start, destinations, 1)

        assertEquals(checkValue, result)
    }

    /**
     * Note: unsure if this test is needed, since we should
     * never call getShortestPathToLocations from a LAND tile ...
     */
    @Test
    fun shortestPathToLocationsTest9() {
        // define check value
        val checkValue = Pair(Pair(Pair(2, 2), 13), 0)

        // get start location and list two tiles
        val start = Pair(2, 2) // LAND tile
        val t1 = Pair(0, 0)
        val t2 = Pair(1, 0)
        val t3 = Pair(2, 0)
        val destinations = listOf(t1, t2, t3)

        // call shortestPathToLocations()
        val result = nm.shortestPathToLocations(start, destinations, 8)

        assertEquals(checkValue, result)
    }

    // --------------------------------------- dijkstra() tests ---------------------------------------

    @Test
    fun dijkstraTest1() {
        // call dijkstra method
        val (distancesResult, previousNodesResult) = callDijkstraForTileID(15)

        val checkDistances = mapOf(
            14 to 10,
            15 to 0,
            19 to 20,
            23 to 30
        )

        val checkPreviousNodes = mapOf(
            14 to 15,
            19 to 14,
            23 to 19
        )

        assertEquals(checkDistances, distancesResult)
        assertEquals(checkPreviousNodes, previousNodesResult)
    }

    @Test
    fun dijkstraTest2() {
        // call dijkstra method
        val (distancesResult, previousNodesResult) = callDijkstraForTileID(25)

        val checkDistances = mapOf(
            25 to 0
        )

        val checkPreviousNodes = emptyMap<Int, Int?>()

        assertEquals(checkDistances, distancesResult)
        assertEquals(checkPreviousNodes, previousNodesResult)
    }

    @Test
    fun dijkstraTest3() {
        // call dijkstra method
        val (distancesResult, previousNodesResult) = callDijkstraForTileID(20)

        val checkDistances = mapOf(
            20 to 0
        )

        val checkPreviousNodes = mapOf<Int, Int>()

        assertEquals(checkDistances, distancesResult)
        assertEquals(checkPreviousNodes, previousNodesResult)
    }
}
