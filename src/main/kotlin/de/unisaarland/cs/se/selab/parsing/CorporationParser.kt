package de.unisaarland.cs.se.selab.parsing

import de.unisaarland.cs.se.selab.assets.Corporation
import de.unisaarland.cs.se.selab.assets.Ship
/**
 * Parses corporation data from a file.
 *
 * @property fileName The name of the file to parse.
 * @property corporations The list of corporations to update.
 * @property ships The list of ships to update.
 */
class CorporationParser(
    private val fileName: String,
    corporations: List<Corporation>,
    ships: List<Ship>
) {
    /**
     * Parses corporation data from a file.
     *
     * @return True if the parsing was successful, false otherwise.
     */
    fun parseMap(): Boolean {
        TODO()
    }
}