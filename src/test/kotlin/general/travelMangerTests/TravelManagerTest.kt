package general.travelMangerTests
/*
import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.*
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import de.unisaarland.cs.se.selab.travelling.TravelManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.*
import java.io.PrintWriter
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TravelManagerTest {
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

    @Test
    fun testShipDriftingPhase_MultipleShipsOnSameTile() {
        // Mock ships
        val ship1 = mock(Ship::class.java).apply {
            `when`(tileId).thenReturn(1)
            `when`(location).thenReturn(Pair(1, 1))
        }
        val ship2 = mock(Ship::class.java).apply {
            `when`(tileId).thenReturn(1)
            `when`(location).thenReturn(Pair(1, 1))
        }

        // Mock current
        val tileCurrent = mock(Current::class.java).apply {
            `when`(intensity).thenReturn(2)
            `when`(direction).thenReturn(Direction.EAST)
            `when`(speed).thenReturn(1)
        }

        // Mock tile
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

        // Set up mocks
        `when`(simDat.ships).thenReturn(mutableListOf(ship1, ship2))
        `when`(navigationManager.findTile(1)).thenReturn(tile)
        `when`(
            navigationManager.calculateDrift(tile.location, tileCurrent.direction, tileCurrent.speed)
        ).thenReturn(tilePath)

        // Call the method under test
        travelManager.shipDriftingPhase()

        // Verify results
        verify(navigationManager).findTile(1)
        verify(navigationManager, times(2)).calculateDrift(tile.location, tileCurrent.direction, tileCurrent.speed)
        verify(ship1).location = Pair(2, 1)
        verify(ship1).tileId = 2
        verify(ship2).location = Pair(2, 1)
        verify(ship2).tileId = 2
    }

    @Test
    fun testUpdateTiles() {
        // Mock tiles
        val tile1 = mock(Tile::class.java)
        val tile2 = mock(Tile::class.java)
        val tile3 = mock(Tile::class.java)

        // Create a list of mocked tiles
        val tiles = setOf(tile1, tile2, tile3) as Set<Tile>
        val method = TravelManager::class.java.getDeclaredMethod("updateTiles", Set::class.java)
        // Call the method under test
        method.isAccessible = true
        // Call the method under test
        method.invoke(travelManager, tiles)

        // Verify that moveAllArrivingGarbageToTile was called on each mocked tile
        verify(tile1).moveAllArrivingGarbageToTile()
        verify(tile2).moveAllArrivingGarbageToTile()
        verify(tile3).moveAllArrivingGarbageToTile()
    }

    @Test
    fun testDriftGarbageAlongPath_GarbageFitsOnFirstTile() {
        // Mock garbage
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(50)
            `when`(type).thenReturn(GarbageType.CHEMICALS)
        }
        val oldGarbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(50)
            `when`(type).thenReturn(GarbageType.CHEMICALS)
        }

        // Mock tiles
        val tile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(location).thenReturn(Pair(1, 1))
        }
        val candidateTile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(location).thenReturn(Pair(2, 1))
            `when`(canGarbageFitOnTile(garbage)).thenReturn(true)
        }
        val path = listOf(candidateTile)

        // Set up mocks
        `when`(simDat.currentHighestGarbageID).thenReturn(1)

        // Use reflection to call the private method
        val method = TravelManager::class.java.getDeclaredMethod(
            "driftGarbageAlongPath",
            Garbage::class.java,
            Garbage::class.java,
            Boolean::class.java,
            List::class.java,
            Tile::class.java,
            Int::class.java
        )
        method.isAccessible = true
        val result = method.invoke(travelManager, garbage, oldGarbage, false, path, tile, 50) as Tile?

        // Verify results
        verify(tile).removeGarbageFromTile(garbage)
        verify(candidateTile).addArrivingGarbageToTile(garbage)
        assertEquals(candidateTile, result)
    }

    @Test
    fun testDriftGarbageAlongPath_GarbageDoesNotFitOnAnyTile() {
        // Mock garbage
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(50)
            `when`(type).thenReturn(GarbageType.CHEMICALS)
        }
        val oldGarbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(50)
            `when`(type).thenReturn(GarbageType.CHEMICALS)
        }

        // Mock tiles
        val tile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(location).thenReturn(Pair(1, 1))
        }
        val candidateTile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(location).thenReturn(Pair(2, 1))
            `when`(canGarbageFitOnTile(garbage)).thenReturn(false)
        }
        val path = listOf(candidateTile)

        // Use reflection to call the private method
        val method = TravelManager::class.java.getDeclaredMethod(
            "driftGarbageAlongPath",
            Garbage::class.java,
            Garbage::class.java,
            Boolean::class.java,
            List::class.java,
            Tile::class.java,
            Int::class.java
        )
        method.isAccessible = true
        val result = method.invoke(travelManager, garbage, oldGarbage, false, path, tile, 50) as Tile?

        // Verify results
        verify(tile, never()).removeGarbageFromTile(garbage)
        verify(candidateTile, never()).addArrivingGarbageToTile(garbage)
        assertNull(result)
    }

    @Test
    fun testDriftGarbageAlongPath_GarbageSplitAndFitsOnFirstTile() {
        // Mock garbage
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(amount).thenReturn(50)
            `when`(type).thenReturn(GarbageType.CHEMICALS)
        }
        val oldGarbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(100)
            `when`(type).thenReturn(GarbageType.CHEMICALS)
        }

        // Mock tiles
        val tile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(location).thenReturn(Pair(1, 1))
        }
        val candidateTile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(location).thenReturn(Pair(2, 1))
            `when`(canGarbageFitOnTile(garbage)).thenReturn(true)
        }
        val path = listOf(candidateTile)

        // Set up mocks
        `when`(simDat.currentHighestGarbageID).thenReturn(1)

        // Use reflection to call the private method
        val method = TravelManager::class.java.getDeclaredMethod(
            "driftGarbageAlongPath",
            Garbage::class.java,
            Garbage::class.java,
            Boolean::class.java,
            List::class.java,
            Tile::class.java,
            Int::class.java
        )
        method.isAccessible = true
        val result = method.invoke(travelManager, garbage, oldGarbage, true, path, tile, 50) as Tile?

        // Verify results
        verify(tile).setAmountOfGarbage(oldGarbage.id, oldGarbage.amount - 50)
        verify(candidateTile).addArrivingGarbageToTile(garbage)
        assertEquals(candidateTile, result)
    }

    @Test
    fun testDriftGarbageAlongPath_GarbageSplitAndFitsOnSecondTile() {
        // Mock garbage
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(amount).thenReturn(50)
            `when`(type).thenReturn(GarbageType.CHEMICALS)
        }
        val oldGarbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(100)
            `when`(type).thenReturn(GarbageType.CHEMICALS)
        }

        // Mock tiles
        val tile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(location).thenReturn(Pair(1, 1))
        }
        val candidateTile1 = mock(Tile::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(location).thenReturn(Pair(3, 1))
            `when`(canGarbageFitOnTile(garbage)).thenReturn(false)
        }
        val candidateTile2 = mock(Tile::class.java).apply {
            `when`(id).thenReturn(3)
            `when`(location).thenReturn(Pair(2, 1))
            `when`(canGarbageFitOnTile(garbage)).thenReturn(true)
        }
        val path = listOf(candidateTile1, candidateTile2)

        // Set up mocks
        `when`(simDat.currentHighestGarbageID).thenReturn(2)

        // Use reflection to call the private method
        val method = TravelManager::class.java.getDeclaredMethod(
            "driftGarbageAlongPath",
            Garbage::class.java,
            Garbage::class.java,
            Boolean::class.java,
            List::class.java,
            Tile::class.java,
            Int::class.java
        )
        method.isAccessible = true
        val result = method.invoke(travelManager, garbage, oldGarbage, true, path, tile, 50) as? Tile

        // Verify results
        verify(tile).setAmountOfGarbage(oldGarbage.id, oldGarbage.amount - 50)
        verify(candidateTile2).addArrivingGarbageToTile(garbage)
        assertEquals(candidateTile2, result)
        verify(tile, never()).removeGarbageFromTile(garbage)
        verify(candidateTile1, never()).addArrivingGarbageToTile(garbage)
    }

    @Test
    fun testHandleGarbageDrift_TwoGarbageOnSameTileSecondOneHasToBeSplit() {
        // Mock garbage
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(50)
            `when`(type).thenReturn(GarbageType.PLASTIC)
        }
        val garbage2 = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(amount).thenReturn(100)
            `when`(type).thenReturn(GarbageType.OIL)
            `when`(checkSplit(50)).thenReturn(true)
        }
        val garbage3 = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(3)
            `when`(amount).thenReturn(50)
            `when`(type).thenReturn(GarbageType.OIL)
        }

        // Mock tile
        val tile = mock(Tile::class.java).apply {
            `when`(location).thenReturn(Pair(1, 1))
            `when`(id).thenReturn(1)
            `when`(checkGarbageLeft()).thenReturn(true)
        }
        val candidateTile = mock(Tile::class.java).apply {
            `when`(location).thenReturn(Pair(2, 1))
            `when`(id).thenReturn(2)
            `when`(canGarbageFitOnTile(garbage)).thenReturn(true)
            `when`(canGarbageFitOnTile(garbage3)).thenReturn(true)
        }
        val tilePath = listOf(candidateTile)

        // Set up mocks
        `when`(navigationManager.calculateDrift(tile.location, Direction.EAST, 1)).thenReturn(tilePath)
        `when`(travelManager.split(garbage2, 50)).thenReturn(garbage3)

        // Create mutable list of garbage and tiles to update
        val mutableSetGarbage = mutableListOf(garbage, garbage2)
        val tilesToUpdate = mutableSetOf<Tile>()

        // Use reflection to call the private method
        val method = TravelManager::class.java.getDeclaredMethod(
            "handleGarbageDrift",
            Tile::class.java,
            MutableList::class.java,
            Direction::class.java,
            Int::class.java,
            MutableSet::class.java,
            Int::class.java
        )
        method.isAccessible = true
        method.invoke(travelManager, tile, mutableSetGarbage, Direction.EAST, 1, tilesToUpdate, 100)

        // Verify results
        verify(tile, times(2)).checkGarbageLeft()
        verify(navigationManager, times(2)).calculateDrift(tile.location, Direction.EAST, 1)
        verify(tile).setAmountOfGarbage(garbage2.id, garbage2.amount - 50)
        verify(tile).removeGarbageFromTile(garbage)
        verify(candidateTile).addArrivingGarbageToTile(garbage)
        verify(candidateTile).addArrivingGarbageToTile(garbage3)
        assertEquals(1, tilesToUpdate.size)
        assertTrue(tilesToUpdate.contains(candidateTile))
    }

    @Test
    fun testHandleGarbageDrift_NoGarbageLeftOnTile() {
        // Mock garbage
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(50)
        }

        // Mock tile
        val tile = mock(Tile::class.java).apply {
            `when`(location).thenReturn(Pair(1, 1))
            `when`(checkGarbageLeft()).thenReturn(false)
        }

        // Create mutable list of garbage and tiles to update
        val mutableListGarbage = mutableListOf(garbage)
        val tilesToUpdate = mutableSetOf<Tile>()

        // Use reflection to call the private method
        val method = TravelManager::class.java.getDeclaredMethod(
            "handleGarbageDrift",
            Tile::class.java,
            MutableList::class.java,
            Direction::class.java,
            Int::class.java,
            MutableSet::class.java,
            Int::class.java
        )

        method.isAccessible = true
        method.invoke(travelManager, tile, mutableListGarbage, Direction.EAST, 1, tilesToUpdate, 50)

        // Verify results
        verify(tile).checkGarbageLeft()
        verify(navigationManager, never()).calculateDrift(tile.location, Direction.EAST, 1)
        assertTrue(tilesToUpdate.isEmpty())
    }

    @Test
    fun testHandleGarbageDrift_GarbageDoesNotFitOnAnyTile() {
        // Mock garbage
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(50)
        }

        // Mock tile
        val tile = mock(Tile::class.java).apply {
            `when`(location).thenReturn(Pair(1, 1))
            `when`(checkGarbageLeft()).thenReturn(true)
        }
        val candidateTile = mock(Tile::class.java).apply {
            `when`(canGarbageFitOnTile(garbage)).thenReturn(false)
        }
        val candidateTile2 = mock(Tile::class.java).apply {
            `when`(canGarbageFitOnTile(garbage)).thenReturn(false)
        }
        val tilePath = listOf(candidateTile,candidateTile2)

        // Set up mocks
        `when`(navigationManager.calculateDrift(tile.location, Direction.EAST, 1)).thenReturn(tilePath)

        // Create mutable list of garbage and tiles to update
        val mutableListGarbage = mutableListOf(garbage)
        val tilesToUpdate = mutableSetOf<Tile>()

        // Use reflection to call the private method
        val method = TravelManager::class.java.getDeclaredMethod(
            "handleGarbageDrift",
            Tile::class.java,
            MutableList::class.java,
            Direction::class.java,
            Int::class.java,
            MutableSet::class.java,
            Int::class.java
        )
        method.isAccessible = true
        method.invoke(travelManager, tile, mutableListGarbage, Direction.EAST, 1, tilesToUpdate, 50)

        // Verify results
        verify(tile).checkGarbageLeft()
        verify(navigationManager).calculateDrift(tile.location, Direction.EAST, 1)
        verify(tile, never()).removeGarbageFromTile(garbage)
        verify(candidateTile, never()).addArrivingGarbageToTile(garbage)
        assertTrue(tilesToUpdate.isEmpty())
    }

    @Test
    fun testHandleGarbageDrift_EmptyGarbageList() {
        // Mock tile
        val tile = mock(Tile::class.java).apply {
            `when`(location).thenReturn(Pair(1, 1))
        }

        // Create an empty list of garbage
        val mutableListGarbage = mutableListOf<Garbage>()
        val tilesToUpdate = mutableSetOf<Tile>()

        // Call the method under test
        val method = TravelManager::class.java.getDeclaredMethod(
            "handleGarbageDrift",
            Tile::class.java,
            MutableList::class.java,
            Direction::class.java,
            Int::class.java,
            MutableSet::class.java,
            Int::class.java
        )
        method.isAccessible = true
        method.invoke(travelManager,
            tile,
            mutableListGarbage,
            Direction.EAST,
            10,
            tilesToUpdate,
            50
        )

        // Verify no interactions with the tile or navigationManager
        verify(tile, never()).checkGarbageLeft()
        verify(navigationManager, never()).calculateDrift(tile.location, Direction.EAST, 10)
        assertTrue(tilesToUpdate.isEmpty())
    }

    @Test
    fun testDriftGarbagePhase_NoTilesWithCurrents() {
        `when`(navigationManager.getGarbageFromAllTilesInCorrectOrderForDrifting()).thenReturn(emptyList())

        travelManager.driftGarbagePhase()

        verify(navigationManager).getGarbageFromAllTilesInCorrectOrderForDrifting()
    }

    @Test
    fun testDriftGarbagePhase_InvalidTile() {
        val invalidTile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(3)
            `when`(current).thenReturn(null)
        }
        `when`(navigationManager.getGarbageFromAllTilesInCorrectOrderForDrifting())
            .thenReturn(listOf(Pair(3, listOf())))
        `when`(navigationManager.findTile(3)).thenReturn(null)

        travelManager.driftGarbagePhase()

        verify(navigationManager).getGarbageFromAllTilesInCorrectOrderForDrifting()
        verify(invalidTile, never()).current
    }

    @Test
    fun testDriftGarbagePhase_TileWithoutCurrent() {
        val tile = mock(Tile::class.java).apply {
            `when`(hasCurrent).thenReturn(false)
        }
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
        }
        `when`(navigationManager.getGarbageFromAllTilesInCorrectOrderForDrifting())
            .thenReturn(listOf(Pair(1, listOf<Garbage>(garbage))))
        `when`(navigationManager.findTile(1)).thenReturn(tile)

        travelManager.driftGarbagePhase()

        verify(navigationManager).getGarbageFromAllTilesInCorrectOrderForDrifting()
        verify(navigationManager).findTile(1)
        verify(tile, never()).current
    }

    @Test
    fun testDriftGarbagePhase_TileWithCurrentAndGarbage() {
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(50)
        }
        val garbage2 = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(amount).thenReturn(150)
        }
        val garbage3 = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(3)
            `when`(amount).thenReturn(100)
        }
        val tileCurrent = mock(Current::class.java).apply {
            `when`(intensity).thenReturn(2)
            `when`(direction).thenReturn(Direction.EAST)
            `when`(speed).thenReturn(1)
        }
        val tile = mock(Tile::class.java).apply {
            `when`(hasCurrent).thenReturn(true)
            `when`(current).thenReturn(tileCurrent)
        }
        val tile2 = mock(Tile::class.java).apply {
            `when`(hasCurrent).thenReturn(true)
            `when`(current).thenReturn(tileCurrent)
        }

        `when`(navigationManager.getGarbageFromAllTilesInCorrectOrderForDrifting())
            .thenReturn(listOf(Pair(2, listOf(garbage2,garbage3)),Pair(1, listOf(garbage))))
        `when`(navigationManager.findTile(1)).thenReturn(tile)
        `when`(navigationManager.findTile(2)).thenReturn(tile2)

        travelManager.driftGarbagePhase()

        verify(navigationManager).getGarbageFromAllTilesInCorrectOrderForDrifting()
        verify(navigationManager).findTile(1)
        verify(navigationManager).findTile(2)
        verify(tile).current
        verify(tile2).current
    }

    @Test
    fun testDriftGarbagePhase_GarbageSplitAndFitsOnTile() {
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(amount).thenReturn(100)
            `when`(checkSplit(50)).thenReturn(true)
            `when`(type).thenReturn(GarbageType.CHEMICALS)
        }
        val splitGarbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(amount).thenReturn(50)
            `when`(type).thenReturn(GarbageType.CHEMICALS)
        }
        val tileCurrent = mock(Current::class.java).apply {
            `when`(intensity).thenReturn(1)
            `when`(direction).thenReturn(Direction.EAST)
            `when`(speed).thenReturn(1)
        }
        val tile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(1)
            `when`(hasCurrent).thenReturn(true)
            `when`(current).thenReturn(tileCurrent)
            `when`(location).thenReturn(Pair(1, 1))
            `when`(checkGarbageLeft()).thenReturn(true)
        }
        val candidateTile = mock(Tile::class.java).apply {
            `when`(id).thenReturn(2)
            `when`(canGarbageFitOnTile(splitGarbage)).thenReturn(true)
            `when`(location).thenReturn(Pair(2, 1))
        }
        val tilePath = listOf(candidateTile)

        `when`(navigationManager.getGarbageFromAllTilesInCorrectOrderForDrifting())
        .thenReturn(listOf(Pair(1,listOf(garbage))))
        `when`(navigationManager.findTile(1)).thenReturn(tile)
        `when`(navigationManager.calculateDrift(tile.location, tileCurrent.direction, tileCurrent.speed))
        .thenReturn(tilePath)
        `when`(travelManager.split(garbage, 50)).thenReturn(splitGarbage)

        travelManager.driftGarbagePhase()

        verify(navigationManager).getGarbageFromAllTilesInCorrectOrderForDrifting()
        verify(navigationManager).findTile(1)
        verify(tile).current
        verify(navigationManager).calculateDrift(tile.location, tileCurrent.direction, tileCurrent.speed)
        verify(tile).setAmountOfGarbage(garbage.id, garbage.amount - 50)
        verify(candidateTile).addArrivingGarbageToTile(splitGarbage)
    }
}
*/
