package de.unisaarland.cs.se.selab

import de.unisaarland.cs.se.selab.parsing.SimulationParser
import de.unisaarland.cs.se.selab.logger.Logger
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
    val corporations by argsParser.option(ArgType.String, "assets").required()
    val scenario by argsParser.option(ArgType.String, "scenario").required()
    val maxTick by argsParser.option(ArgType.Int, "ticks").required()
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
    outputStream?.let { PrintWriter(it) }?.let { Logger.setOutput(it) }

    // Create SimParser
    val simParser = SimulationParser(map, corporations, scenario, maxTick)

    // Parse Files and create simulator
    val sim = simParser.createSimulator()

    // run -- add sim.run or whatever
    TODO()
}



