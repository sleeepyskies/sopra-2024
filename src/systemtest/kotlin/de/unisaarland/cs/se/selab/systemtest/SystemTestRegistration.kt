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
import de.unisaarland.cs.se.selab.systemtest.invalidConfig.invalidScenario.ThisShipAintHere
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
        manager.registerTest(CyclopsCorp())
        manager.registerTest(Atlantis4())
        manager.registerTest(Atlantis5())
        manager.registerTest(Atlantis6())
        manager.registerTest(Atlantis7())
        manager.registerTest(CorpWithInvalidGarbageCollectingShips404())
        manager.registerTest(CorpWithNoShips404())
        manager.registerTest(MultiReward())
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
        manager.registerTest(TripleCorporationRadio())
        manager.registerTest(ThisShipAintHere())
        manager.registerTest(WhySoMuchSpace())
        manager.registerTest(UhOhVoyagerrr())
        manager.registerTest(ChiefKeef())
        manager.registerTest(SilentHouseChemicalsOnDeepOcean())
        manager.registerTest(SilentHouseCoordinateNotOnHarbor())
        manager.registerTest(SilentHouseEventsOnLand())
        manager.registerTest(SilentHouseSameRewardsFor2Tasks())
        manager.registerTest(SilentHouseShipSpawsOnLand())
        manager.registerTest(SilentHouseTasksOnLand())
        manager.registerTest(SilentHouseTileHoldsMoreThanThOil())
        manager.registerTest(SilentHouseTooMuchOilOnTile2())
        manager.registerTest(CurrentCollect())
        manager.registerTest(CurrentCollect1())
    }

    /**
     * Register the tests you want to run against the validation mutants here!
     * The test only check validation, so they log messages will only possibly
     * be incorrect during the parsing/validation.
     * Everything after 'Simulation start' works correctly
     */
    fun registerSystemTestsMutantValidation(manager: SystemTestManager) {
        manager.registerTest(WrongNeighbors())
        manager.registerTest(InvalidCurrent())
        manager.registerTest(CorporationFileInvalid())
        manager.registerTest(ScenarioFileInvalid())
        manager.registerTest(NonUniqueTileIDs())
        manager.registerTest(HarborCantBeHere())
        manager.registerTest(FloatingHarbarObamna())
        manager.registerTest(HarborWithNoCorporation()) // validation
        manager.registerTest(CorporationWithNoShips()) // validation
        manager.registerTest(Atlantis3())
        manager.registerTest(Error404V2())
        manager.registerTest(Error404V3())
        manager.registerTest(Error404V4())
        manager.registerTest(FreeCandy())
        manager.registerTest(ThisShipAintHere())
        manager.registerTest(Atlantis7())
        manager.registerTest(CorpWithNoShips404())
        manager.registerTest(CorpWithInvalidGarbageCollectingShips404())
        manager.registerTest(SilentHouseChemicalsOnDeepOcean())
        manager.registerTest(SilentHouseCoordinateNotOnHarbor())
        manager.registerTest(SilentHouseSameRewardsFor2Tasks())
        manager.registerTest(SilentHouseShipSpawsOnLand())
        manager.registerTest(SilentHouseTasksOnLand())
        manager.registerTest(SilentHouseTileHoldsMoreThanThOil())
        manager.registerTest(SilentHouseTooMuchOilOnTile2())
    }

    private fun runRegisterValidationTests(manager: SystemTestManager) {
        manager.registerTest(WrongNeighbors())
        manager.registerTest(InvalidCurrent())
        manager.registerTest(CorporationFileInvalid())
        manager.registerTest(ScenarioFileInvalid())
        manager.registerTest(NonUniqueTileIDs())
        manager.registerTest(HarborCantBeHere())
        manager.registerTest(FloatingHarbarObamna())
        manager.registerTest(HarborWithNoCorporation()) // validation
        manager.registerTest(CorporationWithNoShips()) // validation
        manager.registerTest(Atlantis3())
        manager.registerTest(Error404V2())
        manager.registerTest(Error404V3())
        manager.registerTest(Error404V4())
        manager.registerTest(FreeCandy())
        manager.registerTest(ThisShipAintHere())
        manager.registerTest(Atlantis7())
        manager.registerTest(CorpWithNoShips404())
        manager.registerTest(CorpWithInvalidGarbageCollectingShips404())
        manager.registerTest(SilentHouseChemicalsOnDeepOcean())
        manager.registerTest(SilentHouseCoordinateNotOnHarbor())
        manager.registerTest(SilentHouseSameRewardsFor2Tasks())
        manager.registerTest(SilentHouseShipSpawsOnLand())
        manager.registerTest(SilentHouseTasksOnLand())
        manager.registerTest(SilentHouseTileHoldsMoreThanThOil())
        manager.registerTest(SilentHouseTooMuchOilOnTile2())
        manager.registerTest(Atlantis4())
        manager.registerTest(Atlantis5())
        manager.registerTest(Atlantis6())
    }

    /**
     * The same as above, but the log message only (possibly) become incorrect
     * from the 'Simulation start' log onwards
     */
    fun registerSystemTestsMutantSimulation(manager: SystemTestManager) {
        runRegisterValidationTests(manager)
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
        manager.registerTest(CorporationFileInvalid())
        manager.registerTest(DawnOfThePlanetOfTheApes())
        manager.registerTest(CoordinatingShipsCoordinateAndGoOutOfRestriction())
        manager.registerTest(TaskGotCompletedAssignRewardToShip())
        // manager.registerTest(TaskShouldBeAssignedFullCapacity())
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
        manager.registerTest(EpicCollabGoneWrong())
        manager.registerTest(TripleCorporationRadio())
        manager.registerTest(WhySoMuchSpace())
        manager.registerTest(EternalVoyagerTest())
        manager.registerTest(OnlyCorrectNumberOfShipsAssigned())
        manager.registerTest(MultiReward())
        manager.registerTest(TestOilSpillIds())
    }
}
