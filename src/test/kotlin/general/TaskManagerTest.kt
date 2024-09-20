package general

import de.unisaarland.cs.se.selab.Logger
import de.unisaarland.cs.se.selab.assets.Current
import de.unisaarland.cs.se.selab.assets.Direction
import de.unisaarland.cs.se.selab.assets.GarbageType
import de.unisaarland.cs.se.selab.assets.Reward
import de.unisaarland.cs.se.selab.assets.RewardType
import de.unisaarland.cs.se.selab.assets.Ship
import de.unisaarland.cs.se.selab.assets.ShipState
import de.unisaarland.cs.se.selab.assets.ShipType
import de.unisaarland.cs.se.selab.assets.SimulationData
import de.unisaarland.cs.se.selab.assets.Task
import de.unisaarland.cs.se.selab.assets.TaskType
import de.unisaarland.cs.se.selab.assets.Tile
import de.unisaarland.cs.se.selab.assets.TileType
import de.unisaarland.cs.se.selab.navigation.NavigationManager
import de.unisaarland.cs.se.selab.tasks.TaskManager
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.PrintWriter
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskManagerTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun init() {
            Logger.setOutput(PrintWriter(System.out, true))
        }
    }

    private lateinit var taskManager: TaskManager

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

    @BeforeEach
    fun setUp() {
        /**
         simulationData = SimulationData(
         mock(), emptyList(), mutableListOf(), mutableListOf(), mutableListOf(),
         mutableMapOf(), mutableMapOf(), mutableListOf(), mutableListOf(), 0
         )**/
        this.nm = NavigationManager(map)
        this.nm.initializeAndUpdateGraphStructure()
        sd = SimulationData(
            nm, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableMapOf(),
            mutableMapOf(),
            mutableListOf(), mutableListOf(), 0, 1
        )
        taskManager = TaskManager(sd)
    }

    /**
     * Test the startTasksPhase method
     * Add ships to simdata, ship location should be target task location so we can reward the ship
     * The task ship id should be the same as the ship id
     */

    /*@Test
    fun `test startTaskPhase with active tasks and completed task`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            -1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            1,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(sd.rewards.isEmpty())
        assertTrue(sd.ships[0].capacityInfo[GarbageType.OIL]?.second == 100)
    }*/

    /** Test for task target loc different from ship loc **/
    @Test
    fun `test startTaskPhase with task target location different from ship location but ship can reach it`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            -1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            3,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        // should not be empty because the ships needs a tick to reach the target location
        assertTrue(sd.activeTasks.isNotEmpty())
        // rewards should not be granted so they should stay in list
        assertTrue(sd.rewards.isNotEmpty())
        // ship should be tasked because the task wasn't completed
        assertTrue(ship.state == ShipState.TASKED)
        // reward was not granted because the ship has not reached the target location
        assertFalse(sd.ships[0].capacityInfo[GarbageType.OIL]?.second == 100)
    }

    /** Test for tasked ship task completed **/
    /*@Test
    fun `test startTaskPhase for a tasked ship but the task is completed`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            -1, state = ShipState.TASKED, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            1,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()

        // Verify that the task is removed from active tasks
        assertTrue(sd.activeTasks.isEmpty())
        // Verify that the reward is granted
        assertTrue(sd.rewards.isEmpty())
        // Verify that the ship's state is updated
        assertEquals(ShipState.DEFAULT, ship.state)
        // Verify that the ship received the reward
        assertEquals(100, ship.capacityInfo[GarbageType.OIL]?.second)
    }*/

    /** Test for tasked ship task not completed **/
    @Test
    fun `test for a tasked ship which has not completed the task yet`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            1, state = ShipState.TASKED, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            2,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()

        // Verify that the task is still in active tasks
        assertTrue(sd.activeTasks.contains(task))
        // Verify that the reward is not granted
        assertTrue(sd.rewards.contains(reward))
        // Verify that the ship's state is still TASKED
        assertEquals(ShipState.TASKED, ship.state)
        // Verify that the ship did not receive the reward
        assertFalse(sd.ships[0].capacityInfo[GarbageType.OIL]?.second == 100)
    }

    /** Test for task tick is not current tick **/
    @Test
    fun `test for a task which is not happening on the current tick`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            -1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            2,
            1,
            2,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[2] = listOf(task)
        sd.tick = 0
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        // Verify that the task is not added to scheduled tasks
        assertTrue(sd.scheduledTasks[sd.tick].isNullOrEmpty())
        // Verify that the task is not added to active tasks
        assertTrue(sd.activeTasks.isEmpty())
        // Verify that the reward is not granted
        assertTrue(sd.rewards.contains(reward))
        // Verify that the ship's state is not changed
        assertEquals(ShipState.DEFAULT, ship.state)
        // Verify that the ship did not receive the reward
        assertFalse(sd.ships[0].capacityInfo[GarbageType.OIL]?.second == 100)
    }

    /** Test for conditions when we cannot assign the task and check ships state**/
    /** Test for a ship which needs unloading but is also assigned a task **/
    @Test
    fun `ship cannot get task assigned because it is in unloading state`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            -1, state = ShipState.NEED_UNLOADING, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            2,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isNotEmpty())
        assertTrue(sd.rewards.contains(reward))
        assertEquals(ShipState.TASKED, ship.state)
        assertTrue(ship.currentTaskId == 1)
    }

    /** Test for a ship which needs refueling but is also assigned a task **/

    @Test
    fun `ship needs refueling but also has a task`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            -1, state = ShipState.NEED_REFUELING, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            2,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)
        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(ship.state == ShipState.NEED_REFUELING)
        assertTrue(ship.currentTaskId == -1)
    }

    /** Test for correct granting of rewards **/

    /*@Test
    fun `ship completes a task and gets a container reward`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            1,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(sd.rewards.isEmpty())
        assertTrue(ship.state == ShipState.DEFAULT)
        assertTrue(ship.capacityInfo[GarbageType.OIL]?.second == 100)
    }*/

    /*@Test
    fun `ship completes a task and gets a telescope reward`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            1,
            1,
            1
        )
        val reward = Reward(1, RewardType.TELESCOPE, 10, 0, GarbageType.NONE)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(sd.rewards.isEmpty())
        assertTrue(ship.state == ShipState.DEFAULT)
        assertTrue(ship.visibilityRange == 20)
    }*/

    /*@Test
    fun `ship completes a task and gets a tracker reward`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            1,
            1,
            1
        )
        val reward = Reward(1, RewardType.TRACKING, 0, 0, GarbageType.NONE)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(sd.rewards.isEmpty())
        assertTrue(ship.state == ShipState.DEFAULT)
        assertTrue(ship.hasTracker)
    }*/

    /*@Test
    fun `ship completes a task and gets a radio reward`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 100,
            1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            1,
            1,
            1
        )
        val reward = Reward(1, RewardType.RADIO, 0, 0, GarbageType.NONE)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(sd.rewards.isEmpty())
        assertTrue(ship.state == ShipState.DEFAULT)
        assertTrue(ship.hasRadio)
    }*/

    /*
    @Test
    fun `ship doesnt have enough fuel to complete the task and go back to harbor`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 0), direction = Direction.EAST,
            1,
            25, 0, 20, 100, 10, 20,
            -1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            2,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(ship.state == ShipState.DEFAULT)
        assertTrue(ship.currentTaskId == -1)
    }
    */

    @Test
    fun `ship has no path to task`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(), 10, Pair(1, 3), direction = Direction.EAST,
            16,
            25, 0, 20, 100, 10, 100,
            -1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            18,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(ship.state == ShipState.DEFAULT)
        assertTrue(ship.currentTaskId == -1)
    }

    /*@Test
    fun `test grant reward with container of already collectable garbage reward`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(GarbageType.OIL to Pair(100, 100)), 10,
            Pair(1, 3), direction = Direction.EAST,
            16,
            25, 0, 20, 100, 10, 100,
            -1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            16,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.OIL)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(sd.rewards.isEmpty())
        assertTrue(ship.state == ShipState.DEFAULT)
        assertTrue(ship.capacityInfo[GarbageType.OIL]?.second == 200)
    }*/

    /*@Test
    fun `test grant reward with container of not yet collectable garbage reward`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(GarbageType.OIL to Pair(100, 100)), 10,
            Pair(1, 3), direction = Direction.EAST,
            16,
            25, 0, 20, 100, 10, 100,
            -1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            16,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.PLASTIC)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(sd.rewards.isEmpty())
        assertTrue(ship.state == ShipState.DEFAULT)
        assertTrue(ship.capacityInfo[GarbageType.OIL]?.second == 100)
        assertTrue(ship.capacityInfo[GarbageType.PLASTIC]?.second == 100)
    }*/

    /*@Test
    fun `test no active tasks`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(GarbageType.OIL to Pair(100, 100)), 10,
            Pair(1, 3), direction = Direction.EAST,
            16,
            25, 0, 20, 100, 10, 100,
            -1, state = ShipState.DEFAULT, ShipType.COLLECTING_SHIP, false, false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            3,
            1,
            16,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.PLASTIC)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
    }*/

    @Test
    fun `with refueling and unloading state but cannot reach harbor`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(GarbageType.OIL to Pair(100, 100)), 10,
            Pair(0, 4), direction = Direction.EAST,
            20,
            25, 0, 20, 100, 10, 20,
            -1, state = ShipState.NEED_REFUELING_AND_UNLOADING, ShipType.COLLECTING_SHIP, false,
            false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            16,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.PLASTIC)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(ship.state == ShipState.NEED_REFUELING_AND_UNLOADING)
        assertTrue(ship.currentTaskId == -1)
    }

    @Test
    fun `with refueling and unloading state but tile does not exist`() {
        val ship = Ship(
            1, "duxas", 1, mutableMapOf(GarbageType.OIL to Pair(100, 100)), 10,
            Pair(69, 420), direction = Direction.EAST,
            34,
            25, 0, 20, 100, 10, 20,
            -1, state = ShipState.NEED_REFUELING_AND_UNLOADING, ShipType.COLLECTING_SHIP, false,
            false,
            false
        )
        val task = Task(
            1,
            TaskType.COLLECT,
            1,
            1,
            16,
            1,
            1
        )
        val reward = Reward(1, RewardType.CONTAINER, 0, 100, GarbageType.PLASTIC)
        sd.ships.add(ship)
        sd.scheduledTasks[1] = listOf(task)
        sd.tick = 1
        sd.rewards.add(reward)

        taskManager.startTasksPhase()
        assertTrue(sd.activeTasks.isEmpty())
        assertTrue(ship.state == ShipState.NEED_REFUELING_AND_UNLOADING)
        assertTrue(ship.currentTaskId == -1)
    }
}
