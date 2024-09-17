package general.corporationManagerTests

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.*
import de.unisaarland.cs.se.selab.corporations.CorporationManager
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.PrintWriter
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    private lateinit var cm: CorporationManager
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
        val method = CorporationManager::class.java.getDeclaredMethod("scan", Pair::class.java, Int::class.java)
        method.isAccessible = true
        val scanResult = method.invoke(cm, Pair(0, 0), ship.visibilityRange) as? Pair<*, *>
        if (scanResult != null) {
            val mapResult = scanResult.first as? Map<*, *>
            if (mapResult != null) {
                assertEquals(Pair(1, Pair(0, 0)), mapResult[1])
                assertEquals(Pair(2, Pair(0,1)), mapResult[2])
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
        assertEquals(listOf(Pair(1, 0)), locationToGoTo)
    }

    @Test
    fun test_checkNeedRefuelOrUnload() {
        val shipState1 = ShipState.NEED_REFUELING
        val shipState2 = ShipState.NEED_UNLOADING
        val shipState3 = ShipState.NEED_REFUELING_AND_UNLOADING
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ship2 = Ship(
            2, "white_pearl", 1, mutableMapOf(), 2, Pair(0, 0),
            Direction.EAST, 6, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ship3 = Ship(
            3, "gray_pearl", 2, mutableMapOf(), 3, Pair(0, 0),
            Direction.EAST, 6, 10, 10, 10, 1000,
            10, 0, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        ship2.capacityInfo[GarbageType.OIL] = Pair(0, 1000)
        ship3.capacityInfo[GarbageType.PLASTIC] = Pair(0, 1000)
        simDat.ships.addAll(listOf(ship, ship2, ship3))
        simDat.corporations[0].ships.addAll(listOf(ship, ship2))
        simDat.corporations[1].ships.addAll(listOf(ship3))
        val method = CorporationManager::class.java.getDeclaredMethod(
            "checkNeedRefuelOrUnload",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        method.invoke(cm, ship, simDat.corporations[0])
        method.invoke(cm, ship2, simDat.corporations[1])
        method.invoke(cm, ship3, simDat.corporations[1])
        assertEquals(shipState1, ship.state)
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
            10, 300, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
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
        assertEquals(listOf(Pair(0, 0)), locationToGoTo1)
        assertEquals(listOf(Pair(4, 0)), locationToGoTo2)
    }

    @Test
    fun test_determineBehaviourNeedsToGoBackToHarborToUnload() {
        val shipState1 = ShipState.DEFAULT
        val shipState2 = ShipState.NEED_UNLOADING
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
        assertEquals(listOf(Pair(0, 0)), locationToGoTo1)
        assertEquals(listOf(Pair(4, 0)), locationToGoTo2)
    }

    @Test
    fun test_shipAtHarborAndNeedsToRefuelOrUnload() {
        val shipState1 = ShipState.REFUELING
        val shipState2 = ShipState.UNLOADING
        val shipState3 = ShipState.REFUELING_AND_UNLOADING
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(4, 0),
            Direction.EAST, 5, 10, 10, 10, 1000,
            10, 0, -1, shipState1, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = true
        )
        val ship2 = Ship(
            2, "white_pearl", 1, mutableMapOf(), 2, Pair(4, 2),
            Direction.EAST, 15, 10, 10, 10, 1000,
            10, 1000, -1, shipState2, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = true
        )
        val ship3 = Ship(
            3, "gray_pearl", 2, mutableMapOf(), 3, Pair(4, 2),
            Direction.EAST, 15, 10, 10, 10, 1000,
            10, 0, -1, shipState3, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = true
        )
        ship2.capacityInfo[GarbageType.OIL] = Pair(0, 1000)
        ship3.capacityInfo[GarbageType.PLASTIC] = Pair(0, 1000)
        simDat.ships.addAll(listOf(ship, ship2, ship3))
        simDat.corporations[0].ships.addAll(listOf(ship, ship2))
        simDat.corporations[1].ships.addAll(listOf(ship3))
        val method = CorporationManager::class.java.getDeclaredMethod(
            "checkShipOnHarborAndNeedsToRefuelOrUnload",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        method.invoke(cm, ship, simDat.corporations[0])
        method.invoke(cm, ship2, simDat.corporations[0])
        method.invoke(cm, ship3, simDat.corporations[1])
        assertEquals(shipState1, ship.state)
        assertEquals(shipState2, ship2.state)
        assertEquals(shipState3, ship3.state)
    }

    @Test
    fun test_checkShipNeedsToRefuelOrUnloadShouldReturnHomeHarbors() {
        val shipState1 = ShipState.NEED_REFUELING
        val shipState2 = ShipState.NEED_UNLOADING
        val shipState3 = ShipState.NEED_REFUELING_AND_UNLOADING
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(4, 0),
            Direction.EAST, 5, 10, 10, 10, 1000,
            10, 0, -1, shipState1, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = true
        )
        val ship2 = Ship(
            2, "white_pearl", 1, mutableMapOf(), 2, Pair(4, 2),
            Direction.EAST, 15, 10, 10, 10, 1000,
            10, 1000, -1, shipState2, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = true
        )
        val ship3 = Ship(
            3, "gray_pearl", 2, mutableMapOf(), 3, Pair(4, 2),
            Direction.EAST, 15, 10, 10, 10, 1000,
            10, 0, -1, shipState3, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = true
        )
        ship2.capacityInfo[GarbageType.OIL] = Pair(0, 1000)
        ship3.capacityInfo[GarbageType.PLASTIC] = Pair(0, 1000)
        simDat.ships.addAll(listOf(ship, ship2, ship3))
        simDat.corporations[0].ships.addAll(listOf(ship, ship2))
        simDat.corporations[1].ships.addAll(listOf(ship3))
        val method = CorporationManager::class.java.getDeclaredMethod(
            "determineBehavior",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        val locationsToGoTo1 = method.invoke(cm, ship, simDat.corporations[0])
        val locationsToGoTo2 = method.invoke(cm, ship2, simDat.corporations[0])
        val locationsToGoTo3 = method.invoke(cm, ship3, simDat.corporations[1])
        val corporation1Harbors = simDat.corporations[0].harbors
        val corporation2Harbors = simDat.corporations[1].harbors
        assertEquals(corporation1Harbors, locationsToGoTo1)
        assertEquals(corporation1Harbors, locationsToGoTo2)
        assertEquals(corporation2Harbors, locationsToGoTo3)
    }

    @Test
    fun test_isWaitingForPlastic() {
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.WAITING_FOR_PLASTIC, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        simDat.ships.addAll(listOf(ship))
        simDat.corporations[0].ships.addAll(listOf(ship))
        val method = CorporationManager::class.java.getDeclaredMethod(
            "determineBehavior",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        val tilesToMoveTo = method.invoke(cm, ship, simDat.corporations[0])
        assertEquals(listOf(Pair(0, 0)), tilesToMoveTo)
    }

    @Test
    fun test_handleTaskedState() {
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, 1, ShipState.TASKED, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        simDat.ships.addAll(listOf(ship))
        simDat.corporations[0].ships.addAll(listOf(ship))
        val method = CorporationManager::class.java.getDeclaredMethod(
            "handleTaskedState",
            Ship::class.java
        )
        method.isAccessible = true
        val tilesToMoveTo = method.invoke(cm, ship)
        assertEquals(listOf(Pair(2, 1)), tilesToMoveTo)
    }

    @Test
    fun test_isTaskedAndDetermineBehaviour() {
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, 1, ShipState.TASKED, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        simDat.ships.addAll(listOf(ship))
        simDat.corporations[0].ships.addAll(listOf(ship))
        val method = CorporationManager::class.java.getDeclaredMethod(
            "determineBehavior",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        val tilesToMoveTo = method.invoke(cm, ship, simDat.corporations[0])
        assertEquals(listOf(Pair(2, 1)), tilesToMoveTo)
    }

    @Test
    fun test_defaultBehaviourOfCollectingShip() {
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 0, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ship2 = Ship(
            2, "white_pearl", 2, mutableMapOf(), 0, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val garbage = Garbage(1, 50, GarbageType.OIL, 2, Pair(1, 0))
        simDat.ships.addAll(listOf(ship))
        simDat.corporations[0].ships.addAll(listOf(ship))
        val method = CorporationManager::class.java.getDeclaredMethod(
            "determineBehavior",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        var tilesToMoveTo = method.invoke(cm, ship, simDat.corporations[0])
        assertEquals(listOf(Pair(0, 0)), tilesToMoveTo)
        simDat.garbage.add(garbage)
        simDat.corporations[0].visibleGarbage[garbage.id] = Pair(Pair(1, 0), GarbageType.OIL)
        tilesToMoveTo = method.invoke(cm, ship, simDat.corporations[0])
        assertEquals(listOf(Pair(1, 0)), tilesToMoveTo)

        tilesToMoveTo = method.invoke(cm, ship2, simDat.corporations[1])
        assertEquals(listOf(Pair(0, 0)), tilesToMoveTo)
        simDat.corporations[1].visibleGarbage[garbage.id] = Pair(Pair(1, 0), GarbageType.OIL)
        tilesToMoveTo = method.invoke(cm, ship2, simDat.corporations[1])
        assertEquals(listOf(Pair(0, 0)), tilesToMoveTo)
    }

    @Test
    fun test_defaultBehaviourOfScoutingShip() {
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 0, Pair(0, 0),
            Direction.EAST, 1, 20, 20, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ship2 = Ship(
            2, "white_pearl", 1, mutableMapOf(), 0, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val garbage = Garbage(1, 50, GarbageType.OIL, 2, Pair(1, 0))
        val garbage2 = Garbage(2, 50, GarbageType.PLASTIC, 2, Pair(2, 0))
        simDat.ships.addAll(listOf(ship, ship2))
        simDat.corporations[0].ships.addAll(listOf(ship))
        val method = CorporationManager::class.java.getDeclaredMethod(
            "determineBehavior",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        var tilesToMoveTo = method.invoke(cm, ship, simDat.corporations[0])
        assertEquals(listOf(Pair(2, 0)), tilesToMoveTo)
        simDat.corporations[0].garbage[garbage.id] = Pair(garbage.location, garbage.type)
        tilesToMoveTo = method.invoke(cm, ship, simDat.corporations[0])
        assertEquals(listOf(Pair(1, 0)), tilesToMoveTo)
        simDat.corporations[0].visibleGarbage[garbage2.id] = Pair(garbage2.location, garbage2.type)
        tilesToMoveTo = method.invoke(cm, ship2, simDat.corporations[0])
        assertEquals(listOf(Pair(2, 0)), tilesToMoveTo)
    }

    @Test
    fun test_defaultBehaviourOfCooperatingShip() {
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 0, Pair(0, 0),
            Direction.EAST, 1, 20, 20, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COORDINATING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ship2 = Ship(
            2, "white_pearl", 2, mutableMapOf(), 0, Pair(0, 1),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COORDINATING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        simDat.ships.addAll(listOf(ship, ship2))
        simDat.corporations[0].ships.addAll(listOf(ship))
        val method = CorporationManager::class.java.getDeclaredMethod(
            "determineBehavior",
            Ship::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
        var tilesToMoveTo = method.invoke(cm, ship, simDat.corporations[0])
        assertEquals(listOf(Pair(2, 0)), tilesToMoveTo)
        simDat.corporations[0].visibleShips[ship2.id] = Pair(ship2.id, ship2.location)
        tilesToMoveTo = method.invoke(cm, ship, simDat.corporations[0])
        assertEquals(listOf(Pair(0, 1)), tilesToMoveTo)
    }

    @Test
    fun test_updateInfo() {
        val garbage1 = Garbage(1, 50, GarbageType.OIL, 2, Pair(1, 0))
        val garbage2 = Garbage(2, 50, GarbageType.PLASTIC, 2, Pair(2, 0))
        val ship1 = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ship2 = Ship(
            2, "white_pearl", 1, mutableMapOf(), 2, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val method = CorporationManager::class.java.getDeclaredMethod(
            "updateInfo",
            Corporation::class.java,
            Pair::class.java
        )
        method.isAccessible = true
        val garbageMap = mutableMapOf(
            garbage1.id to Pair(garbage1.location, garbage1.type),
            garbage2.id to Pair(garbage2.location, garbage2.type)
        )
        val shipMap = mutableMapOf(
            ship1.id to Pair(ship1.corporation, ship1.location),
            ship2.id to Pair(ship2.corporation, ship2.location)
        )
        val visibleShipMap = mutableMapOf(
            ship1.id to Pair(ship1.corporation, ship1.location),
            ship2.id to Pair(ship2.corporation, ship2.location)
        )
        method.invoke(cm, simDat.corporations[0], Pair(shipMap, garbageMap))
        assertEquals(garbageMap, simDat.corporations[0].visibleGarbage)
        assertEquals(visibleShipMap, simDat.corporations[0].visibleShips)
    }

    @Test
    fun test_assignCapacity() {
        val garbageAssignedAmountList: MutableList<Garbage> = mutableListOf()
        val garbage1 = Garbage(1, 50, GarbageType.OIL, 2, Pair(1, 0))
        val garbage2 = Garbage(2, 50, GarbageType.PLASTIC, 2, Pair(2, 0))

        val method = CorporationManager::class.java.getDeclaredMethod(
            "assignCapacity",
            Garbage::class.java,
            Int::class.java,
            MutableList::class.java
        )
        method.isAccessible = true
        val shipCapacityLeft = method.invoke(cm, garbage1, 60, garbageAssignedAmountList)
        assertEquals(50, garbage1.assignedCapacity)
        assertEquals(listOf(garbage1), garbageAssignedAmountList)
        assertEquals(10, shipCapacityLeft)
        val shipCapacityLeft2 = method.invoke(cm, garbage1, 50, garbageAssignedAmountList)
        assertEquals(50, garbage1.assignedCapacity) //Assigned capacity should stay the same
        assertEquals(listOf(garbage1,garbage1), garbageAssignedAmountList)
        assertEquals(50, shipCapacityLeft2)
        val shipCapacityLeft3 = method.invoke(cm, garbage2, 30, garbageAssignedAmountList)
        assertEquals(30, garbage2.assignedCapacity) //Assigned capacity should be only 30
        assertEquals(listOf(garbage1,garbage1,garbage2), garbageAssignedAmountList)
        assertEquals(0, shipCapacityLeft3)
    }

    @Test
    fun test_assignCapacityToGarbageList() {
        val method = CorporationManager::class.java.getDeclaredMethod(
            "assignCapacityToGarbageList",
            Int::class.java,
            Map::class.java
        )
        method.isAccessible = true
        val garbage1 = Garbage(1, 500, GarbageType.OIL, 2, Pair(1, 0))
        val garbage2 = Garbage(2, 1000, GarbageType.PLASTIC, 2, Pair(1, 0))
        simDat.garbage.addAll(listOf(garbage1, garbage2))
        t2.addGarbageToTile(garbage1)
        t2.addGarbageToTile(garbage2)
        val collectableGarbageShip1: Map<GarbageType, Pair<Int, Int>> = mapOf(
            GarbageType.OIL to Pair(1000, 1000),
            GarbageType.PLASTIC to Pair(50, 50)
        )
        val collectableGarbageShip2: Map<GarbageType, Pair<Int, Int>> = mapOf(
            GarbageType.OIL to Pair(1000, 1000),
            GarbageType.PLASTIC to Pair(1000, 1000)
        )
        val garbageToBeUpdated = method.invoke(cm, 2, collectableGarbageShip1) as List<*>
        assertEquals(2, garbageToBeUpdated.size)
        assertEquals(500, garbage1.assignedCapacity)
        assertEquals(50, garbage2.assignedCapacity)
        val garbageToBeUpdated2 = method.invoke(cm, 2, collectableGarbageShip2) as List<*>
        assertEquals(2, garbageToBeUpdated2.size)
        assertEquals(500, garbage1.assignedCapacity)
        assertEquals(1000, garbage2.assignedCapacity)
    }

    @Test
    fun test_applyTrackersForCorporation() {
        val method = CorporationManager::class.java.getDeclaredMethod(
            "applyTrackersForCorporation",
            Corporation::class.java
        )
        method.isAccessible = true
        val ship1 = Ship(
            1, "black_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = true, travelingToHarbor = false
        )
        val ship2 = Ship(
            2, "white_pearl", 1, mutableMapOf(), 1, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = true, travelingToHarbor = false
        )
        val garbage1 = Garbage(1, 50, GarbageType.OIL, 1, Pair(0, 0))
        val garbage2 = Garbage(2, 50, GarbageType.OIL, 1, Pair(0, 0))

        val corporation1 = Corporation("Test_corp1",
            1, listOf(Pair(4, 0)),
            mutableListOf(ship1),
            listOf(GarbageType.OIL)
        )

        val corporation2 = Corporation("Test_corp2",
            2, listOf(Pair(4, 0)),
            mutableListOf(ship2),
            listOf(GarbageType.OIL)
        )
        simDat.garbage.add(garbage1)
        simDat.garbage.add(garbage2)
        method.invoke(cm, corporation1)
        method.invoke(cm, corporation2)
        assertTrue(garbage1.trackedBy.contains(corporation1.id))
        assertTrue(garbage1.trackedBy.contains(corporation2.id))
        assertTrue(garbage2.trackedBy.contains(corporation1.id))
        assertTrue(garbage2.trackedBy.contains(corporation2.id))
    }

    @Test
    fun test_handleDefaultStateCollectingShipGetOnlyAssignableGarbage() {
        val garbage1 = Garbage(1, 50, GarbageType.OIL, 2, Pair(1, 0), assignedCapacity = 50)
        val garbage2 = Garbage(2, 50, GarbageType.PLASTIC, 3, Pair(2, 0))
        val ship1 = Ship(
            1, "black_pearl", 1, mutableMapOf(), 0, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val ship2 = Ship(
            2, "white_pearl", 1, mutableMapOf(), 0, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.COLLECTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val corporation = Corporation("Test_corp1",
            1, listOf(Pair(4, 0)),
            mutableListOf(ship1, ship2),
            listOf(GarbageType.OIL)
        )
        simDat.garbage.addAll(listOf(garbage1, garbage2))
        corporation.visibleGarbage[garbage1.id] = Pair(garbage1.location, garbage1.type)
        corporation.visibleGarbage[garbage2.id] = Pair(garbage2.location, garbage2.type)

        val method = CorporationManager::class.java.getDeclaredMethod(
            "handleDefaultState",
            ShipType::class.java,
            Pair::class.java,
            Int::class.java,
            Corporation::class.java
        )
        method.isAccessible = true
       var output = method.invoke(cm, ship1.type,ship1.location,1,corporation) as List<*>
        assertEquals(1, output.size)
        assertEquals(listOf( Pair(0, 0)), output)
        val garbage3 = Garbage(2, 50, GarbageType.OIL, 3, Pair(2, 0))
        simDat.garbage.add(garbage3)
        corporation.visibleGarbage[garbage3.id] = Pair(garbage3.location, garbage3.type)
        output = method.invoke(cm, ship1.type,ship1.location,1,corporation) as List<*>
        assertEquals(1, output.size)
        assertEquals(listOf(Pair(2, 0)), output)
    }
}
