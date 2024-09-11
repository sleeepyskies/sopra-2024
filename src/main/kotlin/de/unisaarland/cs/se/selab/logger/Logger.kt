package de.unisaarland.cs.se.selab.logger

import de.unisaarland.cs.se.selab.assets.RewardType
import de.unisaarland.cs.se.selab.assets.TaskType
import java.io.File
import java.io.PrintWriter

object Logger {
    private lateinit var writer: PrintWriter
    // Path to the file
    private var filePath: String = "stdout"

    // Map to store collected garbage by corporations
    private val corporationCollectedGarbage: Map<Int, Triple<Int, Int, Int>> = mutableMapOf()

    /**
     * Sets the output for the logger. Either to a provided file path or stdout.
     */
    fun setOutput(writer: PrintWriter) {
        // still need to make all methods use this.writer !!s
        this.writer = writer
    }

    /**
     * Initializes the simulation with the given file name.
     */
    fun initInfo(filePath: String) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Initialization Info: $filePath successfully parsed and validated.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Initialization Info: $filePath successfully parsed and validated.")
            writer.flush()
        }
    }

    fun initInfoInvalid(filePath: String) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Initialization Info: $filePath is invalid.")
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Initialization Info: $filePath is invalid.")
            writer.flush()
        }
    }

    /**
     * Starts the simulation.
     */
    fun simulationStart() {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Simulation Info: Simulation started.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Simulation Info: Simulation started.")
            writer.flush()
        }
    }

    fun simulationEnd(){
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Simulation Info: Simulation ended.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Simulation Info: Simulation ended.")
            writer.flush()
        }
    }

    /**
     * Simulates a tick in the simulation.
     */
    fun simTick(tick: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Simulation Info: Tick $tick started.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Simulation Info: Tick $tick started.")
            writer.flush()
        }
    }

    /**
     * Handles the move action for a corporation.
     */
    fun corporationActionMove(id: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Corporation Action: Corporation $id is starting to move its ships.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $id is starting to move its ships.")
            writer.flush()
        }
    }

    /**
     * Handles the movement of a ship.
     */
    fun shipMovement(shipId: Int, speed: Int, tileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Ship Movement: Ship $shipID moved with speed $speed to tile $tileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Ship Movement: Ship $shipID moved with speed $speed to tile $tileId.")
            writer.flush()
        }
    }

    /**
     * Handles the garbage collection action for a corporation.
     */
    fun corporationActionCollectGarbage(corpId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Corporation Action: Corporation $corporationId is starting to collect garbage.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $corporationId is starting to collect garbage.")
            writer.flush()
        }
    }

    /**
     * Handles the garbage collection by a ship.
     */
    fun garbageCollection(shipId: Int, amt: Int, garbageType: String, garbageId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Garbage Collection: Ship $shipId collected $amt of garbage $garbageType with $garbageId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Garbage Collection: Ship $shipId collected $amt of garbage $garbageType with $garbageId.")
            writer.flush()
        }
    }

    /**
     * Handles the cooperation action for a corporation.
     */
    fun corporationActionCooperate(corpId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Corporation Action: Corporation $corpId is starting to cooperate with other corporations.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $corpId is starting to cooperate with other corporations.")
            writer.flush()
        }
    }

    /**
     * Handles the cooperation between corporations.
     */
    fun cooperate(corpId: Int, otherCorpId: Int, shipId: Int, otherShipId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Cooperation: Corporation $corpId cooperated with corporation $otherCorpId with ship $shipId to ship $otherShipId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Cooperation: Corporation $corpId cooperated with corporation $otherCorpId with ship $shipId to ship $otherShipId.")
            writer.flush()
        }
    }

    /**
     * Handles the refuel action for a corporation.
     */
    fun corporationActionRefuel(corpId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Corporation Action: Corporation $corporationId is starting to refuel.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $corporationId is starting to refuel.")
            writer.flush()
        }
    }

    /**
     * Handles the refueling of a ship.
     */
    fun refuel(shipId: Int, tileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Refueling: Ship $shipID refueled at harbor $tileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Refueling: Ship $shipID refueled at harbor $tileId.")
            writer.flush()
        }
    }

    /**
     * Handles the unloading of garbage by a ship.
     */
    fun unload(shipId: Int, amt: Int, garbageType: String, tileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Unload: Ship $shipId unloaded $amt of garbage $garbageType at harbor $tileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Unload: Ship $shipId unloaded $amt of garbage $garbageType at harbor $tileId.")
            writer.flush()
        }
    }

    /**
     * Marks the action of a corporation as finished.
     */
    fun corporationActionFinished(corpId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Corporation Action: Corporation $corpId finished its actions.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $corpId finished its actions.")
            writer.flush()
        }
    }

    /**
     * Handles the current drift of garbage.
     */
    fun currentDriftGarbage(garbageType: String, garbageId: Int, amt: Int, startTileId: Int, endTileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("32 Current Drift: $garbageType $garbageId with amount $amt drifted from tile $startTileId to tile $endTileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("32 Current Drift: $garbageType $garbageId with amount $amt drifted from tile $startTileId to tile $endTileId.")
            writer.flush()
        }
    }

    /**
     * Handles the current drift of a ship.
     */
    fun currentShipDrift(shipId: Int, startTileId: Int, endTileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Current Drift: Ship $shipId drifted from tile $startTileId to tile $endTileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Current Drift: Ship $shipId drifted from tile $startTileId to tile $endTileId.")
            writer.flush()
        }
    }

    /**
     * Handles an event in the simulation.
     */
    fun event(eventId: Int, eventType: String) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Event: Event $eventId of type $eventType happened.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Event: Event $eventId of type $eventType happened.")
            writer.flush()
        }
    }

    /**
     * Attaches a tracker to a ship for a corporation.
     */
    fun attachTracker(corpId: Int, garbageId: Int, shipId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Corporation Action: Corporation $corpId attached tracker to garbage $garbageId with ship $shipId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Corporation Action: Corporation $corpId attached tracker to garbage $garbageId with ship $shipId.")
            writer.flush()
        }
    }

    /**
     * Assigns a task to a ship.
     */
    fun assignTask(taskId: Int, type: TaskType, shipId: Int, tileId: Int) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Task: Task $taskId of type $type with ship $shipId is added with destination $tileId.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Task: Task $taskId of type $type with ship $shipId is added with destination $tileId.")
            writer.flush()
        }
    }

    /**
     * Grants a reward to a ship for completing a task.
     */
    fun grantReward(taskId: Int, shipId: Int, type: RewardType) {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Reward: Task $taskId: Ship $shipId received reward of type $type.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Reward: Task $taskId: Ship $shipId received reward of type $type.")
            writer.flush()
        }
    }

    /**
     * Provides statistics about the simulation.
     */
    fun simulationInfoStatistics() {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Simulation Info: Simulation statistics are calculated.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Simulation Info: Simulation statistics are calculated.")
            writer.flush()
        }
    }

    /**
     * Provides statistics about the collected garbage.
     */
    fun simulationStatsCollectedGarbage() {
        if (filePath == "stdout") {
            val writer = PrintWriter()
            writer.write("Simulation Info: Simulation statistics are calculated.")
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.write("Simulation Info: Simulation statistics are calculated.")
            writer.flush()
        }
    }

    /**
     * Provides statistics about the total plastic collected.
     */
    fun simulationStatsTotalPlastic() {
        // Implementation here
    }

    /**
     * Provides statistics about the total oil collected.
     */
    fun simulationStatsTotalOil() {
        // Implementation here
    }

    /**
     * Provides statistics about the total chemical collected.
     */
    fun simulationStatsTotalChemical() {
        // Implementation here
    }

    /**
     * Provides statistics about the uncollected garbage.
     */
    fun simulationStatsUncollected(amt: Int) {
        // Implementation here
    }


}