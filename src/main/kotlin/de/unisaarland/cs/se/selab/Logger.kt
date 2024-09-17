package de.unisaarland.cs.se.selab
import de.unisaarland.cs.se.selab.assets.RewardType
import de.unisaarland.cs.se.selab.assets.TaskType
import java.io.PrintWriter
import java.util.TreeMap

/**
 * Logger object, used to log specific actions during the simulation
 */
object Logger {
    // Path to the file
    private lateinit var outputBuffer: PrintWriter

    // Map to store collected garbage by corporations
    private lateinit var corporationCollectedGarbage: Map<Int, Triple<Int, Int, Int>>
    var corporationTotalUncollectedGarbage: Int = 0

    /**
     * Set the output stream of the logger
     */
    fun setOutput(outputStream: PrintWriter) {
        if (this::outputBuffer.isInitialized) {
            throw IllegalCallerException("Already initialized output buffer!")
        }
        outputBuffer = outputStream
    }

    /**
     * Initializes the initial collected garbage
     * for each corporation that is actually inside the simulation
     */
    fun setCorporationsInitialCollectedGarbage(corporations: List<Int>) {
        val map = mutableMapOf<Int, Triple<Int, Int, Int>>()
        for (corpId in corporations) {
            map[corpId] = Triple(0, 0, 0)
        }
        corporationCollectedGarbage = map
    }

    /**
     * Logs the initialization of the simulation with the given file name.
     */
    fun initInfo(filePath: String) {
        outputBuffer.println("Initialization Info: $filePath successfully parsed and validated.")
    }

    /**
     * Logs the invalid initialization of the simulation with the given file name.
     */
    fun initInfoInvalid(filePath: String) {
        outputBuffer.println("Initialization Info: $filePath is invalid.")
    }

    /**
     * Logs the start of the simulation.
     */
    fun simulationStart() {
        outputBuffer.println("Simulation Info: Simulation started.")
    }

    /**
     * Logs the end of the simulation.
     */
    fun simulationEnd() {
        outputBuffer.println("Simulation Info: Simulation ended.")
    }

    /**
     * Logs the start of a tick in the simulation.
     */
    fun simTick(tick: Int) {
        outputBuffer.println("Simulation Info: Tick $tick started.")
    }

    /**
     * Logs the start of the corporation phase for a specific corporation.
     */
    fun corporationActionMove(id: Int) {
        outputBuffer.println("Corporation Action: Corporation $id is starting to move its ships.")
    }

    /**
     * Logs the movement of a ship.
     */
    fun shipMovement(shipId: Int, speed: Int, tileId: Int) {
        outputBuffer.println("Ship Movement: Ship $shipId moved with speed $speed to tile $tileId.")
    }

    /**
     * Logs the start of the garbage collection phase for a corporation.
     */
    fun corporationActionCollectGarbage(corpId: Int) {
        outputBuffer.println("Corporation Action: Corporation $corpId is starting to collect garbage.")
    }

    /**
     * Logs the garbage collection of a ship.
     */
    fun garbageCollection(shipId: Int, amt: Int, garbageType: String, garbageId: Int) {
        outputBuffer.println("Garbage Collection: Ship $shipId collected $amt of garbage $garbageType with $garbageId.")
    }

    /**
     * Logs the start of the cooperation phase for a corporation.
     */
    fun corporationActionCooperate(corpId: Int) {
        outputBuffer.println(
            "Corporation Action: Corporation $corpId is starting to cooperate with other corporations."
        )
    }

    /**
     * Logs the cooperation between two corporations.
     */
    fun cooperate(corpId: Int, otherCorpId: Int, shipId: Int, otherShipId: Int) {
        outputBuffer.println(
            "Cooperation: Corporation $corpId " +
                "cooperated with corporation $otherCorpId with ship $shipId to ship $otherShipId."
        )
    }

    /**
     * Logs the start of the refueling phase for a corporation.
     */
    fun corporationActionRefuel(corpId: Int) {
        outputBuffer.println("Corporation Action: Corporation $corpId is starting to refuel.")
    }

    /**
     * Logs the refueling of a ship.
     */
    fun refuel(shipId: Int, tileId: Int) {
        outputBuffer.println("Refueling: Ship $shipId refueled at harbor $tileId.")
    }

