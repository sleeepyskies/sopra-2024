package de.unisaarland.cs.se.selab.systemtest.utils

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest
import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError

/**
 * adds skips to basic systemtest class
 */
abstract class ExampleSystemTestExtension : SystemTest() {

    /**
     * skips until the given [startString] is found
     */
    private suspend fun skipUntilString(startString: String): String {
        val line: String = getNextLine()
            ?: throw SystemTestAssertionError("End of log reached when there should be more.")
        return if (line.startsWith(startString)) {
            line
        } else {
            skipUntilString(startString)
        }
    }

    /**
     * skips until the given [logs] is found
     */
    suspend fun skipUntilLogType(logs: Logs): String {
        return skipUntilString(logs.toString())
    }
}

/**
 * the categories of logs
 */
enum class Logs(private val message: String) {
    INITIALIZATION_INFO("Initialization Info"),
    SIMULATION_INFO("Simulation Info"),
    SIMULATION_STATISTICS("Simulation Statistics");

    override fun toString(): String {
        return message
    }
}
