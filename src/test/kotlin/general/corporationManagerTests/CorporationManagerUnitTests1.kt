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
import de.unisaarland.cs.se.selab.assets.Task
import de.unisaarland.cs.se.selab.assets.TaskType
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.corporations.CorporationManager
import de.unisaarland.cs.se.selab.corporations.CorporationManagerHelper
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.PrintWriter
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CorporationManagerUnitTests1 {
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
    private val t15 = Tile(15, Pair(4, 2), TileType.SHORE, true, current, false, 1000)

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
    private lateinit var cm: CorporationManager
    private lateinit var cmh: CorporationManagerHelper
    private lateinit var simDat: SimulationData

    @BeforeEach
    fun setup() {
        this.nm = NavigationManager(map)
        // init the graph
        this.nm.initializeAndUpdateGraphStructure()
        val corp1 = Corporation("Test_corp", 1, listOf(Pair(4, 0)), mutableListOf(), listOf(GarbageType.OIL))
        val corp2 = Corporation(
            "Hammer Industries",
            2,
            listOf(Pair(4, 2), Pair(0, 4)),
            mutableListOf(),
            listOf(GarbageType.PLASTIC)
        )
        val task1 = Task(1, TaskType.COLLECT, 1, 1, 8, 1, 2)
        this.simDat = SimulationData(
            nm, mutableListOf(corp1, corp2), mutableListOf(), mutableListOf(), mutableListOf(), mutableMapOf(),
            mutableMapOf(),
            mutableListOf(task1), mutableListOf(), 0, 1
        )
        this.cm = CorporationManager(simDat)
        this.cmh = CorporationManagerHelper(simDat)
    }

    @AfterEach
    fun cleanup() {
        for (tile in this.nm.tiles) {
            tile.value.isRestricted = false
            tile.value.currentGarbage.clear()
        }
        simDat.corporations = mutableListOf()
        simDat.ships.clear()
        simDat.garbage.clear()
        simDat.activeTasks.clear()
    }

    @Test
    fun testScan() {
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        simDat.ships.add(ship)
        simDat.corporations[0].ships.add(ship)
        val ship2 = Ship(
            2, "black_pearl", 2, mutableMapOf(), 2, Pair(0, 1),
            Direction.EAST, 6, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        simDat.ships.add(ship2)
        simDat.corporations[1].ships.add(ship2)

        val garbage1 = Garbage(1, 50, GarbageType.OIL, 2, Pair(1, 0))
        t2.addGarbageToTile(garbage1)
        simDat.garbage.add(garbage1)
        this.cm = CorporationManager(simDat)
        val method = CorporationManager::class.java.getDeclaredMethod(
            "scan",
            Pair::class.java,
            Int::class.java,
            Int::class.java
        )
        method.isAccessible = true
        val scanResult = method.invoke(cm, Pair(0, 0), ship.visibilityRange, ship.id) as? Pair<*, *>
        if (scanResult != null) {
            val mapResult = scanResult.first as? Map<*, *>
            if (mapResult != null) {
                assertEquals(Pair(2, Pair(0, 1)), mapResult[2])
            }
            val garbageResult = scanResult.second as? Map<*, *>
            if (garbageResult != null) {
                assertEquals(garbageResult[1], Pair(Pair(1, 0), GarbageType.OIL))
            }
        }
    }

    @Test
    fun test_shipMoveToLocation() {
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        simDat.ships.add(ship)
        simDat.corporations[0].ships.add(ship)
        val method = CorporationManager::class.java.getDeclaredMethod(
            "shipMoveToLocation",
            Ship::class.java,
            Pair::class.java
        )
        method.isAccessible = true
        method.invoke(cm, ship, Pair(Pair(1, 0), 2))
        assertEquals(Pair(1, 0), ship.location)
    }

    @Test
    fun test_determineBehaviourOutOfRestriction() {
        val shipState = ShipState.DEFAULT
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        map[Pair(0, 0)]?.isRestricted = true
        simDat.ships.add(ship)
        simDat.corporations[0].ships.add(ship)
        val method = CorporationManager::class.java.getDeclaredMethod(
            "determineBehavior",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        val locationToGoTo = method.invoke(cm, ship, simDat.corporations[0])
        assertEquals(shipState, ship.state)
        assertEquals(Pair(listOf(Pair(1, 0) to 10), false), locationToGoTo)
    }

    @Test
    fun test_checkNeedRefueling() {
        val shipState = ShipState.NEED_REFUELING
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        simDat.corporations[0].ships.addAll(listOf(ship))
        val method = CorporationManager::class.java.getDeclaredMethod(
            "checkNeedRefuelOrUnload",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        method.invoke(cm, ship, simDat.corporations[0])
        assertEquals(shipState, ship.state)
    }

    @Test
    fun test_checkNeedUnloading() {
        val shipState2 = ShipState.NEED_UNLOADING
        val shipState3 = ShipState.NEED_REFUELING_AND_UNLOADING
        val ship2 = Ship(
            2, "white_pearl", 1, mutableMapOf(), 2, Pair(0, 0),
            Direction.EAST, 6, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ship3 = Ship(
            3, "gray_pearl", 2, mutableMapOf(), 3, Pair(0, 0),
            Direction.EAST, 6, 10, 10, 10, 1000,
            10, 0, -1, ShipState.NEED_REFUELING, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        ship2.capacityInfo[GarbageType.OIL] = Pair(0, 1000)
        ship3.capacityInfo[GarbageType.PLASTIC] = Pair(0, 1000)
        simDat.ships.addAll(listOf(ship2, ship3))
        simDat.corporations[0].ships.addAll(listOf(ship2))
        simDat.corporations[1].ships.addAll(listOf(ship3))
        val method = CorporationManagerHelper::class.java.getDeclaredMethod(
            "checkNeedUnloading",
            Ship::class.java
        )
        method.isAccessible = true
        method.invoke(cmh, ship2)
        method.invoke(cmh, ship3)
        assertEquals(shipState2, ship2.state)
        assertEquals(shipState3, ship3.state)
    }

    @Test
    fun test_determineBehaviourNeedsToGoBackToHarborToRefuel() {
        val shipState1 = ShipState.DEFAULT
        val shipState2 = ShipState.NEED_REFUELING
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ship2 = Ship(
            2, "black_pearl_which_needs_refuel", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 400, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        simDat.ships.addAll(listOf(ship, ship2))
        simDat.corporations[0].ships.add(ship)
        simDat.corporations[0].ships.add(ship2)
        val method = CorporationManager::class.java.getDeclaredMethod(
            "determineBehavior",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        val locationToGoTo1 = method.invoke(cm, ship, simDat.corporations[0])
        val locationToGoTo2 = method.invoke(cm, ship2, simDat.corporations[0])
        assertEquals(shipState1, ship.state)
        assertEquals(shipState2, ship2.state)
        assertEquals(Pair(listOf(Pair(0, 0) to 0), false), locationToGoTo1)
        assertEquals(Pair(listOf(Pair(4, 0) to 5), false), locationToGoTo2)
    }

    @Test
    fun test_determineBehaviourNeedsToGoBackToHarborToUnload() {
        val shipState1 = ShipState.DEFAULT
        val shipState2 = ShipState.DEFAULT
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ship2 = Ship(
            2, "black_pearl_which_needs_unload", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        ship2.capacityInfo[GarbageType.OIL] = Pair(0, 1000)
        simDat.ships.addAll(listOf(ship, ship2))
        simDat.corporations[0].ships.add(ship)
        simDat.corporations[0].ships.add(ship2)
        val method = CorporationManager::class.java.getDeclaredMethod(
            "determineBehavior",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        val locationToGoTo1 = method.invoke(cm, ship, simDat.corporations[0])
        val locationToGoTo2 = method.invoke(cm, ship2, simDat.corporations[0])
        assertEquals(shipState1, ship.state)
        assertEquals(shipState2, ship2.state)
        assertEquals(Pair(listOf(Pair(0, 0) to 0), false), locationToGoTo1)
        assertEquals(Pair(listOf(Pair(1, 0) to 0), true), locationToGoTo2)
    }
}
