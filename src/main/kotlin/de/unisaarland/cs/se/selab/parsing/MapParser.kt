
package de.unisaarland.cs.se.selab.parsing
import de.unisaarland.cs.se.selab.navigation.NavigationManager
/**
 * Parses map data from a file.
 *
 * @property fileName The name of the file to parse.
 * @property navigationManager The navigation manager to use.
 */
class MapParser(
    private val fileName: String,
    private val navigationManager: NavigationManager
) {
    /**
     * Parses map data from a file.
     *
     * @return True if the parsing was successful, false otherwise.
     */
    fun parseMap(): Boolean {
        TODO()
    }
}
