/*
 * Copyright 2020, 2024, 2025 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.bookkeeper.recordpart;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataRedactorTest {
	private DataRedactorImp dataRedactor;

	private DataRecordGroupSpy topDataGroupSpy;
	private Set<Constraint> emptyConstraints;
	private Set<String> emptyPermissions;
	private Set<Constraint> someConstraints;
	private DataRecordGroupSpy originalDataGroup;
	private DataRecordGroupSpy updatedDataGroup;
	private DataGroupRedactorSpy dataGroupRedactorSpy;
	private MethodCallRecorder dataGroupRedactorMCR;
	private MetadataHolderSpy metadataHolder;
	private MethodCallRecorder metadataHolderMCR;

	private String metadataId = "someMetadataId";
	private DataGroupWrapperFactorySpy wrapperFactory;
	private MethodCallRecorder wrapperFactoryMCR;
	private MatcherFactorySpy matcherFactory;
	private MethodCallRecorder matcherFactoryMCR;

	private DataFactorySpy dataFactorySpy;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		metadataHolder = new MetadataHolderSpy();
		metadataHolderMCR = metadataHolder.MCR;
		dataGroupRedactorSpy = new DataGroupRedactorSpy();
		dataGroupRedactorMCR = dataGroupRedactorSpy.MCR;
		wrapperFactory = new DataGroupWrapperFactorySpy();
		wrapperFactoryMCR = wrapperFactory.MCR;
		matcherFactory = new MatcherFactorySpy();
		matcherFactoryMCR = matcherFactory.MCR;
		dataRedactor = new DataRedactorImp(metadataHolder, dataGroupRedactorSpy, wrapperFactory,
				matcherFactory);
		topDataGroupSpy = new DataRecordGroupSpy();

		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		someConstraints = createConstraintForSome();
		originalDataGroup = new DataRecordGroupSpy();
		updatedDataGroup = new DataRecordGroupSpy();
	}

	private Set<Constraint> createConstraintForSome() {
		Set<Constraint> recordPartConstraints = new HashSet<>();
		Constraint constraint = new Constraint("some");
		recordPartConstraints.add(constraint);
		return recordPartConstraints;
	}

	@Test
	public void testInit() {
		assertSame(dataRedactor.onlyForTestGetMetadataHolder(), metadataHolder);
		assertSame(dataRedactor.onlyForTestGetDataGroupRedactor(), dataGroupRedactorSpy);
		assertSame(dataRedactor.onlyForTestGetDataGroupWrapperFactory(), wrapperFactory);
		assertSame(dataRedactor.onlyForTestGetMatcherFactory(), matcherFactory);
	}

	@Test
	public void testRemoveWhenNoConstraints() {
		String metadataId = "someMetadataId";
		DataRecordGroup filteredDataGroup = dataRedactor
				.removeChildrenForConstraintsWithoutPermissions(metadataId, topDataGroupSpy,
						emptyConstraints, emptyPermissions);
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		assertSame(filteredDataGroup, topDataGroupSpy);
	}

	@Test
	public void testLoopTwoGroupChildren() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);

		DataRecordGroup answer = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, someConstraints, emptyPermissions);

		var recordGroupFromProvider = dataFactorySpy.MCR
				.getReturnValue("factorRecordGroupFromDataGroup", 0);
		assertEquals(answer, recordGroupFromProvider);

		assertReturnedDataIsFromGroupRedactor();
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertGroupRedactorCalledForFirstChildGroup();
		assertGroupRedactorCalledForSecondChildGroup();
		assertMetadataHasBeenRequestedForAllProcessedGroups();
	}

	private DataGroup getParameterForFactorRecordGroupFromDataGroup() {
		return (DataGroup) dataFactorySpy.MCR.getParameterForMethodAndCallNumberAndParameter(
				"factorRecordGroupFromDataGroup", 0, "dataGroup");
	}

	private DataGroup getValueFactorGroupFromDataRecordGroup() {
		return (DataGroup) dataFactorySpy.MCR.getReturnValue("factorGroupFromDataRecordGroup", 0);
	}

	private void assertReturnedDataIsFromGroupRedactor() {
		DataGroup filteredDataGroup = getParameterForFactorRecordGroupFromDataGroup();
		dataGroupRedactorMCR.assertReturn("removeChildrenForConstraintsWithoutPermissions", 0,
				filteredDataGroup);
	}

	private MetadataGroupSpy createAndAddTopGroup(String metadataId) {
		MetadataGroupSpy topGroup = new MetadataGroupSpy(metadataId, "someNameInData");
		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement", () -> topGroup,
				metadataId);
		return topGroup;
	}

	private void assertGroupRedactorCalledForTopLevelDataGroup() {
		DataGroup topGroupAsDataGroup = getValueFactorGroupFromDataRecordGroup();
		dataGroupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 0,
				topGroupAsDataGroup, someConstraints, emptyPermissions);
	}

	private void assertGroupRedactorCalledForFirstChildGroup() {
		DataGroupWrapperSpy firstChildGroup = getGroupReturnedFromMatcherWithCallNumber(0);
		dataGroupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 1,
				firstChildGroup, someConstraints, emptyPermissions);
	}

	private void assertGroupRedactorCalledForSecondChildGroup() {
		DataGroupWrapperSpy secondChildGroup = getGroupReturnedFromMatcherWithCallNumber(1);
		dataGroupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 2,
				secondChildGroup, someConstraints, emptyPermissions);
	}

	private void assertMetadataHasBeenRequestedForAllProcessedGroups() {
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolderMCR.assertParameters("getMetadataElement", 1, "childDataGroup");
		metadataHolderMCR.assertParameters("getMetadataElement", 2, "childDataGroup");
		metadataHolderMCR.assertParameters("getMetadataElement", 3, "recordInfo");
	}

	private MetadataGroupSpy createAndAddChildToDataGroup(MetadataGroupSpy topGroup,
			String linkedRecordType, String linkedRecordId, int repeatMin, int repeatMax) {
		topGroup.createChildReference(linkedRecordType, linkedRecordId, repeatMin, repeatMax,
				ConstraintType.WRITE);
		MetadataGroupSpy metadataChild = new MetadataGroupSpy(linkedRecordId,
				linkedRecordId + "NameInData");

		metadataHolder.MRV.setSpecificReturnValuesSupplier("getMetadataElement",
				() -> metadataChild, linkedRecordId);
		return metadataChild;
	}

	private DataGroupWrapperSpy getGroupReturnedFromMatcherWithCallNumber(int firstGroupNumber) {
		MatcherSpy matcherSpy = matcherFactory.returnedMatchers.get(firstGroupNumber);
		return matcherSpy.returnedDataGroup;
	}

	@Test
	public void testOneGroupChildNoMatchingData() {
		matcherFactory.hasMatchingChild = false;
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "type", 0, 1);

		dataRedactor.removeChildrenForConstraintsWithoutPermissions(metadataId, topDataGroupSpy,
				someConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor();
		assertGroupRedactorCalledForTopLevelDataGroup();
		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);

		assertMetadataHasBeenRequestedForTwoGroups();
	}

	private void assertMetadataHasBeenRequestedForTwoGroups() {
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolderMCR.assertParameters("getMetadataElement", 1, "type");
	}

	@Test
	public void testOneChildNotGroup() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataTextVariable", "id", 0, 1);

		dataRedactor.removeChildrenForConstraintsWithoutPermissions(metadataId, topDataGroupSpy,
				someConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor();
		assertGroupRedactorCalledForTopLevelDataGroup();
		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 2);

		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 3);
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
	}

	@Test
	public void testOneChildGroupRepeatable() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 3);

		dataRedactor.removeChildrenForConstraintsWithoutPermissions(metadataId, topDataGroupSpy,
				someConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor();
		assertGroupRedactorCalledForTopLevelDataGroup();
		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);

		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 2);
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
	}

	@Test
	public void testOneGroupChildWithGrandChild() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		MetadataGroupSpy recordInfo = createAndAddChildToDataGroup(topGroup, "metadataGroup",
				"recordInfo", 0, 1);
		createAndAddChildToDataGroup(recordInfo, "metadataGroup", "dataDivider", 0, 1);

		dataRedactor.removeChildrenForConstraintsWithoutPermissions(metadataId, topDataGroupSpy,
				someConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor();
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertGroupRedactorCalledForFirstChildGroup();
		assertGroupRedactorCalledForFirstGrandChildGroup();

		assertMetadataHasBeenRequestedForAllProcessedGroupsForGrandChildTest();
	}

	private void assertGroupRedactorCalledForFirstGrandChildGroup() {
		DataGroupWrapperSpy grandChildGroup = getGroupReturnedFromMatcherWithCallNumber(1);

		dataGroupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 2,
				grandChildGroup, someConstraints, emptyPermissions);
	}

	private void assertMetadataHasBeenRequestedForAllProcessedGroupsForGrandChildTest() {
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolderMCR.assertParameters("getMetadataElement", 1, "recordInfo");
		metadataHolderMCR.assertParameters("getMetadataElement", 3, "dataDivider");
	}

	@Test
	public void testRemoveChildrenWithNoMatch() {
		matcherFactory.hasMatchingChild = false;

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		dataRedactor.removeChildrenForConstraintsWithoutPermissions(metadataId, updatedDataGroup,
				someConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 1);

		DataGroupWrapperSpy returnedRedactedGroup = (DataGroupWrapperSpy) dataGroupRedactorMCR
				.getReturnValue("removeChildrenForConstraintsWithoutPermissions", 0);

		MetadataGroup returnedMetadataChild = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", 1);

		assertEquals(matcherFactory.dataGroups.get(0), returnedRedactedGroup);
		assertEquals(matcherFactory.metadataGroups.get(0), returnedMetadataChild);
		MatcherSpy returnedMatcherForUpdated = matcherFactory.returnedMatchers.get(0);
		assertTrue(returnedMatcherForUpdated.hasMatchingChildWasCalled);
		assertFalse(returnedMatcherForUpdated.getMatchingChildWasCalled);

		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
	}

	@Test
	public void testReplaceWhenNoConstraints() {
		DataRecordGroup replacedDataGroup = dataRedactor
				.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
						updatedDataGroup, emptyConstraints, emptyPermissions);
		assertSame(replacedDataGroup, originalDataGroup);
		metadataHolderMCR.assertMethodNotCalled("getMetadataElement");
		dataGroupRedactorMCR
				.assertMethodNotCalled("replaceChildrenForConstraintsWithoutPermissions");
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
	}

	@Test
	public void testReplaceTopLevelGroupWithoutChildren() {
		createAndAddTopGroup(metadataId);
		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		topGroupIsWrappedAndDataGroupRedactorReplaceCalledForTopLevel();

		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 1);
		wrapperFactoryMCR.assertNumberOfCallsToMethod("factor", 1);
	}

	private void topGroupIsWrappedAndDataGroupRedactorReplaceCalledForTopLevel() {
		DataGroupWrapperSpy wTopDataGroupSpy = (DataGroupWrapperSpy) wrapperFactoryMCR
				.getReturnValue("factor", 0);

		var originalDataGroupAsDataGroup = dataFactorySpy.MCR
				.getReturnValue("factorGroupFromDataRecordGroup", 0);
		var updatedDataGroupAsDataGroup = dataFactorySpy.MCR
				.getReturnValue("factorGroupFromDataRecordGroup", 1);

		DataGroup wrappedDataGroup = (DataGroup) wrapperFactoryMCR
				.getParameterForMethodAndCallNumberAndParameter("factor", 0, "dataGroup");
		assertSame(wrappedDataGroup, updatedDataGroupAsDataGroup);

		dataGroupRedactorMCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions", 0,
				originalDataGroupAsDataGroup, wTopDataGroupSpy, someConstraints, emptyPermissions);
	}

	@Test
	public void testReplaceOneChildNOTGroup() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataTextVar", "type", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		topGroupIsWrappedAndDataGroupRedactorReplaceCalledForTopLevel();

		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 3);
		wrapperFactoryMCR.assertNumberOfCallsToMethod("factor", 2);
	}

	@Test
	public void testReplaceOneChildGroupRepeatMaxNOTOne() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "type", 0, 2);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		topGroupIsWrappedAndDataGroupRedactorReplaceCalledForTopLevel();

		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 2);
		wrapperFactoryMCR.assertNumberOfCallsToMethod("factor", 1);
	}

	@Test
	public void testReplaceOneChildGroupNoDataAfterTopReplace() {
		matcherFactory.hasMatchingChildList.add(false);

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "type", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		assertCorrectCallsMadeForFirstChildOnFirstLevel();
		assertFirstChildHasNoDataAfterTopLevel();
		assertNoCallsMadeBeyondFirstChildOnFirstLevel();
	}

	private void assertFirstChildHasNoDataAfterTopLevel() {
		MatcherSpy matcherChild1 = (MatcherSpy) matcherFactoryMCR.getReturnValue("factor", 0);
		matcherChild1.MCR.assertMethodWasCalled("groupHasMatchingDataChild");
		matcherChild1.MCR.assertMethodNotCalled("getMatchingDataChild");
	}

	private void assertCorrectCallsMadeForFirstChildOnFirstLevel() {
		topGroupIsWrappedAndDataGroupRedactorReplaceCalledForTopLevel();

		MetadataGroup returnedMetadataForTopGroup = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", 0);
		MetadataChildReference metadataChildReference1 = returnedMetadataForTopGroup
				.getChildReferences().get(0);
		String childMetadataId1 = metadataChildReference1.getLinkedRecordId();

		metadataHolderMCR.assertParameters("getMetadataElement", 1, childMetadataId1);
		MetadataGroup returnedMetadataChild1 = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", 1);

		DataGroupWrapperSpy wTopDataGroupSpy = (DataGroupWrapperSpy) dataGroupRedactorMCR
				.getReturnValue("replaceChildrenForConstraintsWithoutPermissions", 0);
		matcherFactoryMCR.assertParameters("factor", 0, wTopDataGroupSpy, returnedMetadataChild1);
	}

	private void assertNoCallsMadeBeyondFirstChildOnFirstLevel() {
		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 3);
		wrapperFactoryMCR.assertNumberOfCallsToMethod("factor", 1);
		matcherFactoryMCR.assertNumberOfCallsToMethod("factor", 1);
	}

	@Test
	public void testReplaceOneChildGroupReplacedDataAfterTopReplace() {
		matcherFactory.hasMatchingChildList.add(true);
		dataGroupRedactorSpy.removeHasBeenCalledList.add(true);

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "type", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		assertCorrectCallsMadeForFirstChildOnFirstLevel();
		assertCheckIfFirstChildIsReplaced();
		assertNoCallsMadeBeyondFirstChildOnFirstLevel();
	}

	private void assertCheckIfFirstChildIsReplaced() {
		MatcherSpy matcherChild1 = (MatcherSpy) matcherFactoryMCR.getReturnValue("factor", 0);
		matcherChild1.MCR.assertMethodWasCalled("groupHasMatchingDataChild");

		DataGroup childData1 = (DataGroup) matcherChild1.MCR.getReturnValue("getMatchingDataChild",
				0);
		DataGroupSpy wTopDataGroup = (DataGroupSpy) dataGroupRedactorMCR
				.getReturnValue("replaceChildrenForConstraintsWithoutPermissions", 0);
		wTopDataGroup.MCR.assertParameters("hasRemovedBeenCalled", 0, childData1);
	}

	@Test
	public void testReplaceOneChildGroupDataNotReplacedAfterTopReplaceWithDataInOriginal() {
		setupDataExistsInUpdateIsNotChangedByGroupRedactorExistsInOriginalShouldBeReplacedOnSecondLevel();

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		assertCorrectCallsMadeForFirstChildOnFirstLevel();
		assertCheckIfFirstChildIsReplaced();

		assertCorrectCallsMadeForFirstGrandChildOnReplace();

		assertMetadataFetchedForFirstGrandChild();

		assertNoCallsMadeBeyondFirstChildOnSecondLevelForReplace();
	}

	private void setupDataExistsInUpdateIsNotChangedByGroupRedactorExistsInOriginalShouldBeReplacedOnSecondLevel() {
		// topLevel, updated data, remove is called (replace done)
		dataGroupRedactorSpy.removeHasBeenCalledList.add(false);
		// first child, updated data, has data for metadataChild1
		matcherFactory.hasMatchingChildList.add(true);
		// first child, original data, has data for metadataChild1
		matcherFactory.hasMatchingChildList.add(true);
		// recurse 1
		// first child, updated data, remove is called (replace done)
		dataGroupRedactorSpy.removeHasBeenCalledList.add(false);

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "type", 0, 1);
	}

	private void assertMetadataFetchedForFirstGrandChild() {
		MetadataGroup returnedMetadataForFirstChildGroup = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", 0);
		MetadataChildReference metadataGrandChildReference1 = returnedMetadataForFirstChildGroup
				.getChildReferences().get(0);
		String grandChildMetadataId1 = metadataGrandChildReference1.getLinkedRecordId();
		metadataHolderMCR.assertParameters("getMetadataElement", 1, grandChildMetadataId1);
	}

	private void assertCorrectCallsMadeForFirstGrandChildOnReplace() {
		MetadataGroup returnedMetadataChild1 = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", 1);

		var originalDataGroupAsDataGroup = dataFactorySpy.MCR
				.getReturnValue("factorGroupFromDataRecordGroup", 0);

		matcherFactoryMCR.assertParameters("factor", 1, originalDataGroupAsDataGroup,
				returnedMetadataChild1);
		MatcherSpy originalMatcher = (MatcherSpy) matcherFactoryMCR.getReturnValue("factor", 1);
		originalMatcher.MCR.assertMethodWasCalled("groupHasMatchingDataChild");
		originalMatcher.MCR.assertMethodWasCalled("getMatchingDataChild");
		DataGroup childOriginal1 = (DataGroup) originalMatcher.MCR
				.getReturnValue("getMatchingDataChild", 0);

		MatcherSpy matcherChild1 = (MatcherSpy) matcherFactoryMCR.getReturnValue("factor", 0);
		DataGroup childData1 = (DataGroup) matcherChild1.MCR.getReturnValue("getMatchingDataChild",
				0);

		DataGroupWrapperSpy wTopDataGroupSpy2 = (DataGroupWrapperSpy) wrapperFactoryMCR
				.getReturnValue("factor", 1);
		DataGroup wrappedDataGroup = (DataGroup) wrapperFactoryMCR
				.getParameterForMethodAndCallNumberAndParameter("factor", 1, "dataGroup");
		assertSame(wrappedDataGroup, childData1);
		dataGroupRedactorMCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions", 1,
				childOriginal1, wTopDataGroupSpy2, someConstraints, emptyPermissions);
	}

	private void assertNoCallsMadeBeyondFirstChildOnSecondLevelForReplace() {
		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 3);
		matcherFactoryMCR.assertNumberOfCallsToMethod("factor", 2);
		wrapperFactoryMCR.assertNumberOfCallsToMethod("factor", 2);
	}

	@Test
	public void testReplaceOneChildGroupDataNotReplacedAfterTopReplaceWithoutDataInOriginal() {
		setupDataExistsInUpdateIsNotChangedByGroupRedactorDoesNotExistsInOriginalShouldBeRemovedOnSecondLevel();

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		assertCorrectCallsMadeForFirstChildOnFirstLevel();
		assertCheckIfFirstChildIsReplaced();

		assertCorrectCallsMadeForFirstGrandChildOnRemove();

		assertMetadataFetchedForFirstGrandChild();

		assertNoCallsMadeBeyondFirstChildOnSecondLevelForRemove();
	}

	private void setupDataExistsInUpdateIsNotChangedByGroupRedactorDoesNotExistsInOriginalShouldBeRemovedOnSecondLevel() {
		// topLevel, updated data, remove is called (replace done)
		dataGroupRedactorSpy.removeHasBeenCalledList.add(false);
		// first child, updated data, has data for metadataChild1
		matcherFactory.hasMatchingChildList.add(true);
		// first child, original data, has data for metadataChild1
		matcherFactory.hasMatchingChildList.add(false);

		// recurse 1
		// first child, updated data, remove is called (replace done)
		dataGroupRedactorSpy.removeHasBeenCalledList.add(false);

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "type", 0, 1);
	}

	private void assertCorrectCallsMadeForFirstGrandChildOnRemove() {
		MetadataGroup returnedMetadataChild1 = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", 1);

		var originalDataGroupAsDataGroup = dataFactorySpy.MCR
				.getReturnValue("factorGroupFromDataRecordGroup", 0);

		matcherFactoryMCR.assertParameters("factor", 1, originalDataGroupAsDataGroup,
				returnedMetadataChild1);
		MatcherSpy originalMatcher = (MatcherSpy) matcherFactoryMCR.getReturnValue("factor", 1);
		originalMatcher.MCR.assertMethodWasCalled("groupHasMatchingDataChild");
		originalMatcher.MCR.assertMethodNotCalled("getMatchingDataChild");

		MatcherSpy matcherChild1 = (MatcherSpy) matcherFactoryMCR.getReturnValue("factor", 0);
		DataGroup childData1 = (DataGroup) matcherChild1.MCR.getReturnValue("getMatchingDataChild",
				0);
		dataGroupRedactorMCR
				.assertMethodWasCalled("removeChildrenForConstraintsWithoutPermissions");
		dataGroupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 0,
				childData1, someConstraints, emptyPermissions);
	}

	private void assertNoCallsMadeBeyondFirstChildOnSecondLevelForRemove() {
		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 3);
		matcherFactoryMCR.assertNumberOfCallsToMethod("factor", 2);
		wrapperFactoryMCR.assertNumberOfCallsToMethod("factor", 1);
	}

	@Test
	public void testMoreThanOneChildOnTopLevelAllAreHandled() {
		setupDataForTwoChildrenOnTopLevel();

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 3);
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 5);
		matcherFactoryMCR.assertNumberOfCallsToMethod("factor", 4);
		wrapperFactoryMCR.assertNumberOfCallsToMethod("factor", 3);
	}

	private void setupDataForTwoChildrenOnTopLevel() {
		// topLevel, updated data, remove is called (replace done)
		dataGroupRedactorSpy.removeHasBeenCalledList.add(false);

		// first child, updated data, has data for metadataChild1
		matcherFactory.hasMatchingChildList.add(true);
		// first child, original data, has data for metadataChild1
		matcherFactory.hasMatchingChildList.add(true);
		// first child, updated data, remove is called (replace done)
		dataGroupRedactorSpy.removeHasBeenCalledList.add(false);

		// second child, updated data, has data for metadataChild1
		matcherFactory.hasMatchingChildList.add(true);
		// second child, original data, has data for metadataChild1
		matcherFactory.hasMatchingChildList.add(true);
		// second child, updated data, remove is called (replace done)
		dataGroupRedactorSpy.removeHasBeenCalledList.add(false);

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "oneGroup", 0, 1);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "otherGroup", 0, 1);
	}

}