    /**
     * Logs the unloading of a ship
     */
    fun unload(shipId: Int, amt: Int, garbageType: String, tileId: Int) {
        outputBuffer.println("Unload: Ship $shipId unloaded $amt of garbage $garbageType at harbor $tileId.")
    }

    /**
     * Logs the end of the corporation phase for a corporation.
     */
    fun corporationActionFinished(corpId: Int) {
        outputBuffer.println("Corporation Action: Corporation $corpId finished its actions.")
    }

    /**
     * Logs the drift of a garbage from tile to tile.
     */
    fun currentDriftGarbage(garbageType: String, garbageId: Int, amt: Int, startTileId: Int, endTileId: Int) {
        outputBuffer.println(
            "32 Current Drift: $garbageType $garbageId " +
                "with amount $amt drifted from tile $startTileId to tile $endTileId."
        )
    }

    /**
     * Logs the drift of a ship from tile to tile.
     */
    fun currentShipDrift(shipId: Int, startTileId: Int, endTileId: Int) {
        outputBuffer.println("Current Drift: Ship $shipId drifted from tile $startTileId to tile $endTileId.")
    }

    /**
     * Logs the happening of an event.
     */
    fun event(eventId: Int, eventType: String) {
        outputBuffer.println("Event: Event $eventId of type $eventType happened.")
    }

    /**
     * Logs the attachment of a tracker to a garbage.
     */
    fun attachTracker(corpId: Int, garbageId: Int, shipId: Int) {
        outputBuffer.println(
            "Corporation Action: Corporation $corpId attached tracker to garbage $garbageId with ship $shipId."
        )
    }

    /**
     * Logs the assignment of a task to a ship.
     */
    fun assignTask(taskId: Int, type: TaskType, shipId: Int, tileId: Int) {
        outputBuffer.println("Task: Task $taskId of type $type with ship $shipId is added with destination $tileId.")
    }

    /**
     * Logs the completion of a task by a ship.
     */
    fun grantReward(taskId: Int, shipId: Int, type: RewardType) {
        outputBuffer.println("Reward: Task $taskId: Ship $shipId received reward of type $type.")
    }

    /**
     * Logs the start of the calculation of statistics for the simulation.
     */
    fun simulationInfoStatistics() {
        outputBuffer.println("Simulation Info: Simulation statistics are calculated.")
        simulationStatsCollectedGarbage()
        simulationStatsTotalPlastic()
        simulationStatsTotalOil()
        simulationStatsTotalChemical()
        simulationStatsUncollected()
    }

    /**
     * Logs the amount of garbage collected for a corporation. (ALL DATA STORED LOCALLY HERE)
     */
    private fun simulationStatsCollectedGarbage() {
        val sortedByLowestShipIdMap: TreeMap<Int, Triple<Int, Int, Int>> = TreeMap(corporationCollectedGarbage)
        for ((corpId, garbageInfo) in sortedByLowestShipIdMap) {
            val sum = garbageInfo.first + garbageInfo.second + garbageInfo.third
            outputBuffer.println("Simulation Statistics: Corporation $corpId collected $sum of garbage.")
        }
    }

    /**
     * Provides statistics about the total plastic collected.
     */
    private fun simulationStatsTotalPlastic() {
        var plasticAmount = 0
        for ((_, garbageInfo) in corporationCollectedGarbage) {
            plasticAmount += garbageInfo.first
        }
        outputBuffer.println("Simulation Statistics: Total amount of plastic collected: $plasticAmount.")
    }

    /**
     * Provides statistics about the total oil collected.
     */
    private fun simulationStatsTotalOil() {
        var oilAmount = 0
        for ((_, garbageInfo) in corporationCollectedGarbage) {
            oilAmount += garbageInfo.second
        }
        outputBuffer.println("Simulation Statistics: Total amount of oil collected: $oilAmount.")
    }

    /**
     * Provides statistics about the total chemical collected.
     */
    private fun simulationStatsTotalChemical() {
        var chemicalAmount = 0
        for ((_, garbageInfo) in corporationCollectedGarbage) {
            chemicalAmount += garbageInfo.third
        }
        outputBuffer.println("Simulation Statistics: Total amount of chemicals collected: $chemicalAmount.")
    }

    /**
     * Provides statistics about the uncollected garbage.
     */
    private fun simulationStatsUncollected() {
        outputBuffer.println(
            "Simulation Statistics: " +
                "Total amount of garbage still in the ocean: $corporationTotalUncollectedGarbage."
        )
    }
}
