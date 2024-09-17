package general.corporationManagerTests

import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.junit.jupiter.api.BeforeEach

class corporationManagerUnitTests2 {

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

    private lateinit var nm: NavigationManager
    private lateinit var sd: SimulationData

    @BeforeEach
    fun setup() {
        this.nm = NavigationManager(map)
        // init the graph
        this.nm.initializeAndUpdateGraphStructure()
        sd = SimulationData(
            nm, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableMapOf(),
            mutableMapOf(),
            mutableListOf(), mutableListOf(), 0, 1
        )
    }
}
