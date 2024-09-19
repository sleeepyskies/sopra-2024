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
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.io.PrintWriter
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
            Boolean::class.java,
            List::class.java,
            Tile::class.java
        )
        method.isAccessible = true
        val result = method.invoke(travelManager, garbage, false, path, tile) as? Tile

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
            Boolean::class.java,
            List::class.java,
            Tile::class.java
        )
        method.isAccessible = true
        val result = method.invoke(travelManager, garbage, false, path, tile) as? Tile

        // Verify results
        verify(tile, never()).removeGarbageFromTile(garbage)
        verify(candidateTile, never()).addArrivingGarbageToTile(garbage)
        assertEquals(tile, result)
    }

    @Test
    fun testDriftGarbageAlongPath_GarbageSplitAndFitsOnFirstTile() {
        // Mock garbage
        val garbage = mock(Garbage::class.java).apply {
            `when`(id).thenReturn(2)
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
            Boolean::class.java,
            List::class.java,
            Tile::class.java
        )
        method.isAccessible = true
        val result = method.invoke(travelManager, garbage, true, path, tile) as? Tile

        // Verify results
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
            Boolean::class.java,
            List::class.java,
            Tile::class.java
        )
        method.isAccessible = true
        val result = method.invoke(travelManager, garbage, true, path, tile) as? Tile

        // Verify results
        verify(candidateTile2).addArrivingGarbageToTile(garbage)
        assertEquals(candidateTile2, result)
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
        val tilePath = listOf(candidateTile, candidateTile2)

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
        assertEquals(setOf(tile), tilesToUpdate)
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
        method.invoke(
            travelManager,
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
            .thenReturn(listOf(Pair(3, mutableListOf())))
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
            .thenReturn(listOf(Pair(2, listOf(garbage2, garbage3)), Pair(1, listOf(garbage))))
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
            .thenReturn(listOf(Pair(1, listOf(garbage))))
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
