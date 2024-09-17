package de.unisaarland.cs.se.selab

import de.unisaarland.cs.se.selab.parsing.SimulationParser
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import java.io.FileOutputStream
import java.io.PrintWriter

/**
 * Entry point of the simulation.
 */
fun main(args: Array<String>) {
    val argsParser = ArgParser("Save the Ocean!")
    val map by argsParser.option(ArgType.String, "map").required()
    val corporations by argsParser.option(ArgType.String, "corporations").required()
    val scenario by argsParser.option(ArgType.String, "scenario").required()
    val maxTick by argsParser.option(ArgType.Int, "max_ticks").required()
    val output by argsParser.option(ArgType.String, "out")

    argsParser.parse(args)

    // handle case when no output file is provided.
    val outputStream = if (output == null) {
        System.out
    } else {
        // if output not null, call FileOutputStream(output)
        output?.let { FileOutputStream(it) }
    }
    // init logger with output
    outputStream?.let { PrintWriter(it, true) }?.let { Logger.setOutput(it) }

    // Create SimParser
    val simParser = SimulationParser(map, corporations, scenario, maxTick)

    // Parse Files, create simulator and run
    simParser.createSimulator()?.run()
}
