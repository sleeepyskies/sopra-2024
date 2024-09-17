package general.eventManagerTests

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.OilSpillEvent
import de.unisaarland.cs.se.selab.assets.PirateAttackEvent
import de.unisaarland.cs.se.selab.assets.RestrictionEvent
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipState
import de.unisaarland.cs.se.selab.assets.ShipType
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.StormEvent
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.events.EventManager
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.PrintWriter
import kotlin.test.assertTrue

/**
 * What to test:
 * updatePhase active list empty, one event upcoming return true
 * updatePhase active list empty, one event upcoming return false
 * updatePhase upcoming list empty, one event active end return true
 * updatePhase upcoming list empty, one event active reapply return true*/
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventManagerUnitTest {

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
    private val t1 = Tile(0, Pair(0, 0), TileType.DEEP_OCEAN, false, currentT1, true, 1000)
    private val t2 = Tile(1, Pair(1, 0), TileType.SHALLOW_OCEAN, false, current, false, 1000)
    private val t3 = Tile(2, Pair(2, 0), TileType.SHORE, false, current, false, 1000)
    private val t4 = Tile(3, Pair(3, 0), TileType.SHORE, false, current, false, 1000)
    private val t5 = Tile(4, Pair(4, 0), TileType.SHORE, true, current, false, 1000)

    // row 1
    private val t6 = Tile(5, Pair(0, 1), TileType.DEEP_OCEAN, false, currentT6, true, 1000)
    private val t7 = Tile(6, Pair(1, 1), TileType.SHALLOW_OCEAN, false, current, false, 1000)
    private val t8 = Tile(7, Pair(2, 1), TileType.SHORE, false, current, false, 1000)
    private val t9 = Tile(8, Pair(3, 1), TileType.LAND, false, current, false, 1000)
    private val t10 = Tile(9, Pair(4, 1), TileType.LAND, false, current, false, 1000)

    // row 2
    private val t11 = Tile(10, Pair(0, 2), TileType.SHALLOW_OCEAN, false, current, false, 1000)
    private val t12 = Tile(11, Pair(1, 2), TileType.SHORE, false, current, false, 1000)
    private val t13 = Tile(12, Pair(2, 2), TileType.LAND, false, current, false, 1000)
    private val t14 = Tile(13, Pair(3, 2), TileType.SHORE, false, current, false, 1000)
    private val t15 = Tile(14, Pair(4, 2), TileType.SHORE, true, current, false, 1000)

    // row 3
    private val t16 = Tile(15, Pair(0, 3), TileType.SHALLOW_OCEAN, false, current, false, 1000)
    private val t17 = Tile(16, Pair(1, 3), TileType.SHORE, false, current, false, 1000)
    private val t18 = Tile(17, Pair(2, 3), TileType.LAND, false, current, false, 1000)
    private val t19 = Tile(18, Pair(3, 3), TileType.SHORE, false, current, false, 1000)
    private val t20 = Tile(19, Pair(4, 3), TileType.LAND, false, current, false, 1000)

    // row 4
    private val t21 = Tile(20, Pair(0, 4), TileType.SHORE, false, current, false, 1000)
    private val t22 = Tile(21, Pair(1, 4), TileType.LAND, false, current, false, 1000)
    private val t23 = Tile(22, Pair(2, 4), TileType.SHORE, false, current, false, 1000)
    private val t24 = Tile(23, Pair(3, 4), TileType.LAND, false, current, false, 1000)
    private val t25 = Tile(24, Pair(4, 4), TileType.SHORE, false, current, false, 1000)
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

    // navigation manager for testing
    private lateinit var nm: NavigationManager
    private lateinit var sd: SimulationData
    private lateinit var em: EventManager

    /**
     * Sets up the map for the tests. Map is based on the map on page 36 in the specification.
     */
    @BeforeEach
    fun setup() {
        val ship = Ship(
            1, "black_pearl", 1, mutableMapOf(), 10, Pair(0, 0),
            Direction.EAST, 1, 10, 10, 10, 1000,
            10, 1000, -1, ShipState.DEFAULT, ShipType.SCOUTING_SHIP,
            hasRadio = false, hasTracker = false, travelingToHarbor = false
        )
        val corp = mock<Corporation> {
            on { id } doReturn 1
            on { ships } doReturn mutableListOf(ship)
        }
        this.nm = NavigationManager(map)
        this.nm.initializeAndUpdateGraphStructure()
        sd = SimulationData(
            nm, mutableListOf(corp), mutableListOf(ship), mutableListOf(), mutableListOf(), mutableMapOf(),
            mutableMapOf(),
            mutableListOf(), mutableListOf(), 0, 1
        )
        em = EventManager(sd)
    }

    /** updatePhase active list empty, one event upcoming return true*/
    @Test
    fun applyPirateAttackTest() {
        val pirate = PirateAttackEvent(1, 1, 1)
        sd.scheduledEvents[1] = mutableListOf(pirate)
        em.startEventPhase()
        assertFalse(sd.activeEvents.contains(pirate))
        assertTrue(sd.ships.isEmpty())
        assertTrue(sd.corporations[0].ships.isEmpty())
    }

    /** updatePhase active list empty, one event upcoming return False*/
    @Test
    fun applyOilSpillEventTest() {
        val oilspill1 = OilSpillEvent(0, 1, Pair(0, 0), 1, 500)
        val oilspill2 = OilSpillEvent(16, 1, Pair(1, 3), 2, 500)
        sd.scheduledEvents[1] = mutableListOf(oilspill1, oilspill2)
        em.startEventPhase()
        assertTrue(t1.currentGarbage.isNotEmpty())
        assertTrue(t2.currentGarbage.isNotEmpty())
        assertTrue(t6.currentGarbage.isNotEmpty() && t6.currentOilAmount == 1000)
        assertTrue(t7.currentGarbage.isNotEmpty() && t7.currentOilAmount == 1000)
        assertTrue(t19.currentGarbage.isNotEmpty() && t19.currentOilAmount == 500)
        assertTrue(t18.currentGarbage.isEmpty())
    }

    /** updatePhase upcoming list empty, one event active end return true
     * run a tick so the event is assigned to the active list*/
    @Test
    fun applyStormEventTest() {
        val oilspill1 = OilSpillEvent(0, 1, Pair(0, 0), 1, 500)
        val oilspill2 = OilSpillEvent(16, 1, Pair(1, 3), 2, 500)
        val storm = StormEvent(17, 1, Pair(1, 3), 2, Direction.NORTH_WEST, 20)
        sd.scheduledEvents[1] = mutableListOf(oilspill1, oilspill2, storm)
        em.startEventPhase()
        assertTrue(t6.currentOilAmount == 1000)
        assertTrue(t1.currentOilAmount == 1000)
        assertTrue(t2.currentOilAmount == 1000)
        assertTrue(t8.currentOilAmount == 0)
        assertTrue(t7.currentOilAmount == 1000)
        assertTrue(t11.currentOilAmount == 1000)
        assertTrue(t12.currentOilAmount == 0)
        assertTrue(t16.currentOilAmount == 1000)
        assertTrue(t17.currentOilAmount == 0)
        assertTrue(t19.currentOilAmount == 500)
        assertTrue(t21.currentOilAmount == 0)
        assertTrue(t23.currentOilAmount == 500)
    }

    /** updatePhase upcoming list empty, one event active reapply return true
     * run a tick so the event is assigned to the active list*/
    @Test
    fun applyRestrictionEventTest() {
        val restrictionEvent = RestrictionEvent(1, 1, Pair(1, 0), 2, 1)
        sd.scheduledEvents[1] = mutableListOf(restrictionEvent)
        em.startEventPhase()

        // Check if the restriction is applied correctly
        val restrictedTiles = nm.getTilesInRadius(Pair(1, 0), 2)
        for (coordinates in restrictedTiles) {
            val tile = nm.findTile(coordinates)
            assertNotNull(tile)
            assertTrue(tile!!.isRestricted)
        }

        // Check if the graph structure is updated
        nm.initializeAndUpdateGraphStructure()

        val graph = NavigationManager::class.java.getDeclaredField("graph")
        graph.isAccessible = true
        val gra = graph.get(nm) as Map<*, *>
        for (coordinates in restrictedTiles) {
            val tile = nm.findTile(coordinates)
            assertNotNull(tile)
            assertTrue(gra.containsKey(tile!!.id))
        }
        assertTrue(sd.activeEvents.isNotEmpty())
        sd.tick = 2
        em.startEventPhase()
        for (coordinates in restrictedTiles) {
            val tile = nm.findTile(coordinates)
            assertNotNull(tile)
            assertFalse(tile!!.isRestricted)
        }
    }
}
