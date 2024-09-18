package de.unisaarland.cs.se.selab.systemtest.validConfig

import de.unisaarland.cs.se.selab.systemtest.api.SystemTestAssertionError
import de.unisaarland.cs.se.selab.systemtest.utils.ExampleSystemTestExtension
import de.unisaarland.cs.se.selab.systemtest.utils.Logs

/**
 * applying pirate attack and checking if ship was removed
 */
class PirateAttackDeletesShip : ExampleSystemTestExtension() {
    override val description = "applying pirate attack"
    override val corporations = "corporationJsons/empty_corps.json"
    override val scenario = "scenarioJsons/justPirateAttack.json"
    override val map = "mapFiles/obamna.json"
    override val name = "PirateAttackDeletesShip"
    override val maxTicks = 2
    override suspend fun run() {
        val expectedString = "Event: Event 1 of type PIRATE_ATTACK happened."
        if (skipUntilLogType(Logs.EVENT) != expectedString) {
            throw SystemTestAssertionError("Pirate attack was not applied")
        }
        skipUntilLogType(Logs.TOTAL_AMOUNT_OF_GARBAGE)
        assertEnd()
    }
}
