package de.unisaarland.cs.se.selab.systemtest

import de.unisaarland.cs.se.selab.systemtest.basictests.ExampleSystemTest
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations.CorporationWithNoShips
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations.DoubleAgent
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations.HarborCantBeHere
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations.HarborWithNoCorporation
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations.ThisCorporationIsMessedUPbruh
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidCorporations.WhyDoYouDoThis
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidMap.FloatingHarbarObamna
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidMap.InvalidCurrent
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidMap.NonUniqueTileIDs
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidMap.WrongNeighbors
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidScenario.MultiTaskInvalid
import de.unisaarland.cs.se.selab.systemtest.runner.SystemTestManager
import de.unisaarland.cs.se.selab.systemtest.validConfig.*

/**
 * Class to register for systemTests
 */
object SystemTestRegistration {
    /**
     * Register your tests to run against the reference implementation!
     * This can also be used to debug our system test, or to see if we
     * understood something correctly or not (everything should work
     * the same as their reference implementation)
     */
    fun registerSystemTestsReferenceImpl(manager: SystemTestManager) {
        manager.registerTest(ExampleSystemTest())
        manager.registerTest(OilSpillHappensCorrectly())
        manager.registerTest(RestrictionHappensCorrectly())
        manager.registerTest(PirateAttackDeletesShip())
        manager.registerTest(ScoutContainerFillMoveHarborTest())
        manager.registerTest(GarbageDriftsCorrectly())
        manager.registerTest(WrongNeighbors())
        manager.registerTest(InvalidCurrent())
        manager.registerTest(RewardsGivenAndFunctionCorrectly())
        manager.registerTest(CollectingShipMovesToVisibleGarbage())
        manager.registerTest(ShipHasNoPathToVisibleGarbage())
        manager.registerTest(CoordinatingShipsCoordinateAndGoOutOfRestriction())
        manager.registerTest(CorporationFileInvalid())
        manager.registerTest(GarbageDriftsCorrectly2())
        manager.registerTest(ScenarioFileInvalid())
        manager.registerTest(TaskCouldNotBeAssignedNotEnoughFuel())
        manager.registerTest(TaskGotCompletedAssignRewardToShip())
        manager.registerTest(TaskShouldBeAssignedFullCapacity())
        manager.registerTest(OnlyCorrectNumberOfShipsAssigned())
        registerSystemTestsReferenceImplHelper(manager)
    }
    private fun registerSystemTestsReferenceImplHelper(manager: SystemTestManager) {
        manager.registerTest(NonUniqueTileIDs())
        manager.registerTest(HarborCantBeHere())
        manager.registerTest(StormEventDriftsGarbage())
        manager.registerTest(TaskRestrictionReward())
        manager.registerTest(NoWayToLeaveRestriction())
        manager.registerTest(FloatingHarbarObamna())
        manager.registerTest(CorporationGetsInformedThenCollectingShipGoes())
        manager.registerTest(ShipCannotBeAssignedTask())
        manager.registerTest(TestCoordinateTaskAndRadioReward())
        manager.registerTest(ShipCannotCollectGarbageWithContainer())
        manager.registerTest(TestOilSpillIds())
        manager.registerTest(CannotCollectPlasticHasToWaitForHelp())
        manager.registerTest(TrackerTest())
        manager.registerTest(AssignManyShipsToGarbage())
        manager.registerTest(UnloadingOfCollectedChemicals())
        manager.registerTest(VisibleGarbagePriority())
        manager.registerTest(DawnOfThePlanetOfTheApes())
        manager.registerTest(ICanSeeWhatYouCannot())
        manager.registerTest(GarbageLandsOnShip())
        // FAILED
        manager.registerTest(CurrentsFreakRefueling())
        // failed
        manager.registerTest(ShipCantReachHarborOnSpawn404()) // validation
        manager.registerTest(ThisCorporationIsMessedUPbruh()) // validation
        manager.registerTest(WhyDoYouDoThis()) // validation
        manager.registerTest(DoubleAgent()) // validation
        manager.registerTest(ThisScoutingShipIsFamous())
        manager.registerTest(MultiTaskInvalid())
        manager.registerTest(AtlantisTest1())
        manager.registerTest(AtlantisTest2())
        manager.registerTest(BigEyes())
        manager.registerTest(HarborWithNoCorporation()) // validation
        manager.registerTest(CorporationWithNoShips()) // validation
        manager.registerTest(FreeGiftTest()) // Task
        manager.registerTest(EternalVoyagerTest()) // Default behaviour
        manager.registerTest(FreeGiftTest1())
        manager.registerTest(Atlantis3())
        manager.registerTest(EpicCollabGoneWrong())
        manager.registerTest(Error404V2())
        manager.registerTest(Error404V3())
        manager.registerTest(Error404V4())
        manager.registerTest(FreeCandy())
    }

