/*
 * Copyright 2020 Uppsala University Library
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
import se.uu.ub.cora.bookkeeper.spy.MethodCallRecorder;
import se.uu.ub.cora.data.DataGroup;

public class DataRedactorTest {
	private DataRedactorImp dataRedactor;

	private DataGroupForDataRedactorSpy topDataGroupSpy;
	private Set<Constraint> emptyConstraints;
	private Set<String> emptyPermissions;
	private Set<Constraint> someConstraints;
	private DataGroupForDataRedactorSpy originalDataGroup;
	private DataGroupForDataRedactorSpy updatedDataGroup;
	private DataGroupRedactorSpy dataGroupRedactorSpy;
	private MethodCallRecorder dataGroupRedactorMCR;
	private MetadataHolderSpy metadataHolder;
	private MethodCallRecorder metadataHolderMCR;

	private String metadataId = "someMetadataId";
	private DataGroupWrapperFactorySpy wrapperFactory;
	private MethodCallRecorder wrapperFactoryMCR;
	private MatcherFactorySpy matcherFactory;
	private MethodCallRecorder matcherFactoryMCR;

	@BeforeMethod
	public void setUp() {
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
		topDataGroupSpy = new DataGroupForDataRedactorSpy("someDataGroup");
		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		someConstraints = createConstraintForSome();
		originalDataGroup = new DataGroupForDataRedactorSpy("originalDataGroup");
		updatedDataGroup = new DataGroupForDataRedactorSpy("changedDataGroup");
	}

	private Set<Constraint> createConstraintForSome() {
		Set<Constraint> recordPartConstraints = new HashSet<>();
		Constraint constraint = new Constraint("some");
		recordPartConstraints.add(constraint);
		return recordPartConstraints;
	}

	@Test
	public void testInit() {
		assertSame(dataRedactor.getMetadataHolder(), metadataHolder);
		assertSame(dataRedactor.getDataGroupRedactor(), dataGroupRedactorSpy);
		assertSame(dataRedactor.getDataGroupWrapperFactory(), wrapperFactory);
		assertSame(dataRedactor.getMatcherFactory(), matcherFactory);
	}

	@Test
	public void testRemoveWhenNoConstraints() throws Exception {
		String metadataId = "someMetadataId";
		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, emptyConstraints, emptyPermissions);
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		assertSame(filteredDataGroup, topDataGroupSpy);
	}

	@Test
	public void testLoopTwoGroupChildren() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, someConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertGroupRedactorCalledForFirstChildGroup();
		assertGroupRedactorCalledForSecondChildGroup();
		assertMetadataHasBeenRequestedForAllProcessedGroups();
	}

	private void assertReturnedDataIsFromGroupRedactor(DataGroup filteredDataGroup) {
		dataGroupRedactorMCR.assertReturn("removeChildrenForConstraintsWithoutPermissions", 0,
				filteredDataGroup);
	}

	private MetadataGroupSpy createAndAddTopGroup(String metadataId) {
		MetadataGroupSpy topGroup = new MetadataGroupSpy(metadataId, "someNameInData");
		metadataHolder.elementsToReturn.put(metadataId, topGroup);
		return topGroup;
	}

	private void assertGroupRedactorCalledForTopLevelDataGroup() {
		dataGroupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 0,
				topDataGroupSpy, someConstraints, emptyPermissions);
	}

	private void assertGroupRedactorCalledForFirstChildGroup() {
		DataGroupForDataRedactorSpy firstChildGroup = getGroupReturnedFromMatcherWithCallNumber(0);
		dataGroupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 1,
				firstChildGroup, someConstraints, emptyPermissions);
	}

	private void assertGroupRedactorCalledForSecondChildGroup() {
		DataGroupForDataRedactorSpy secondChildGroup = getGroupReturnedFromMatcherWithCallNumber(1);
		dataGroupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 2,
				secondChildGroup, someConstraints, emptyPermissions);
	}

	private void assertMetadataHasBeenRequestedForAllProcessedGroups() {
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolderMCR.assertParameters("getMetadataElement", 1, "childDataGroup");
		metadataHolderMCR.assertParameters("getMetadataElement", 2, "recordInfo");
	}

	private MetadataGroupSpy createAndAddChildToDataGroup(MetadataGroupSpy topGroup,
			String linkedRecordType, String linkedRecordId, int repeatMin, int repeatMax) {
		topGroup.createChildReference(linkedRecordType, linkedRecordId, repeatMin, repeatMax,
				ConstraintType.WRITE);
		MetadataGroupSpy metadataChild = new MetadataGroupSpy(linkedRecordId,
				linkedRecordId + "NameInData");

		metadataHolder.elementsToReturn.put(linkedRecordId, metadataChild);
		return metadataChild;
	}

	private DataGroupForDataRedactorSpy getGroupReturnedFromMatcherWithCallNumber(
			int firstGroupNumber) {
		MatcherSpy matcherSpy = matcherFactory.returnedMatchers.get(firstGroupNumber);
		DataGroupForDataRedactorSpy firstChildGroup = matcherSpy.returnedDataGroup;
		return firstChildGroup;
	}

	@Test
	public void testOneGroupChildNoMatchingData() {
		matcherFactory.hasMatchingChild = false;
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "type", 0, 1);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, someConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertOnlyOneCallToGroupRedactor();

		assertMetadataHasBeenRequestedForTwoGroups();
	}

	private void assertOnlyOneCallToGroupRedactor() {
		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
	}

	private void assertMetadataHasBeenRequestedForTwoGroups() {
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolderMCR.assertParameters("getMetadataElement", 1, "type");
	}

	@Test
	public void testOneChildNotGroup() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataTextVariable", "id", 0, 1);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, someConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertOnlyOneCallToGroupRedactor();

		assertOnlyMetadataForTopGroupHasBeenRequested();
	}

	private void assertOnlyMetadataForTopGroupHasBeenRequested() {
		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 1);
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
	}

	@Test
	public void testOneChildGroupRepeatable() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 3);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, someConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertOnlyOneCallToGroupRedactor();

		assertOnlyMetadataForTopGroupHasBeenRequested();
	}

	@Test
	public void testOneGroupChildWithGrandChild() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		MetadataGroupSpy recordInfo = createAndAddChildToDataGroup(topGroup, "metadataGroup",
				"recordInfo", 0, 1);
		createAndAddChildToDataGroup(recordInfo, "metadataGroup", "dataDivider", 0, 1);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, someConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertGroupRedactorCalledForFirstChildGroup();
		assertGroupRedactorCalledForFirstGrandChildGroup();

		assertMetadataHasBeenRequestedForAllProcessedGroupsForGrandChildTest();
	}

	private void assertGroupRedactorCalledForFirstGrandChildGroup() {
		DataGroupForDataRedactorSpy grandChildGroup = getGroupReturnedFromMatcherWithCallNumber(1);

		dataGroupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 2,
				grandChildGroup, someConstraints, emptyPermissions);
	}

	private void assertMetadataHasBeenRequestedForAllProcessedGroupsForGrandChildTest() {
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolderMCR.assertParameters("getMetadataElement", 1, "recordInfo");
		metadataHolderMCR.assertParameters("getMetadataElement", 2, "dataDivider");
	}

	@Test
	public void testRemoveChildrenWithNoMatch() {
		matcherFactory.hasMatchingChild = false;

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		dataRedactor.removeChildrenForConstraintsWithoutPermissions(metadataId, updatedDataGroup,
				someConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 1);

		DataGroupForDataRedactorSpy returnedRedactedGroup = (DataGroupForDataRedactorSpy) dataGroupRedactorMCR
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
	public void testReplaceWhenNoConstraints() throws Exception {
		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, emptyConstraints,
				emptyPermissions);
		assertSame(replacedDataGroup, originalDataGroup);
		metadataHolderMCR.assertMethodNotCalled("getMetadataElement");
		dataGroupRedactorMCR
				.assertMethodNotCalled("replaceChildrenForConstraintsWithoutPermissions");
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
	}

	@Test
	public void testReplaceTopLevelGroupWithoutChildren() throws Exception {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		assertGroupRedactorCalledWithCorrectParametersForTopGroupOnly();
	}

	private void assertGroupRedactorCalledWithCorrectParametersForTopGroupOnly() {
		topGroupIsWrappedAndDataGroupRedactorReplaceCalledForTopLevel();

		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 1);
		wrapperFactoryMCR.assertNumberOfCallsToMethod("factor", 1);
	}

	private void topGroupIsWrappedAndDataGroupRedactorReplaceCalledForTopLevel() {
		// topGroup is wrapped
		DataGroupWrapperSpy wTopDataGroupSpy = (DataGroupWrapperSpy) wrapperFactoryMCR
				.getReturnValue("factor", 0);
		assertSame(wTopDataGroupSpy.dataGroup, topDataGroupSpy);

		// groupRedactorCalled for top wrapped group
		dataGroupRedactorMCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions", 0,
				originalDataGroup, wTopDataGroupSpy, someConstraints, emptyPermissions);
	}

	@Test
	public void testReplaceOneChildNOTGroup() throws Exception {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataTextVar", "type", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		assertGroupRedactorCalledWithCorrectParametersForTopGroupOnly();
	}

	@Test
	public void testReplaceOneChildGroupRepeatMaxNOTOne() throws Exception {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "type", 0, 2);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		assertGroupRedactorCalledWithCorrectParametersForTopGroupOnly();
	}

	@Test
	public void testReplaceOneChildGroupNoDataAfterTopReplace() throws Exception {
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
		// use matcher to see if we have data
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

		// fetch metadata for child reference
		metadataHolderMCR.assertParameters("getMetadataElement", 1, childMetadataId1);
		MetadataGroup returnedMetadataChild1 = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", 1);

		// redacted topGroup
		DataGroupForDataRedactorSpy wTopDataGroupSpy = (DataGroupForDataRedactorSpy) dataGroupRedactorMCR
				.getReturnValue("replaceChildrenForConstraintsWithoutPermissions", 0);
		// factor matcher on returned metadatachild, and wrapped topData
		matcherFactoryMCR.assertParameters("factor", 0, wTopDataGroupSpy, returnedMetadataChild1);
	}

	private void assertNoCallsMadeBeyondFirstChildOnFirstLevel() {
		dataGroupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
		dataGroupRedactorMCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 2);
		wrapperFactoryMCR.assertNumberOfCallsToMethod("factor", 1);
		matcherFactoryMCR.assertNumberOfCallsToMethod("factor", 1);
	}

	@Test
	public void testReplaceOneChildGroupReplacedDataAfterTopReplace() throws Exception {
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
		// use matcher to see if we have data
		matcherChild1.MCR.assertMethodWasCalled("groupHasMatchingDataChild");

		// use matcher to see if data is changed
		DataGroup childData1 = (DataGroup) matcherChild1.MCR.getReturnValue("getMatchingDataChild",
				0);
		// topGroup is wrapped
		DataGroupForDataRedactorSpy wTopDataGroup = (DataGroupForDataRedactorSpy) dataGroupRedactorMCR
				.getReturnValue("replaceChildrenForConstraintsWithoutPermissions", 0);
		wTopDataGroup.MCR.assertParameters("hasRemovedBeenCalled", 0, childData1);
	}

	@Test
	public void testReplaceOneChildGroupDataNotReplacedAfterTopReplaceWithDataInOriginal()
			throws Exception {
		// data finns, ej utbytt, finns i originaldata, gå vidare nedåt, med replace
		matcherFactory.hasMatchingChildList.add(true);
		matcherFactory.hasMatchingChildList.add(true);
		dataGroupRedactorSpy.removeHasBeenCalledList.add(false);

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildToDataGroup(topGroup, "metadataGroup", "type", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				topDataGroupSpy, someConstraints, emptyPermissions);

		assertCorrectCallsMadeForFirstChildOnFirstLevel();
		assertCheckIfFirstChildIsReplaced();

		// MetadataGroup returnedMetadataForTopGroup = (MetadataGroup) metadataHolderMCR
		// .getReturnValue("getMetadataElement", 0);
		// MetadataChildReference metadataChildReference1 = returnedMetadataForTopGroup
		// .getChildReferences().get(0);
		// String childMetadataId1 = metadataChildReference1.getLinkedRecordId();

		// fetch metadata for child reference
		// metadataHolderMCR.assertParameters("getMetadataElement", 1, childMetadataId1);
		MetadataGroup returnedMetadataChild1 = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", 1);

		// redacted topGroup
		// DataGroupForDataRedactorSpy wTopDataGroupSpy = (DataGroupForDataRedactorSpy)
		// dataGroupRedactorMCR
		// .getReturnValue("replaceChildrenForConstraintsWithoutPermissions", 0);
		// factor matcher on returned metadatachild, and wrapped topData
		matcherFactoryMCR.assertParameters("factor", 1, originalDataGroup, returnedMetadataChild1);
		// matcherFactoryMCR.assertParameter("factor", 1, "dataGroup", originalDataGroup);
		// matcherFactoryMCR.assertParameter("factor", 1, "metadataGroup", returnedMetadataChild1);

		// dataGroupRedactorMCR
		// .assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);

		// dataGroupRedactorMCR
		// .assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		// metadataHolderMCR.assertNumberOfCallsToMethod("getMetadataElement", 2);
		// wrapperFactoryMCR.assertNumberOfCallsToMethod("factor", 1);
		// matcherFactoryMCR.assertNumberOfCallsToMethod("factor", 1);
	}
	// /fyra fall,
	// data finns inte
	// data finns, men är utbytt, gå ej vidare
	// data finns, ej utbytt, finns i originaldata, gå vidare nedåt, med replace
	// data finns, ej utbytt, men ej i originaldata, gå vidare nedåt, switch to possiblyRemove

	//
	// @Test
	// public void testReplaceTwoGroupChildren() {
	// List<Boolean> hasMatchingChildList = new ArrayList<>();
	// hasMatchingChildList.add(true);
	// hasMatchingChildList.add(true);
	// hasMatchingChildList.add(true);
	// hasMatchingChildList.add(true);
	// hasMatchingChildList.add(true);
	// hasMatchingChildList.add(true);
	// matcherFactory.hasMatchingChildList = hasMatchingChildList;
	//
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// createAndAddChildToDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
	// createAndAddChildToDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);
	//
	// DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
	// metadataId, originalDataGroup, topDataGroupSpy, titleConstraints, emptyPermissions);
	// assertSame(replacedDataGroup, topDataGroupSpy);
	//
	// // topGroup
	// DataGroupWrapperSpy wTopDataGroupSpy = wrapperFactory.factoredWrappers.get(0);
	// assertSame(wTopDataGroupSpy.dataGroup, topDataGroupSpy);
	// groupRedactorMCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions", 0,
	// originalDataGroup, wTopDataGroupSpy, titleConstraints, emptyPermissions);
	//
	// // firstChild
	// DataGroupForDataRedactorSpy firstChildGroup = getGroupReturnedFromMatcherWithCallNumber(0);
	// DataGroupWrapperSpy wFirstChildGroup = wrapperFactory.factoredWrappers.get(1);
	// assertSame(wFirstChildGroup.dataGroup, firstChildGroup);
	//
	// groupRedactorMCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions", 1,
	// wFirstChildGroup, titleConstraints, emptyPermissions);
	// // assertGroupRedactorCalledForSecondChildGroup();
	// // assertMetadataHasBeenRequestedForAllProcessedGroups();
	// }
	//
	// @Test
	// public void testReplaceTwoGroupChildrenNoneRemovedFromTop() {
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// createAndAddChildToDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
	// createAndAddChildToDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);
	//
	// dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
	// updatedDataGroup, titleConstraints, emptyPermissions);
	//
	// DataGroupWrapperSpy wrapper = wrapperFactory.factoredWrappers.get(0);
	// assertSame(wrapper.dataGroup, updatedDataGroup);
	//
	// groupRedactorMCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions", 0,
	// originalDataGroup, wrapper, titleConstraints, emptyPermissions);
	//
	// DataGroupForDataRedactorSpy groupReturnedFromRedactor = (DataGroupForDataRedactorSpy)
	// groupRedactorMCR
	// .getReturnValue("replaceChildrenForConstraintsWithoutPermissions", 0);
	//
	// assertCallToRedactorReplaceWasCalledForChildData(groupReturnedFromRedactor, 0, 1);
	// assertCallToRedactorReplaceWasCalledForChildData(groupReturnedFromRedactor, 2, 2);
	//
	// assertMetadataHasBeenRequestedForAllProcessedGroups();
	// assertMatcherWasCalledWithCorrectDataForUpdated(0);
	// assertMatcherWasCalledWithCorrectDataForOriginal(1);
	// }
	//
	// private void assertMatcherWasCalledWithCorrectDataForOriginal(int index) {
	// MatcherSpy returnedMatcherForOriginal = matcherFactory.returnedMatchers.get(index);
	// assertEquals(matcherFactory.dataGroups.get(index), originalDataGroup);
	// MetadataGroup returnedMetadataChild = (MetadataGroup) metadataHolderMCR
	// .getReturnValue("getMetadataElement", index);
	// assertEquals(matcherFactory.metadataGroups.get(index), returnedMetadataChild);
	//
	// assertTrue(returnedMatcherForOriginal.hasMatchingChildWasCalled);
	// assertTrue(returnedMatcherForOriginal.getMatchingChildWasCalled);
	// }
	//
	// private void assertMatcherWasCalledWithCorrectDataForUpdated(int index) {
	// DataGroupForDataRedactorSpy returnedRedactedGroup = (DataGroupForDataRedactorSpy)
	// groupRedactorMCR
	// .getReturnValue("replaceChildrenForConstraintsWithoutPermissions", index);
	//
	// MetadataGroup returnedMetadataChild = (MetadataGroup) metadataHolderMCR
	// .getReturnValue("getMetadataElement", 1);
	//
	// assertEquals(matcherFactory.dataGroups.get(index), returnedRedactedGroup);
	// assertEquals(matcherFactory.metadataGroups.get(index), returnedMetadataChild);
	// MatcherSpy returnedMatcherForUpdated = matcherFactory.returnedMatchers.get(index);
	// assertTrue(returnedMatcherForUpdated.hasMatchingChildWasCalled);
	// assertTrue(returnedMatcherForUpdated.getMatchingChildWasCalled);
	// }
	//
	// private void assertCallToRedactorReplaceWasCalledForChildData(
	// DataGroupForDataRedactorSpy filteredDataGroup, int matcherIndex, int replaceCallIndex) {
	//
	// MatcherSpy matcherSpy = matcherFactory.returnedMatchers.get(matcherIndex);
	// DataGroupForDataRedactorSpy replacedChild = matcherSpy.returnedDataGroup;
	//
	// MatcherSpy matcherSpy2 = matcherFactory.returnedMatchers.get(matcherIndex + 1);
	// DataGroupForDataRedactorSpy originalChild = matcherSpy2.returnedDataGroup;
	//
	// Map<String, Object> parametersForMethodAndCallNumber = groupRedactorMCR
	// .getParametersForMethodAndCallNumber(
	// "replaceChildrenForConstraintsWithoutPermissions", replaceCallIndex);
	// assertEquals(parametersForMethodAndCallNumber.get("originalDataGroup"), originalChild);
	//
	// DataGroupWrapperSpy wrapper = wrapperFactory.factoredWrappers.get(replaceCallIndex);
	//
	// assertEquals(wrapper.dataGroup, replacedChild);
	// assertEquals(parametersForMethodAndCallNumber.get("recordPartConstraints"),
	// titleConstraints);
	// assertEquals(parametersForMethodAndCallNumber.get("recordPartPermissions"),
	// emptyPermissions);
	// }
	//
	// @Test
	// public void testReplaceOneChildGroupRepetable() {
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// createAndAddChildToDataGroup(topGroup, "metadataTextVariable", "childText", 0, 1);
	//
	// dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
	// updatedDataGroup, titleConstraints, emptyPermissions);
	//
	// assertEquals(matcherFactory.returnedMatchers.size(), 0);
	//
	// }
	//
	// @Test
	// public void testReplaceOneChildGroupNoMatchOnNameInData() {
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// createAndAddChildToDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);
	//
	// dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
	// updatedDataGroup, titleConstraints, emptyPermissions);
	//
	// assertEquals(matcherFactory.returnedMatchers.size(), 2);
	//
	// groupRedactorMCR
	// .assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
	// }
	//
	// @Test
	// public void testReplaceOneChildGroupMatchOnNameInDataAndNoMatchOnEmptyAttributes() {
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// createAndAddChildToDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);
	//
	// // List<DataAttribute> attributes = new ArrayList<>();
	// // attributes.add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
	// // List<List<DataAttribute>> listOfAttributes = new ArrayList<>();
	// // listOfAttributes.add(attributes);
	// // wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", listOfAttributes);
	//
	// dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
	// updatedDataGroup, titleConstraints, emptyPermissions);
	//
	// assertEquals(matcherFactory.returnedMatchers.size(), 2);
	//
	// groupRedactorMCR
	// .assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
	// }
	//
	// @Test
	// public void testReplaceOneChildGroupMatchOnNameInDataAndNoMatchAttributes() {
	//
	// // List<DataAttribute> replacedMetadataAttributes = new ArrayList<>();
	// // replacedMetadataAttributes
	// // .add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
	// // matcherFactory.attributesToMatchedDataGroup = replacedMetadataAttributes;
	//
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// createAndAddChildToDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);
	//
	// // List<DataAttribute> attributes = new ArrayList<>();
	// // attributes.add(new DataAttributeSpy("NOTsomeAttributeId", "NOTsomeAttributeValue"));
	// // List<List<DataAttribute>> listOfAttributes = new ArrayList<>();
	// // listOfAttributes.add(attributes);
	//
	// // wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", listOfAttributes);
	// // List<DataAttribute> emptyAttributes = new ArrayList<>();
	// // wrapperFactory.nameInDatasToRemove.get("childGroupNameInData").add(emptyAttributes);
	//
	// dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
	// updatedDataGroup, titleConstraints, emptyPermissions);
	//
	// assertEquals(matcherFactory.returnedMatchers.size(), 2);
	//
	// groupRedactorMCR
	// .assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
	// }
	//
	// @Test
	// public void testReplaceOneChildGroupWhenAlreadyBeenReplaced() {
	// wrapperFactory.removeHasBeenCalled = true;
	//
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// createAndAddChildToDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);
	//
	// dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
	// updatedDataGroup, titleConstraints, emptyPermissions);
	// DataGroupWrapperSpy dataGroupWrapper = wrapperFactory.factoredWrappers.get(0);
	//
	// assertSame(dataGroupWrapper.dataGroup, updatedDataGroup);
	//
	// DataGroupForDataRedactorSpy dataGroupFromMatcher = matcherFactory.returnedMatchers
	// .get(0).returnedDataGroup;
	// DataGroupWrapperSpy dataGroupWrapperForChild = wrapperFactory.factoredWrappers.get(1);
	// assertSame(dataGroupWrapperForChild.dataGroup, dataGroupFromMatcher);
	//
	// assertEquals(matcherFactory.returnedMatchers.size(), 2);
	// groupRedactorMCR
	// .assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	// }
	//
	// @Test
	// public void testReplacedNotRecursivlyCalledForChildGroupWithoutData() {
	//
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// createAndAddChildToDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);
	//
	// List<Boolean> hasMatchingChildList = new ArrayList<>();
	// hasMatchingChildList.add(false);
	// hasMatchingChildList.add(true);
	// matcherFactory.hasMatchingChildList = hasMatchingChildList;
	//
	// // wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", Collections.emptyList());
	//
	// dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
	// updatedDataGroup, titleConstraints, emptyPermissions);
	//
	// assertEquals(matcherFactory.returnedMatchers.size(), 1);
	//
	// groupRedactorMCR
	// .assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	// }
	//
	// // @Test
	// // public void testReplacedNotRecursivlyCalledForChildGroupWithDataButNoPermission() {
	// // // TODO: working on this test...
	// // // wrapperFactory.removeHasBeenCalled = true;
	// //
	// // // MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// // // createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);
	// //
	// // MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// // MetadataGroupSpy recordInfo = createAndAddChildDataGroup(topGroup, "metadataGroup",
	// // "recordInfo", 0, 1);
	// // createAndAddChildDataGroup(recordInfo, "metadataGroup", "dataDivider", 0, 1);
	// //
	// // List<Boolean> hasMatchingChildList = new ArrayList<>();
	// // hasMatchingChildList.add(true);
	// // hasMatchingChildList.add(false);
	// // // matcherFactory.hasMatchingChildList = hasMatchingChildList;
	// //
	// // // wrapperFactory.nameInDatasToRemove.put("childGroupNameInData",
	// Collections.emptyList());
	// //
	// // DataGroup filteredDataGroup =
	// dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
	// // metadataId, originalDataGroup, updatedDataGroup, titleConstraints,
	// // emptyPermissions);
	// //
	// // assertEquals(matcherFactory.returnedMatchers.size(), 1);
	// //
	// // groupRedactorMCR
	// // .assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	// // // groupRedactorMCR
	// // // .assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
	// // groupRedactorMCR.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
	// //
	// // assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
	// // // assertGroupRedactorCalledForTopLevelDataGroup();
	// // // assertGroupRedactorCalledForFirstChildGroup();
	// // // assertGroupRedactorCalledForFirstGrandChildGroup();
	// // //
	// // // assertMetadataHasBeenRequestedForAllProcessedGroupsForGrandChildTest();
	// // }
	//
	// @Test
	// public void testReplaceCallRedirectedToRemoveForChildGroupWithNoDataInOriginal() {
	//
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// createAndAddChildToDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);
	//
	// List<Boolean> hasMatchingChildList = new ArrayList<>();
	// hasMatchingChildList.add(true);
	// hasMatchingChildList.add(false);
	// matcherFactory.hasMatchingChildList = hasMatchingChildList;
	//
	// // wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", Collections.emptyList());
	//
	// dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
	// updatedDataGroup, titleConstraints, emptyPermissions);
	//
	// assertEquals(matcherFactory.returnedMatchers.size(), 2);
	// MatcherSpy matcherSpy = matcherFactory.returnedMatchers.get(0);
	// DataGroupForDataRedactorSpy returnedDataGroup = matcherSpy.returnedDataGroup;
	//
	// groupRedactorMCR
	// .assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	// groupRedactorMCR
	// .assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
	// groupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 0,
	// returnedDataGroup, titleConstraints, emptyPermissions);
	// }

}
