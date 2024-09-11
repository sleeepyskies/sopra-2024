package de.unisaarland.cs.se.selab

import de.unisaarland.cs.se.selab.assets.RewardType
import de.unisaarland.cs.se.selab.assets.TaskType
import java.io.File
import java.io.PrintWriter
import java.util.TreeMap

object Logger {
    // Path to the file
    private var filePath: String = "stdout"

    // Map to store collected garbage by corporations
    private val corporationCollectedGarbage: Map<Int, Triple<Int, Int, Int>> = mutableMapOf()

    /**
     * Logs the initialization of the simulation with the given file name.
     */
    fun initInfo(filePath: String) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Initialization Info: $filePath successfully parsed and validated.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Initialization Info: $filePath successfully parsed and validated.")
            writer.flush()
        }
    }

    /**
     * Logs the invalid initialization of the simulation with the given file name.
     */
    fun initInfoInvalid(filePath: String) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Initialization Info: $filePath is invalid.")
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Initialization Info: $filePath is invalid.")
            writer.flush()
        }
    }

    /**
     * Logs the start of the simulation.
     */
    fun simulationStart() {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Simulation Info: Simulation started.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Simulation Info: Simulation started.")
            writer.flush()
        }
    }

    /**
     * Logs the end of the simulation.
     */
    fun simulationEnd() {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Simulation Info: Simulation ended.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Simulation Info: Simulation ended.")
            writer.flush()
        }
    }

    /**
     * Logs the start of a tick in the simulation.
     */
    fun simTick(tick: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Simulation Info: Tick $tick started.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Simulation Info: Tick $tick started.")
            writer.flush()
        }
    }

    /**
     * Logs the start of the corporation phase for a specific corporation.
     */
    fun corporationActionMove(id: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Corporation Action: Corporation $id is starting to move its ships.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $id is starting to move its ships.")
            writer.flush()
        }
    }

    /**
     * Logs the movement of a ship.
     */
    fun shipMovement(shipId: Int, speed: Int, tileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Ship Movement: Ship $shipId moved with speed $speed to tile $tileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Ship Movement: Ship $shipId moved with speed $speed to tile $tileId.")
            writer.flush()
        }
    }

    /**
     * Logs the start of the garbage collection phase for a corporation.
     */
    fun corporationActionCollectGarbage(corpId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Corporation Action: Corporation $corpId is starting to collect garbage.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $corpId is starting to collect garbage.")
            writer.flush()
        }
    }

    /**
     * Logs the garbage collection of a ship.
     */
    fun garbageCollection(shipId: Int, amt: Int, garbageType: String, garbageId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Garbage Collection: Ship $shipId collected $amt of garbage $garbageType with $garbageId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Garbage Collection: Ship $shipId collected $amt of garbage $garbageType with $garbageId.")
            writer.flush()
        }
    }

    /**
     * Logs the start of the cooperation phase for a corporation.
     */
    fun corporationActionCooperate(corpId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Corporation Action: Corporation $corpId is starting to cooperate with other corporations.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $corpId is starting to cooperate with other corporations.")
            writer.flush()
        }
    }

    /**
     * Logs the cooperation between two corporations.
     */
    fun cooperate(corpId: Int, otherCorpId: Int, shipId: Int, otherShipId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write(
                "Cooperation: Corporation $corpId cooperated with corporation $otherCorpId with ship $shipId to ship $otherShipId."
            )
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write(
                "Cooperation: Corporation $corpId cooperated with corporation $otherCorpId with ship $shipId to ship $otherShipId."
            )
            writer.flush()
        }
    }

    /**
     * Logs the start of the refueling phase for a corporation.
     */
    fun corporationActionRefuel(corpId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Corporation Action: Corporation $corpId is starting to refuel.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $corpId is starting to refuel.")
            writer.flush()
        }
    }

    /**
     * Logs the refueling of a ship.
     */
    fun refuel(shipId: Int, tileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Refueling: Ship $shipId refueled at harbor $tileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Refueling: Ship $shipId refueled at harbor $tileId.")
            writer.flush()
        }
    }

    /**
     * Logs the unloading of a ship
     */
    fun unload(shipId: Int, amt: Int, garbageType: String, tileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Unload: Ship $shipId unloaded $amt of garbage $garbageType at harbor $tileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Unload: Ship $shipId unloaded $amt of garbage $garbageType at harbor $tileId.")
            writer.flush()
        }
    }

    /**
     * Logs the end of the corporation phase for a corporation.
     */
    fun corporationActionFinished(corpId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Corporation Action: Corporation $corpId finished its actions.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $corpId finished its actions.")
            writer.flush()
        }
    }

    /**
     * Logs the drift of a garbage from tile to tile.
     */
    fun currentDriftGarbage(garbageType: String, garbageId: Int, amt: Int, startTileId: Int, endTileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write(
                "32 Current Drift: $garbageType $garbageId with amount $amt drifted from tile $startTileId to tile $endTileId."
            )
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write(
                "32 Current Drift: $garbageType $garbageId with amount $amt drifted from tile $startTileId to tile $endTileId."
            )
            writer.flush()
        }
    }

    /**
     * Logs the drift of a ship from tile to tile.
     */
    fun currentShipDrift(shipId: Int, startTileId: Int, endTileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Current Drift: Ship $shipId drifted from tile $startTileId to tile $endTileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Current Drift: Ship $shipId drifted from tile $startTileId to tile $endTileId.")
            writer.flush()
        }
    }

    /**
     * Logs the happening of an event.
     */
    fun event(eventId: Int, eventType: String) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Event: Event $eventId of type $eventType happened.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Event: Event $eventId of type $eventType happened.")
            writer.flush()
        }
    }

    /**
     * Logs the attachment of a tracker to a garbage.
     */
    fun attachTracker(corpId: Int, garbageId: Int, shipId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write(
                "Corporation Action: Corporation $corpId attached tracker to garbage $garbageId with ship $shipId."
            )
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write(
                "Corporation Action: Corporation $corpId attached tracker to garbage $garbageId with ship $shipId."
            )
            writer.flush()
        }
    }

    /**
     * Logs the assignment of a task to a ship.
     */
    fun assignTask(taskId: Int, type: TaskType, shipId: Int, tileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Task: Task $taskId of type $type with ship $shipId is added with destination $tileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Task: Task $taskId of type $type with ship $shipId is added with destination $tileId.")
            writer.flush()
        }
    }

    /**
     * Logs the completion of a task by a ship.
     */
    fun grantReward(taskId: Int, shipId: Int, type: RewardType) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Reward: Task $taskId: Ship $shipId received reward of type $type.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Reward: Task $taskId: Ship $shipId received reward of type $type.")
            writer.flush()
        }
    }

    /**
     * Logs the start of the calculation of statistics for the simulation.
     */
    fun simulationInfoStatistics() {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.write("Simulation Info: Simulation statistics are calculated.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Simulation Info: Simulation statistics are calculated.")
            writer.flush()
        }
    }

    /**
     * Logs the amount of garbage collected for a corporation. (ALL DATA STORED LOCALLY HERE)
     */
    fun simulationStatsCollectedGarbage() {
        val sortedByLowestShipIdMap : TreeMap<Int, Triple<Int, Int, Int>> = TreeMap(corporationCollectedGarbage)
        val writer = if (filePath == "stdout") {
            PrintWriter(System.out)
        } else {
            PrintWriter(File(filePath))
        }
        for ((corpId, garbageInfo) in sortedByLowestShipIdMap) {
            val sum = garbageInfo.first + garbageInfo.second + garbageInfo.third
            writer.write("Simulation Statistics: Corporation $corpId collected $sum of garbage.")
            writer.flush()
        }
    }

    /**
     * Provides statistics about the total plastic collected.
     */
    fun simulationStatsTotalPlastic() {
        val writer = if (filePath == "stdout") {
            PrintWriter(System.out)
        } else {
            PrintWriter(File(filePath))
        }
        var plasticAmount = 0
        for ((_, garbageInfo) in corporationCollectedGarbage) {
            plasticAmount += garbageInfo.first
        }
        writer.write("Simulation Statistics: Total amount of plastic collected: $plasticAmount.")
        writer.flush()
    }

    /**
     * Provides statistics about the total oil collected.
     */
    fun simulationStatsTotalOil() {
        val writer = if (filePath == "stdout") {
            PrintWriter(System.out)
        } else {
            PrintWriter(File(filePath))
        }
        var oilAmount = 0
        for ((_, garbageInfo) in corporationCollectedGarbage) {
            oilAmount += garbageInfo.second
        }
        writer.write("Simulation Statistics: Total amount of oil collected: $oilAmount.")
        writer.flush()
    }

    /**
     * Provides statistics about the total chemical collected.
     */
    fun simulationStatsTotalChemical() {
        val writer = if (filePath == "stdout") {
            PrintWriter(System.out)
        } else {
            PrintWriter(File(filePath))
        }
        var chemicalAmount = 0
        for ((_, garbageInfo) in corporationCollectedGarbage) {
            chemicalAmount += garbageInfo.third
        }
        writer.write("Simulation Statistics: Total amount of chemicals collected: $chemicalAmount.")
        writer.flush()
    }

    /**
     * Provides statistics about the uncollected garbage.
     */
    fun simulationStatsUncollected(amt: Int) {
        val writer = if (filePath == "stdout") {
            PrintWriter(System.out)
        } else {
            PrintWriter(File(filePath))
        }
        writer.write("Simulation Statistics: Total amount of garbage still in the ocean: $amt.")
        writer.flush()
    }

    fun setOutput(it: PrintWriter) {
        TODO()
    }
}
