package general.travelMangerTests

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.Garbage
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import de.unisaarland.cs.se.selab.travelling.TravelManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.io.PrintWriter
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TravelManagerTest2 {
    private lateinit var navigationManager: NavigationManager
    private lateinit var travelManager: TravelManager
    private lateinit var simDat: SimulationData
    companion object {
        @JvmStatic
        @BeforeAll
        fun init() {
            Logger.setOutput(PrintWriter(System.out, true))
        }
    }

    @BeforeEach
    fun setup() {
        simDat = mock(SimulationData::class.java)
        travelManager = TravelManager(simDat)
        navigationManager = mock(NavigationManager::class.java)
        `when`(simDat.navigationManager).thenReturn(navigationManager)
    }

    @Test
    fun testGetShipsByLowestTileIDThenLowestShipID() {
        // Mock ships
        val ship1 = mock(Ship::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(tileId).thenReturn(1)
        }
        val ship2 = mock(Ship::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(tileId).thenReturn(1)
        }
        val ship3 = mock(Ship::class.java).apply {
            `when`(id).thenReturn(3)
            `when`(tileId).thenReturn(2)
        }
        val ship4 = mock(Ship::class.java).apply {
            `when`(id).thenReturn(4)
            `when`(tileId).thenReturn(2)
        }

        // Mock simData.ships
        `when`(simDat.ships).thenReturn(mutableListOf(ship4, ship2, ship1, ship3))

        val method = TravelManager::class.java.getDeclaredMethod("getShipsByLowestTileIDThenLowestShipID")
        method.isAccessible = true
        // Call the method under test
        val result = method.invoke(travelManager) as List<*>

        // Verify the result
        val expected = listOf(
            1 to listOf(ship1, ship2),
            2 to listOf(ship3, ship4)
        )
        assertEquals(expected, result)
    }

    @Test
    fun testDriftGarbage() {
        val garbage = mock(Garbage::class.java)
        // Define the new location and tile ID
        val newLocation = Pair(5, 5)
        val newTileID = 10

        // Call the method under test
        travelManager.driftGarbage(newLocation, newTileID, garbage)

        // Verify the result
        verify(garbage).location = newLocation
        verify(garbage).tileId = newTileID
    }

    @Test
    fun testDriftShip() {
        val ship = mock(Ship::class.java)
        // Define the new location and tile ID
        val newLocation = Pair(5, 5)
        val newTileID = 10

        // Call the method under test
        travelManager.driftShip(newLocation, newTileID, ship)

        // Verify the result
        verify(ship).location = newLocation
        verify(ship).tileId = newTileID
    }

    @Test
    fun testSplit() {
        // Mock garbage
        val garbage = Garbage(
            id = 1,
            amount = 100,
            type = GarbageType.OIL,
            tileId = 3,
            location = Pair(1, 1),
            assignedCapacity = 0,
            trackedBy = mutableListOf()
        )

        // Mock simData.currentHighestGarbageID
        `when`(simDat.currentHighestGarbageID).thenReturn(1)

        // Call the method under test
        val newGarbage = travelManager.split(garbage, 50)

        assertNotNull(newGarbage, "The newGarbage should not be null")

        // Verify the result
        assertEquals(2, newGarbage.id)
        assertEquals(50, newGarbage.amount)
        assertEquals(mutableListOf<Any>(), newGarbage.trackedBy)
    }

    @Test
    fun testGetRemainingGarbageInOcean() {
        // Mock garbage list
        val garbage1 = mock(Garbage::class.java)
        val garbage2 = mock(Garbage::class.java)
        val garbageList = mutableListOf(garbage1, garbage2)

        // Mock simData.garbage
        `when`(simDat.garbage).thenReturn(garbageList)

        // Call the method under test
        val result = travelManager.getRemainingGarbageInOcean()

        // Verify the result
        assertEquals(garbageList, result)
    }

    @Test
    fun testShipDriftingPhase_NoShips() {
        `when`(simDat.ships).thenReturn(mutableListOf())

        travelManager.shipDriftingPhase()

        verify(navigationManager, never()).findTile(anyInt())
    }

    @Test
    fun testShipDriftingPhase_InvalidStartTile() {
        val ship = mock(Ship::class.java).apply { `when`(tileId).thenReturn(1) }

        `when`(navigationManager.findTile(1)).thenReturn(null)
        `when`(simDat.ships).thenReturn(mutableListOf(ship))

        travelManager.shipDriftingPhase()

        verify(navigationManager, times(1)).findTile(1)
    }

    @Test
    fun testShipDriftingPhase_CurrentHasZeroIntensity() {
        // Mock current with 0 intensity
        val tileCurrent = mock(Current::class.java).apply {
            `when`(intensity).thenReturn(0)
        }

        // Mock tile with the current
        val tile = mock(Tile::class.java).apply {
            `when`(current).thenReturn(tileCurrent)
            `when`(hasCurrent).thenReturn(true)
        }

        // Mock ships
        val ship = mock(Ship::class.java).apply { `when`(tileId).thenReturn(1) }

        // Mock simData.ships to return a list with one ship
        `when`(simDat.ships).thenReturn(mutableListOf(ship))
        `when`(navigationManager.findTile(1)).thenReturn(tile)

        // Call the method under test
        travelManager.shipDriftingPhase()

        // Verify no interactions with the navigationManager beyond findTile
        verify(navigationManager).findTile(1)
        verify(navigationManager, never()).calculateDrift(Pair(anyInt(), anyInt()), Direction.EAST, anyInt())
    }

    @Test
    fun testShipDriftingPhase_NoTilesWithCurrents() {
        val ship = mock(Ship::class.java).apply { `when`(tileId).thenReturn(1) }
        `when`(simDat.ships).thenReturn(mutableListOf(ship))
        `when`(navigationManager.findTile(1)).thenReturn(null)

        travelManager.shipDriftingPhase()

        verify(navigationManager).findTile(1)
    }

    @Test
    fun testShipDriftingPhase_ShipsOnTilesWithoutCurrents() {
        val ship = mock(Ship::class.java).apply { `when`(tileId).thenReturn(1) }
        val tile = mock(Tile::class.java).apply { `when`(hasCurrent).thenReturn(false) }
        `when`(simDat.ships).thenReturn(mutableListOf(ship))
        `when`(navigationManager.findTile(1)).thenReturn(tile)

        travelManager.shipDriftingPhase()

        verify(navigationManager).findTile(1)
        verify(tile, never()).current
    }

    @Test
    fun testShipDriftingPhase_ShipsOnTilesWithCurrentsAndValidDriftPath() {
        val ship = mock(Ship::class.java).apply { `when`(tileId).thenReturn(1) }
        val tileCurrent = mock(Current::class.java).apply {
            `when`(intensity).thenReturn(1)
            `when`(direction).thenReturn(Direction.EAST)
            `when`(speed).thenReturn(1)
        }
        val tile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(location).thenReturn(Pair(1, 1))
            `when`(hasCurrent).thenReturn(true)
            `when`(current).thenReturn(tileCurrent)
        }
        val tileDestination = mock(Tile::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(location).thenReturn(Pair(2, 1))
            `when`(hasCurrent).thenReturn(false)
        }
        val tilePath = listOf(tileDestination)

        `when`(simDat.ships).thenReturn(mutableListOf(ship))
        `when`(navigationManager.findTile(1)).thenReturn(tile)
        `when`(
            navigationManager.calculateDrift(tile.location, tileCurrent.direction, tileCurrent.speed)
        ).thenReturn(tilePath)

        travelManager.shipDriftingPhase()

        verify(navigationManager).findTile(1)
        verify(navigationManager).calculateDrift(tile.location, tileCurrent.direction, tileCurrent.speed)
        verify(ship).location = Pair(2, 1)
        verify(ship).tileId = 2
    }

    @Test
    fun testShipDriftingPhase_ShipsOnTilesWithCurrentsButNoValidDriftPath() {
        val ship = mock(Ship::class.java).apply { `when`(tileId).thenReturn(1) }
        val tileCurrent = mock(Current::class.java).apply {
            `when`(intensity).thenReturn(1)
            `when`(direction).thenReturn(Direction.EAST)
            `when`(speed).thenReturn(1)
        }
        val tile = mock(Tile::class.java).apply {
            `when`(hasCurrent).thenReturn(true)
            `when`(current).thenReturn(tileCurrent)
        }
        `when`(simDat.ships).thenReturn(mutableListOf(ship))
        `when`(navigationManager.findTile(1)).thenReturn(tile)
        `when`(
            navigationManager.calculateDrift(tile.location, tileCurrent.direction, tileCurrent.speed)
        ).thenReturn(emptyList())

        travelManager.shipDriftingPhase()

        verify(navigationManager).findTile(1)
        verify(navigationManager).calculateDrift(tile.location, tileCurrent.direction, tileCurrent.speed)
    }

    @Test
    fun testShipDriftingPhase_MixedShipsOnTilesWithCurrents() {
        // Mock ships
        val ship1 = mock(Ship::class.java).apply {
            `when`(tileId).thenReturn(1)
            `when`(location).thenReturn(Pair(1, 1))
        }
        val ship2 = mock(Ship::class.java).apply {
            `when`(tileId).thenReturn(1)
            `when`(location).thenReturn(Pair(1, 1))
        }
        val ship3 = mock(Ship::class.java).apply {
            `when`(tileId).thenReturn(2)
            `when`(location).thenReturn(Pair(2, 1))
        }
        val ship4 = mock(Ship::class.java).apply {
            `when`(tileId).thenReturn(2)
            `when`(location).thenReturn(Pair(2, 1))
        }
        // Mock currents
        val tileCurrent1 = mock(Current::class.java).apply {
            `when`(intensity).thenReturn(2)
            `when`(direction).thenReturn(Direction.EAST)
            `when`(speed).thenReturn(1)
        }
        val tileCurrent2 = mock(Current::class.java).apply {
            `when`(intensity).thenReturn(1)
            `when`(direction).thenReturn(Direction.EAST)
            `when`(speed).thenReturn(1)
        }
        // Mock tiles
        val tile1 = mock(Tile::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(location).thenReturn(Pair(1, 1))
            `when`(hasCurrent).thenReturn(true)
            `when`(current).thenReturn(tileCurrent1)
        }
        val tile2 = mock(Tile::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(location).thenReturn(Pair(2, 1))
            `when`(hasCurrent).thenReturn(true)
            `when`(current).thenReturn(tileCurrent2)
        }
        val tileDestination = mock(Tile::class.java).apply {
            `when`(id).thenReturn(3)
            `when`(location).thenReturn(Pair(3, 1))
            `when`(hasCurrent).thenReturn(false)
        }
        val tilePath = listOf(tileDestination)
        // Set up mocks
        `when`(simDat.ships).thenReturn(mutableListOf(ship1, ship2, ship3, ship4))
        `when`(navigationManager.findTile(1)).thenReturn(tile1)
        `when`(navigationManager.findTile(2)).thenReturn(tile2)
        `when`(
            navigationManager.calculateDrift(tile1.location, tileCurrent1.direction, tileCurrent1.speed)
        ).thenReturn(tilePath)
        `when`(
            navigationManager.calculateDrift(tile2.location, tileCurrent2.direction, tileCurrent2.speed)
        ).thenReturn(emptyList())
        // Call the method under test
        travelManager.shipDriftingPhase()
        // Verify results
        assert_correct_solution(tile1, tileCurrent1, tile2, tileCurrent2, ship1, ship2, ship3, ship4)
    }

    private fun assert_correct_solution(
        tile1: Tile,
        tileCurrent1: Current,
        tile2: Tile,
        tileCurrent2: Current,
        ship1: Ship,
        ship2: Ship,
        ship3: Ship,
        ship4: Ship
    ) {
        verify(navigationManager).findTile(1)
        verify(navigationManager).findTile(2)
        verify(navigationManager, times(2)).calculateDrift(tile1.location, tileCurrent1.direction, tileCurrent1.speed)
        verify(navigationManager).calculateDrift(tile2.location, tileCurrent2.direction, tileCurrent2.speed)
        verify(ship1).location = Pair(3, 1)
        verify(ship1).tileId = 3
        verify(ship2).location = Pair(3, 1)
        verify(ship2).tileId = 3
        assertEquals(ship3.location, Pair(2, 1))
        assertEquals(ship3.tileId, 2)
        assertEquals(ship4.location, Pair(2, 1))
        assertEquals(ship4.tileId, 2)
    }
}
