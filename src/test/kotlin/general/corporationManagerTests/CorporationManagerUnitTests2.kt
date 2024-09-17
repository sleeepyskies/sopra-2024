package general.corporationManagerTests

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipState
import de.unisaarland.cs.se.selab.assets.ShipType
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.corporations.CorporationManager
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.PrintWriter
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CorporationManagerUnitTests2 {

    companion object {
        @JvmStatic
        @BeforeAll
        fun init() {
            Logger.setOutput(PrintWriter(System.out, true))
        }
    }

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
    val t15 = Tile(15, Pair(4, 2), TileType.SHORE, false, current, false, 1000)

    // row 3
    val t16 = Tile(16, Pair(0, 3), TileType.SHALLOW_OCEAN, false, current, false, 1000)
    val t17 = Tile(17, Pair(1, 3), TileType.SHORE, false, current, false, 1000)
    val t18 = Tile(18, Pair(2, 3), TileType.LAND, false, current, false, 1000)
    val t19 = Tile(19, Pair(3, 3), TileType.SHORE, false, current, false, 1000)
    val t20 = Tile(20, Pair(4, 3), TileType.LAND, false, current, false, 1000)

    // row 4
    val t21 = Tile(21, Pair(0, 4), TileType.SHORE, true, current, false, 1000)
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
    private lateinit var cm: CorporationManager
    private lateinit var corp1: Corporation
    private lateinit var corp2: Corporation
    private lateinit var ship: Ship

    @BeforeEach
    fun setup() {
        this.nm = NavigationManager(map)
        // init the graph
        this.nm.initializeAndUpdateGraphStructure()
        ship = Ship(
            1, "Black_Pearl", 1, mutableMapOf(), 10, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        corp1 = Corporation(
            "Oscorp",
            1,
            mutableListOf(Pair(4, 0)),
            mutableListOf(ship),
            mutableListOf(GarbageType.PLASTIC, GarbageType.OIL)
        )
        corp2 = Corporation(
            "Stark_Industries",
            2,
            mutableListOf(Pair(0, 4)),
            mutableListOf(),
            mutableListOf(GarbageType.PLASTIC, GarbageType.CHEMICALS)
        )
        sd = SimulationData(
            nm, mutableListOf(corp1, corp2), mutableListOf(ship), mutableListOf(), mutableListOf(), mutableMapOf(),
            mutableMapOf(),
            mutableListOf(), mutableListOf(), 0, 1
        )
        cm = CorporationManager(sd)
    }

    // New helper function to create ships
    private fun createShips(): List<Ship> {
        val shipNeedsToPee = Ship(
            2, "Horatio_Nelson", 2, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.REFUELING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedsToDefecateWithContainer1 = Ship(
            4, "Pierre-Charles_Villeneuve", 2, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedsToDefecateWithContainer2 = Ship(
            7, "Pierre-Charles_Villeneuve", 2, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedsToDefecateWithContainer3 = Ship(
            8, "Pierre-Charles_Villeneuve", 2, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        return listOf(
            shipNeedsToPee,
            shipNeedsToDefecateWithContainer1,
            shipNeedsToDefecateWithContainer2,
            shipNeedsToDefecateWithContainer3
        )
    }

    // Modify the existing helper function to use the new helper
    private fun refuelUnloadPhaseTestHelper(): List<Ship> {
        val ships = createShips()
        val shipKaputt = Ship(
            5, "Federico_Gravina", 2, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.REFUELING_AND_UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedRefuel = Ship(
            9, "Federico_Gravina", 2, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_REFUELING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedUnload = Ship(
            10, "Federico_Gravina", 2, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedRefuelUnload = Ship(
            11, "Federico_Gravina", 2, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_REFUELING_AND_UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        return ships + listOf(
            shipKaputt,
            shipNeedRefuel,
            shipNeedUnload,
            shipNeedRefuelUnload
        )
    }

    // Use the refuelUnloadPhaseTestHelper function in the test function
    @Test
    fun refuelUnloadPhaseTest() {
        val ships = refuelUnloadPhaseTestHelper()
        val shipNeedsToPee = ships[0]
        val shipNeedsToDefecateWithContainer1 = ships[1]
        val shipNeedsToDefecateWithContainer2 = ships[2]
        val shipNeedsToDefecateWithContainer3 = ships[3]
        val shipKaputt = ships[4]
        val shipNeedRefuel = ships[5]
        val shipNeedUnload = ships[6]
        val shipNeedRefuelUnload = ships[7]

        shipNeedsToDefecateWithContainer1.capacityInfo[GarbageType.PLASTIC] = Pair(0, 1000)
        shipNeedsToDefecateWithContainer2.capacityInfo[GarbageType.OIL] = Pair(0, 1000)
        shipNeedsToDefecateWithContainer3.capacityInfo[GarbageType.CHEMICALS] = Pair(0, 1000)
        shipKaputt.capacityInfo.keys.forEach { key ->
            shipKaputt.capacityInfo[key] = Pair(0, shipKaputt.capacityInfo[key]?.second ?: 0)
        }
        shipNeedsToDefecateWithContainer1.capacityInfo.keys.forEach { key ->
            shipNeedsToDefecateWithContainer1.capacityInfo[key] = Pair(
                0, shipNeedsToDefecateWithContainer1.capacityInfo[key]?.second ?: 0
            )
        }
        corp2.ships.add(shipNeedsToPee)
        corp2.ships.add(shipKaputt)
        corp2.ships.add(shipNeedsToDefecateWithContainer1)
        corp2.ships.add(shipNeedsToDefecateWithContainer2)
        corp2.ships.add(shipNeedsToDefecateWithContainer3)
        corp2.ships.add(shipNeedRefuel)
        corp2.ships.add(shipNeedUnload)
        corp2.ships.add(shipNeedRefuelUnload)

        val testFun = CorporationManager::class.java.getDeclaredMethod(
            "startRefuelUnloadPhase",
            Corporation::class.java
        )
        testFun.isAccessible = true
        testFun.invoke(cm, corp2)
        assertEquals(ShipState.DEFAULT, shipNeedsToPee.state)
        assertEquals(shipNeedsToPee.currentFuel, shipNeedsToPee.maxFuelCapacity)
        assertEquals(shipNeedsToDefecateWithContainer1.state, ShipState.DEFAULT)
        assertEquals(
            shipNeedsToDefecateWithContainer1.capacityInfo[GarbageType.PLASTIC]?.first ?: 0,
            1000
        )
        assertEquals(
            shipNeedsToDefecateWithContainer1.capacityInfo[GarbageType.PLASTIC]?.second ?: 0,
            1000
        )
        assertEquals(ShipState.UNLOADING, shipKaputt.state)
        assertEquals(shipKaputt.currentFuel, 1000)
        assertEquals(ShipState.DEFAULT, shipNeedsToDefecateWithContainer2.state)
        assertEquals(
            shipNeedsToDefecateWithContainer2.capacityInfo[GarbageType.OIL]?.first ?: 0,
            1000
        )
        assertEquals(
            shipNeedsToDefecateWithContainer2.capacityInfo[GarbageType.OIL]?.second ?: 0,
            1000
        )
        assertEquals(ShipState.DEFAULT, shipNeedsToDefecateWithContainer3.state)
        assertEquals(
            shipNeedsToDefecateWithContainer3.capacityInfo[GarbageType.CHEMICALS]?.first ?: 0,
            1000
        )
        assertEquals(
            shipNeedsToDefecateWithContainer3.capacityInfo[GarbageType.CHEMICALS]?.second ?: 0,
            1000
        )
        assertEquals(ShipState.REFUELING, shipNeedRefuel.state)
        assertEquals(ShipState.UNLOADING, shipNeedUnload.state)
        assertEquals(ShipState.REFUELING_AND_UNLOADING, shipNeedRefuelUnload.state)
    }

    @Test
    fun startCooperatingPhaseTest() {
        val shipThatCooperates = Ship(
            2, "Federico_Gravina", 2, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_REFUELING_AND_UNLOADING, ShipType.COORDINATING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipWithRadio = Ship(
            3, "Federico_Gravina", 2, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_REFUELING_AND_UNLOADING, ShipType.COLLECTING_SHIP,
            hasRadio = true, hasTracker = false, travelingToHarbor = false
        )
        val victimShip1 = Ship(
            4, "Federico_Gravina", 1, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val victimShip2 = Ship(
            4, "Federico_Gravina", 1, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        corp2.ships.addAll(mutableListOf( shipThatCooperates, shipWithRadio))
        corp1.ships.addAll(mutableListOf( victimShip2, victimShip1))

    }
}