    /**
     * Register the tests you want to run against the validation mutants here!
     * The test only check validation, so they log messages will only possibly
     * be incorrect during the parsing/validation.
     * Everything after 'Simulation start' works correctly
     */
    fun registerSystemTestsMutantValidation(manager: SystemTestManager) {
        manager.registerTest(ExampleSystemTest())
        manager.registerTest(WrongNeighbors())
        manager.registerTest(InvalidCurrent())
        manager.registerTest(CorporationFileInvalid())
        manager.registerTest(ScenarioFileInvalid())
        manager.registerTest(NonUniqueTileIDs())
        manager.registerTest(HarborCantBeHere())
        manager.registerTest(FloatingHarbarObamna())
    }

    /**
     * The same as above, but the log message only (possibly) become incorrect
     * from the 'Simulation start' log onwards
     */
    fun registerSystemTestsMutantSimulation(manager: SystemTestManager) {
        manager.registerTest(ExampleSystemTest())
        manager.registerTest(OilSpillHappensCorrectly())
        manager.registerTest(RestrictionHappensCorrectly())
        manager.registerTest(PirateAttackDeletesShip())
        manager.registerTest(ScoutContainerFillMoveHarborTest())
        manager.registerTest(GarbageDriftsCorrectly())
        manager.registerTest(RewardsGivenAndFunctionCorrectly())
        manager.registerTest(CollectingShipMovesToVisibleGarbage())
        manager.registerTest(ShipHasNoPathToVisibleGarbage())
        /************************************************************/
        // this test is NOT CORRECT DON'T PUSH THIS IN ANYWAY
        // manager.registerTest(GarbageDriftsCorrectly2())
        // this test is NOT CORRECT DON'T PUSH THIS IN ANYWAY
        /************************************************************/
        manager.registerTest(ShipCannotCollectGarbageWithContainer())
        // validation
        manager.registerTest(WrongNeighbors())
        manager.registerTest(InvalidCurrent())
        manager.registerTest(CorporationFileInvalid())
        manager.registerTest(ScenarioFileInvalid())
        manager.registerTest(NonUniqueTileIDs())
        manager.registerTest(HarborCantBeHere())
        manager.registerTest(FloatingHarbarObamna())
        manager.registerTest(DawnOfThePlanetOfTheApes())
        manager.registerTest(CoordinatingShipsCoordinateAndGoOutOfRestriction())
        manager.registerTest(TaskGotCompletedAssignRewardToShip())
        manager.registerTest(TaskShouldBeAssignedFullCapacity())
        manager.registerTest(StormEventDriftsGarbage())
        manager.registerTest(UnloadingOfCollectedChemicals())
        manager.registerTest(ICanSeeWhatYouCannot())
        manager.registerTest(GarbageLandsOnShip())
        manager.registerTest(ShipCantReachHarborOnSpawn404()) // validation
        manager.registerTest(ThisCorporationIsMessedUPbruh()) // validation
        manager.registerTest(WhyDoYouDoThis()) // validation
        manager.registerTest(DoubleAgent()) // validation
        manager.registerTest(ThisScoutingShipIsFamous())
        manager.registerTest(MultiTaskInvalid())
        manager.registerTest(AtlantisTest1())
        manager.registerTest(AtlantisTest2())
        manager.registerTest(TaskRestrictionReward())
        manager.registerTest(TestCoordinateTaskAndRadioReward())
        manager.registerTest(CannotCollectPlasticHasToWaitForHelp())
    }
}
