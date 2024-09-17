package navigationmanager

import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NavigationManagerTest2 {
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
     * Removes all garbage from the tiles in the map.
     */
    @AfterEach
    fun resetGarbage() {
        for (tile in this.nm.tiles.values) {
            tile.currentGarbage.clear()
        }
    }

    /**
     * Calls the findTileInDirectionFrom method of the NavigationManager class for the given location and direction.
     */
    private fun callFindTileInDirectionFromForLocation(location: Pair<Int, Int>, direction: Direction): Pair<Int, Int> {
        // access findTileInDirectionFrom method
        val findTileInDirectionFrom = nm::class.java.getDeclaredMethod(
            "findTileInDirectionFrom",
            Pair::class.java,
            Direction::class.java
        )
        findTileInDirectionFrom.isAccessible = true

        // call findTileInDirectionFrom method
        return findTileInDirectionFrom.invoke(
            nm,
            location,
            direction
        ) as Pair<Int, Int>
    }

    // ------------------------------------ calculateDrift() tests ------------------------------------
    @Test
    fun calculateDriftTest1() {
        // get tiles
        val t1 = this.nm.tiles[Pair(3, 1)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(2, 1)] ?: this.mockTile
        val t3 = this.nm.tiles[Pair(1, 1)] ?: this.mockTile

        // define checkValue
        val checkValue = listOf(t3, t2, t1)

        // call calculateDrift()
        val result = this.nm.calculateDrift(Pair(0, 1), Direction.EAST, 3)

        assertEquals(checkValue, result)
    }

    @Test
    fun calculateDriftTest2() {
        // get tiles
        val t1 = this.nm.tiles[Pair(1, 1)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(0, 0)] ?: this.mockTile

        // define checkValue
        val checkValue = listOf(t2, t1)

        // call calculateDrift()
        val result = this.nm.calculateDrift(Pair(0, 0), Direction.SOUTH_EAST, 1)

        assertEquals(checkValue, result)
    }

    @Test
    fun calculateDriftTest3() {
        // define checkValue -- drift but no speed
        val checkValue = listOf<Tile>()

        // call calculateDrift()
        val result = this.nm.calculateDrift(Pair(0, 0), Direction.SOUTH_EAST, 0)

        assertEquals(checkValue, result)
    }

    @Test
    fun calculateDriftTest4() {
        // define checkValue -- drift but no speed
        val checkValue = listOf<Tile>()

        // call calculateDrift()
        val result = this.nm.calculateDrift(Pair(4, 4), Direction.NORTH_WEST, 10)

        assertEquals(checkValue, result)
    }

    // ------------------------------------ findTileInDirectionFrom() tests ------------------------------------

    @Test
    fun findTileInDirectionFromTest() {
        // get results
        val result1 = callFindTileInDirectionFromForLocation(Pair(1, 2), Direction.NORTH_WEST)
        val result2 = callFindTileInDirectionFromForLocation(Pair(3, 2), Direction.EAST)
        val result3 = callFindTileInDirectionFromForLocation(Pair(0, 3), Direction.WEST)

        // assertions
        assertEquals(Pair(1, 1), result1)
        assertEquals(Pair(4, 2), result2)
        assertEquals(Pair(0, 3), result3)
    }

    // ------------------------------------ getTilesInRadius() tests ------------------------------------

    @Test
    fun getTilesInRadiusTest1() {
        // define tiles that should be in the radius
        val t1 = this.nm.tiles[Pair(4, 4)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(3, 4)] ?: this.mockTile
        val t3 = this.nm.tiles[Pair(4, 3)] ?: this.mockTile

        val checkValue = listOf(t1.location, t2.location, t3.location)

        val result = this.nm.getTilesInRadius(Pair(4, 4), 1)

        assertEquals(checkValue, result)
    }

    @Test
    fun getTilesInRadiusTest2() {
        // define tiles that should be in the radius
        val t1 = this.nm.tiles[Pair(4, 4)] ?: this.mockTile

        val checkValue = listOf(t1.location)

        val result = this.nm.getTilesInRadius(Pair(4, 4), 0)

        assertEquals(checkValue, result)
    }

    // ------------------------------------ getRingOfRadius() tests ------------------------------------

    @Test
    fun getRingOfRadiusTest1() {
        // define tiles that should be in the ring
        val t1 = this.nm.tiles[Pair(4, 3)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(3, 4)] ?: this.mockTile

        val checkValue = listOf(t1.location, t2.location).sortedWith(compareBy({ it.first }, { it.second }))

        val result = this.nm.getRingOfRadius(Pair(4, 4), 1).sortedWith(compareBy({ it.first }, { it.second }))

        assertEquals(checkValue, result)
    }

    @Test
    fun getRingOfRadiusTest2() {
        // define tiles that should be in the ring
        val t1 = this.nm.tiles[Pair(0, 2)] ?: this.mockTile
        val t2 = this.nm.tiles[Pair(1, 1)] ?: this.mockTile
        val t3 = this.nm.tiles[Pair(1, 0)] ?: this.mockTile
        val t4 = this.nm.tiles[Pair(2, 0)] ?: this.mockTile
        val t5 = this.nm.tiles[Pair(3, 0)] ?: this.mockTile
        val t6 = this.nm.tiles[Pair(4, 1)] ?: this.mockTile
        val t7 = this.nm.tiles[Pair(4, 2)] ?: this.mockTile
        val t8 = this.nm.tiles[Pair(4, 3)] ?: this.mockTile
        val t9 = this.nm.tiles[Pair(3, 4)] ?: this.mockTile
        val t10 = this.nm.tiles[Pair(2, 4)] ?: this.mockTile
        val t11 = this.nm.tiles[Pair(1, 4)] ?: this.mockTile
        val t12 = this.nm.tiles[Pair(1, 3)] ?: this.mockTile

        val checkValue = listOf(
            t1.location,
            t2.location,
            t3.location,
            t4.location,
            t5.location,
            t6.location,
            t7.location,
            t8.location,
            t9.location,
            t10.location,
            t11.location,
            t12.location
        ).sortedWith(compareBy({ it.first }, { it.second }))

        val result = this.nm.getRingOfRadius(Pair(2, 2), 2).sortedWith(compareBy({ it.first }, { it.second }))

        assertEquals(checkValue, result)
    }

    // ----------------------- getGarbageFromAllTilesInCorrectOrderForDrifting() tests -----------------------

    @Test
    fun getGarbageFromAllTilesInCorrectOrderForDrifting1() {
        // make a mock garbageType
        val gt = GarbageType.PLASTIC

        // create some garbage :O
        val g1 = Garbage(0, 10, gt, 1, Pair(0, 0))
        val g2 = Garbage(1, 10, gt, 1, Pair(0, 0))
        val g3 = Garbage(2, 10, gt, 1, Pair(0, 0))

        val g4 = Garbage(3, 10, gt, 8, Pair(2, 1))

        val g5 = Garbage(4, 10, gt, 19, Pair(3, 3))
        val g6 = Garbage(5, 10, gt, 19, Pair(3, 3))

        // place some garbage on tiles
        this.nm.tiles[Pair(0, 0)]?.addGarbageToTile(g1)
        this.nm.tiles[Pair(0, 0)]?.addGarbageToTile(g2)
        this.nm.tiles[Pair(0, 0)]?.addGarbageToTile(g3)

        this.nm.tiles[Pair(2, 1)]?.addGarbageToTile(g4)

        this.nm.tiles[Pair(3, 3)]?.addGarbageToTile(g5)
        this.nm.tiles[Pair(3, 3)]?.addGarbageToTile(g6)

        // define checkValue
        val checkValue = listOf(
            Pair(1, listOf(g1, g2, g3)),
            Pair(8, listOf(g4)),
            Pair(19, listOf(g5, g6))
        )

        // call getGarbageFromAllTilesInCorrectOrderForDrifting()
        val result = this.nm.getGarbageFromAllTilesInCorrectOrderForDrifting()

        assertEquals(checkValue, result)
    }

    @Test
    fun getGarbageFromAllTilesInCorrectOrderForDrifting2() {
        // define checkValue
        val checkValue = emptyList<Pair<Int, List<Garbage>>>()

        // call getGarbageFromAllTilesInCorrectOrderForDrifting()
        val result = this.nm.getGarbageFromAllTilesInCorrectOrderForDrifting()

        assertEquals(checkValue, result)
    }
}
