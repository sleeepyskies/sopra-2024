package general.corporationManagerTests

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipState
import de.unisaarland.cs.se.selab.assets.ShipType
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.corporations.CorporationManager
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.junit.jupiter.api.Assertions.assertTrue
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
    private val current = Current(Direction.EAST, 0, 0)

    // current for t1
    private val currentT1 = Current(Direction.SOUTH_EAST, 5, 1)

    // current for t6
    private val currentT6 = Current(Direction.EAST, 5, 3)

    // row 0
    private val t1 = Tile(1, Pair(0, 0), TileType.DEEP_OCEAN, false, currentT1, true, 1000)
    private val t2 = Tile(2, Pair(1, 0), TileType.SHALLOW_OCEAN, false, current, false, 1000)
    private val t3 = Tile(3, Pair(2, 0), TileType.SHORE, false, current, false, 1000)
    private val t4 = Tile(4, Pair(3, 0), TileType.SHORE, false, current, false, 1000)
    private val t5 = Tile(5, Pair(4, 0), TileType.SHORE, true, current, false, 1000)

    // row 1
    private val t6 = Tile(6, Pair(0, 1), TileType.DEEP_OCEAN, false, currentT6, true, 1000)
    private val t7 = Tile(7, Pair(1, 1), TileType.SHALLOW_OCEAN, false, current, false, 1000)
    private val t8 = Tile(8, Pair(2, 1), TileType.SHORE, false, current, false, 1000)
    private val t9 = Tile(9, Pair(3, 1), TileType.LAND, false, current, false, 1000)
    private val t10 = Tile(10, Pair(4, 1), TileType.LAND, false, current, false, 1000)

    // row 2
    private val t11 = Tile(11, Pair(0, 2), TileType.SHALLOW_OCEAN, false, current, false, 1000)
    private val t12 = Tile(12, Pair(1, 2), TileType.SHORE, false, current, false, 1000)
    private val t13 = Tile(13, Pair(2, 2), TileType.LAND, false, current, false, 1000)
    private val t14 = Tile(14, Pair(3, 2), TileType.SHORE, false, current, false, 1000)
    private val t15 = Tile(15, Pair(4, 2), TileType.SHORE, false, current, false, 1000)

    // row 3
    private val t16 = Tile(16, Pair(0, 3), TileType.SHALLOW_OCEAN, false, current, false, 1000)
    private val t17 = Tile(17, Pair(1, 3), TileType.SHORE, false, current, false, 1000)
    private val t18 = Tile(18, Pair(2, 3), TileType.LAND, false, current, false, 1000)
    private val t19 = Tile(19, Pair(3, 3), TileType.SHORE, false, current, false, 1000)
    private val t20 = Tile(20, Pair(4, 3), TileType.LAND, false, current, false, 1000)

    // row 4
    private val t21 = Tile(21, Pair(0, 4), TileType.SHORE, true, current, false, 1000)
    private val t22 = Tile(22, Pair(1, 4), TileType.LAND, false, current, false, 1000)
    private val t23 = Tile(23, Pair(2, 4), TileType.SHORE, false, current, false, 1000)
    private val t24 = Tile(24, Pair(3, 4), TileType.LAND, false, current, false, 1000)
    private val t25 = Tile(25, Pair(4, 4), TileType.SHORE, false, current, false, 1000)

    private val map = mapOf(
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
    private lateinit var corp3: Corporation
    private lateinit var ship: Ship

    @BeforeEach
    fun setup() {
        this.nm = NavigationManager(map)
        // init the graph
        this.nm.initializeAndUpdateGraphStructure()
        ship = Ship(
            1, "Black_Pearl", 0, mutableMapOf(), 10, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        corp1 = Corporation(
            "Oscorp",
            0,
            mutableListOf(Pair(4, 0)),
            mutableListOf(ship),
            mutableListOf(GarbageType.PLASTIC, GarbageType.OIL, GarbageType.CHEMICALS)
        )
        corp2 = Corporation(
            "Stark_Industries",
            1,
            mutableListOf(Pair(0, 4)),
            mutableListOf(),
            mutableListOf(GarbageType.PLASTIC, GarbageType.CHEMICALS)
        )
        corp3 = Corporation(
            "Wayne_Enterprises",
            2,
            mutableListOf(Pair(4, 4)),
            mutableListOf(),
            mutableListOf(GarbageType.CHEMICALS, GarbageType.OIL)
        )
        Logger.setCorporationsInitialCollectedGarbage(listOf(corp1.id, corp2.id, corp3.id))
        sd = SimulationData(
            nm, mutableListOf(corp1, corp2, corp3), mutableListOf(ship), mutableListOf(), mutableListOf(),
            mutableMapOf(),
            mutableMapOf(),
            mutableListOf(), mutableListOf(), 0, 1
        )
        cm = CorporationManager(sd)
    }

    // New helper function to create ships
    private fun createShips(): List<Ship> {
        val shipNeedsToPee = Ship(
            2, "Horatio_Nelson", 1, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.REFUELING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedsToDefecateWithContainer1 = Ship(
            4, "Pierre-Charles_Villeneuve", 1, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedsToDefecateWithContainer2 = Ship(
            7, "Pierre-Charles_Villeneuve", 1, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedsToDefecateWithContainer3 = Ship(
            8, "Pierre-Charles_Villeneuve", 1, mutableMapOf(), 10, Pair(0, 4),
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
            5, "Federico_Gravina", 1, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.REFUELING_AND_UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedRefuel = Ship(
            9, "Federico_Gravina", 1, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_REFUELING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedUnload = Ship(
            10, "Federico_Gravina", 1, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipNeedRefuelUnload = Ship(
            11, "Federico_Gravina", 1, mutableMapOf(), 10, Pair(0, 4),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_REFUELING_AND_UNLOADING, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        corp2.ships.add(ships[0])
        corp2.ships.add(ships[1])
        corp2.ships.add(ships[2])
        corp2.ships.add(ships[3])
        corp2.ships.add(shipKaputt)
        corp2.ships.add(shipNeedRefuel)
        corp2.ships.add(shipNeedUnload)
        corp2.ships.add(shipNeedRefuelUnload)
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
        val shipKaputt = ships[4]
        val shipNeedRefuel = ships[5]
        val shipNeedUnload = ships[6]
        val shipNeedRefuelUnload = ships[7]

        ships[1].capacityInfo[GarbageType.PLASTIC] = Pair(0, 1000)
        ships[2].capacityInfo[GarbageType.OIL] = Pair(0, 1000)
        ships[3].capacityInfo[GarbageType.CHEMICALS] = Pair(0, 1000)
        shipKaputt.capacityInfo.keys.forEach { key ->
            shipKaputt.capacityInfo[key] = Pair(0, shipKaputt.capacityInfo[key]?.second ?: 0)
        }
        ships[1].capacityInfo.keys.forEach { key ->
            ships[1].capacityInfo[key] = Pair(
                0, ships[1].capacityInfo[key]?.second ?: 0
            )
        }

        val testFun = CorporationManager::class.java.getDeclaredMethod(
            "startRefuelUnloadPhase",
            Corporation::class.java
        )
        testFun.isAccessible = true
        testFun.invoke(cm, corp2)
        assertEquals(ShipState.DEFAULT, shipNeedsToPee.state)
        assertEquals(shipNeedsToPee.currentFuel, shipNeedsToPee.maxFuelCapacity)
        assertEquals(ships[1].state, ShipState.DEFAULT)
        assertEquals(
            ships[1].capacityInfo[GarbageType.PLASTIC]?.first ?: 0,
            1000
        )
        assertEquals(
            ships[1].capacityInfo[GarbageType.PLASTIC]?.second ?: 0,
            1000
        )
        assertEquals(ShipState.UNLOADING, shipKaputt.state)
        assertEquals(shipKaputt.currentFuel, 1000)
        assertEquals(ShipState.DEFAULT, ships[2].state)
        assertEquals(
            ships[2].capacityInfo[GarbageType.OIL]?.first ?: 0,
            1000
        )
        assertEquals(
            ships[2].capacityInfo[GarbageType.OIL]?.second ?: 0,
            1000
        )
        assertEquals(ShipState.DEFAULT, ships[3].state)
        assertEquals(
            ships[3].capacityInfo[GarbageType.CHEMICALS]?.first ?: 0,
            1000
        )
        assertEquals(
            ships[3].capacityInfo[GarbageType.CHEMICALS]?.second ?: 0,
            1000
        )
        assertEquals(ShipState.REFUELING, shipNeedRefuel.state)
        assertEquals(ShipState.UNLOADING, shipNeedUnload.state)
        assertEquals(ShipState.REFUELING_AND_UNLOADING, shipNeedRefuelUnload.state)
    }

    @Test
    fun startCooperatingPhaseTest() {
        val shipThatCooperates = Ship(
            2, "Federico_Gravina", 1, mutableMapOf(), 10, Pair(0, 2),
            Direction.EAST, 15, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_REFUELING_AND_UNLOADING, ShipType.COORDINATING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val shipWithRadio = Ship(
            3, "Federico_Gravina", 2, mutableMapOf(), 10, Pair(0, 1),
            Direction.EAST, 5, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_REFUELING_AND_UNLOADING, ShipType.COLLECTING_SHIP,
            hasRadio = true, hasTracker = false, travelingToHarbor = false
        )
        val victimShip1 = Ship(
            4, "Federico_Gravina", 0, mutableMapOf(), 10, Pair(0, 2),
            Direction.EAST, 15, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val victimShip2 = Ship(
            4, "Federico_Gravina", 0, mutableMapOf(), 10, Pair(0, 1),
            Direction.EAST, 5, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        for (i in 0 until 5) {
            corp1.garbage[i] = Pair(Pair(0, 2), GarbageType.PLASTIC)
        }
        for (i in 0 until 5) {
            corp1.visibleGarbage[i] = Pair(Pair(0, 2), GarbageType.PLASTIC)
        }
        corp2.ships.addAll(mutableListOf(shipThatCooperates))
        corp3.ships.addAll(mutableListOf(shipWithRadio))
        corp1.ships.addAll(mutableListOf(victimShip2, victimShip1))
        val testFun = CorporationManager::class.java.getDeclaredMethod(
            "startCooperationPhase",
            Corporation::class.java
        )
        testFun.isAccessible = true
        testFun.invoke(cm, corp2)
        testFun.invoke(cm, corp3)
        assertTrue(corp2.garbage.isNotEmpty())
        assertTrue(corp3.garbage.isNotEmpty())
        assertEquals(corp3.garbage, corp2.garbage)
        assertEquals(corp2.lastCooperatedWith, corp3.lastCooperatedWith)
    }

    private fun setupShipsAndGarbage(): List<Ship> {
        val collecting1corp1 = Ship(
            2, "Federico_Gravina", 0, mutableMapOf(), 10, Pair(1, 1),
            Direction.EAST, 15, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ableToCollectShip = Ship(
            3, "Federico_Gravina", 0, mutableMapOf(), 10, Pair(0, 2),
            Direction.EAST, 5, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.COORDINATING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val cantCollectShip = Ship(
            4, "Federico_Gravina", 0, mutableMapOf(), 10, Pair(0, 2),
            Direction.EAST, 5, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val hasToWaitShip = Ship(
            5, "Federico_Gravina", 0, mutableMapOf(), 10, Pair(0, 3),
            Direction.EAST, 5, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val collectTogetherShip1 = Ship(
            6, "Federico_Gravina", 0, mutableMapOf(), 10, Pair(0, 1),
            Direction.EAST, 5, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val collectTogetherShip2 = Ship(
            7, "Federico_Gravina", 0, mutableMapOf(), 10, Pair(0, 1),
            Direction.EAST, 5, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        collecting1corp1.capacityInfo[GarbageType.OIL] = Pair(1000, 1000)
        ableToCollectShip.capacityInfo[GarbageType.CHEMICALS] = Pair(1000, 1000)
        hasToWaitShip.capacityInfo[GarbageType.PLASTIC] = Pair(500, 1000)
        collectTogetherShip2.capacityInfo[GarbageType.PLASTIC] = Pair(500, 1000)
        collectTogetherShip1.capacityInfo[GarbageType.PLASTIC] = Pair(500, 1000)

        return listOf(
            collecting1corp1,
            ableToCollectShip,
            cantCollectShip,
            hasToWaitShip,
            collectTogetherShip1,
            collectTogetherShip2
        )
    }

    @Test
    fun startCollectGarbagePhaseTest() {
        t7.currentGarbage.add(Garbage(0, 1000, GarbageType.OIL, 7, Pair(1, 1)))
        t11.currentGarbage.add(Garbage(1, 1000, GarbageType.CHEMICALS, 11, Pair(0, 2)))
        t16.currentGarbage.add(Garbage(2, 1000, GarbageType.PLASTIC, 16, Pair(0, 3)))
        t6.currentGarbage.add(Garbage(3, 1000, GarbageType.PLASTIC, 6, Pair(0, 1)))
        val ships = setupShipsAndGarbage()
        corp1.ships.addAll(ships)
        val testFun = CorporationManager::class.java.getDeclaredMethod(
            "startCollectGarbagePhase",
            Corporation::class.java
        )
        testFun.isAccessible = true
        testFun.invoke(cm, corp1)
        assertEquals(0, t7.currentGarbage.size)
        assertEquals(0, t11.currentGarbage.size)
        assertEquals(1000, t16.currentGarbage[0].amount)
        // assertEquals(0, t6.currentGarbage.size)
        assertEquals(0, ships[0].capacityInfo[GarbageType.OIL]?.first ?: 1000)
        assertEquals(ships[1].capacityInfo[GarbageType.CHEMICALS]?.first ?: 1000, 0)
        assertTrue(ships[2].capacityInfo.isEmpty())
        assertEquals(ships[3].capacityInfo[GarbageType.PLASTIC]?.first ?: 1000, 500)
        assertEquals(ships[4].capacityInfo[GarbageType.PLASTIC]?.first ?: 1000, 0)
        assertEquals(ships[5].capacityInfo[GarbageType.PLASTIC]?.first ?: 1000, 0)
    }
}
