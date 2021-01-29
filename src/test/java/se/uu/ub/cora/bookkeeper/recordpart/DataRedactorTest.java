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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.spy.MethodCallRecorder;
import se.uu.ub.cora.data.DataGroup;

public class DataRedactorTest {
	private DataRedactorImp dataRedactor;

	private DataGroupForDataRedactorSpy topDataGroupSpy;
	private Set<Constraint> emptyConstraints;
	private Set<String> emptyPermissions;
	private Set<Constraint> titleConstraints;
	private DataGroupForDataRedactorSpy originalDataGroup;
	private DataGroupForDataRedactorSpy updatedDataGroup;
	private DataGroupRedactorSpy dataGroupRedactorSpy;
	private MethodCallRecorder groupRedactorMCR;
	private MetadataHolderSpy metadataHolder;
	private MethodCallRecorder metadataHolderMCR;

	private String metadataId = "someMetadataId";
	private DataGroupWrapperFactorySpy wrapperFactory;
	private MatcherFactorySpy matcherFactory;

	@BeforeMethod
	public void setUp() {
		metadataHolder = new MetadataHolderSpy();
		metadataHolderMCR = metadataHolder.MCR;
		dataGroupRedactorSpy = new DataGroupRedactorSpy();
		groupRedactorMCR = dataGroupRedactorSpy.MCR;
		wrapperFactory = new DataGroupWrapperFactorySpy();
		matcherFactory = new MatcherFactorySpy();
		dataRedactor = new DataRedactorImp(metadataHolder, dataGroupRedactorSpy, wrapperFactory,
				matcherFactory);
		topDataGroupSpy = new DataGroupForDataRedactorSpy("someDataGroup");
		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		titleConstraints = createConstraintForTitle();
		originalDataGroup = new DataGroupForDataRedactorSpy("originalDataGroup");
		updatedDataGroup = new DataGroupForDataRedactorSpy("changedDataGroup");
	}

	private Set<Constraint> createConstraintForTitle() {
		Set<Constraint> recordPartConstraints = new HashSet<>();
		Constraint constraint = new Constraint("title");
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
		groupRedactorMCR.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
		assertSame(filteredDataGroup, topDataGroupSpy);
	}

	@Test
	public void testLoopTwoGroupChildren() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, titleConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertGroupRedactorCalledForFirstChildGroup();
		assertGroupRedactorCalledForSecondChildGroup();
		assertMetadataHasBeenRequestedForAllProcessedGroups();
	}

	private void assertReturnedDataIsFromGroupRedactor(DataGroup filteredDataGroup) {
		groupRedactorMCR.assertReturn("removeChildrenForConstraintsWithoutPermissions", 0,
				filteredDataGroup);
	}

	private MetadataGroupSpy createAndAddTopGroup(String metadataId) {
		MetadataGroupSpy topGroup = new MetadataGroupSpy(metadataId, "someNameInData");
		metadataHolder.elementsToReturn.put(metadataId, topGroup);
		return topGroup;
	}

	private void assertGroupRedactorCalledForTopLevelDataGroup() {
		groupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 0,
				topDataGroupSpy, titleConstraints, emptyPermissions);
	}

	private void assertGroupRedactorCalledForFirstChildGroup() {
		DataGroupForDataRedactorSpy firstChildGroup = getGroupReturnedFromMatcherWithCallNumber(0,
				0);
		groupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 1,
				firstChildGroup, titleConstraints, emptyPermissions);
	}

	private void assertGroupRedactorCalledForSecondChildGroup() {
		DataGroupForDataRedactorSpy secondChildGroup = getGroupReturnedFromMatcherWithCallNumber(0,
				1);
		groupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 2,
				secondChildGroup, titleConstraints, emptyPermissions);
	}

	private void assertMetadataHasBeenRequestedForAllProcessedGroups() {
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolderMCR.assertParameters("getMetadataElement", 1, "childDataGroup");
		metadataHolderMCR.assertParameters("getMetadataElement", 2, "recordInfo");
	}

	private MetadataGroupSpy createAndAddChildDataGroup(MetadataGroupSpy topGroup,
			String linkedRecordType, String linkedRecordId, int repeatMin, int repeatMax) {
		topGroup.createChildReference(linkedRecordType, linkedRecordId, repeatMin, repeatMax,
				ConstraintType.WRITE);
		MetadataGroupSpy metadataChild = new MetadataGroupSpy(linkedRecordId,
				linkedRecordId + "NameInData");

		metadataHolder.elementsToReturn.put(linkedRecordId, metadataChild);
		return metadataChild;
	}

	private DataGroupForDataRedactorSpy getGroupReturnedFromMatcherWithCallNumber(int removeNumber,
			int firstGroupNumber) {
		MatcherSpy matcherSpy = matcherFactory.returnedMatchers.get(firstGroupNumber);
		DataGroupForDataRedactorSpy firstChildGroup = matcherSpy.returnedDataGroup;
		return firstChildGroup;
	}

	@Test
	public void testOneGroupChildNoMatchingData() {
		matcherFactory.hasMatchingChild = false;
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "type", 0, 1);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, titleConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertOnlyOneCallToGroupRedactor();

		assertMetadataHasBeenRequestedForTwoGroups();
	}

	private void assertOnlyOneCallToGroupRedactor() {
		groupRedactorMCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
	}

	private void assertMetadataHasBeenRequestedForTwoGroups() {
		metadataHolderMCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolderMCR.assertParameters("getMetadataElement", 1, "type");
	}

	@Test
	public void testOneChildNotGroup() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataTextVariable", "id", 0, 1);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, titleConstraints, emptyPermissions);

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
		createAndAddChildDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 3);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, titleConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertOnlyOneCallToGroupRedactor();

		assertOnlyMetadataForTopGroupHasBeenRequested();
	}

	@Test
	public void testOneGroupChildWithGrandChild() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		MetadataGroupSpy recordInfo = createAndAddChildDataGroup(topGroup, "metadataGroup",
				"recordInfo", 0, 1);
		createAndAddChildDataGroup(recordInfo, "metadataGroup", "dataDivider", 0, 1);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, titleConstraints, emptyPermissions);

		assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
		assertGroupRedactorCalledForTopLevelDataGroup();
		assertGroupRedactorCalledForFirstChildGroup();
		assertGroupRedactorCalledForFirstGrandChildGroup();

		assertMetadataHasBeenRequestedForAllProcessedGroupsForGrandChildTest();
	}

	private void assertGroupRedactorCalledForFirstGrandChildGroup() {
		DataGroupForDataRedactorSpy grandChildGroup = getGroupReturnedFromMatcherWithCallNumber(1,
				1);

		groupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 2,
				grandChildGroup, titleConstraints, emptyPermissions);
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
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		dataRedactor.removeChildrenForConstraintsWithoutPermissions(metadataId, updatedDataGroup,
				titleConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 1);

		DataGroupForDataRedactorSpy returnedRedactedGroup = (DataGroupForDataRedactorSpy) groupRedactorMCR
				.getReturnValue("removeChildrenForConstraintsWithoutPermissions", 0);

		MetadataGroup returnedMetadataChild = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", 1);

		assertEquals(matcherFactory.dataGroups.get(0), returnedRedactedGroup);
		assertEquals(matcherFactory.metadataGroups.get(0), returnedMetadataChild);
		MatcherSpy returnedMatcherForUpdated = matcherFactory.returnedMatchers.get(0);
		assertTrue(returnedMatcherForUpdated.hasMatchingChildWasCalled);
		assertFalse(returnedMatcherForUpdated.getMatchingChildWasCalled);

		groupRedactorMCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
	}

	@Test
	public void testReplaceWhenNoConstraints() throws Exception {
		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, emptyConstraints,
				emptyPermissions);
		assertSame(replacedDataGroup, originalDataGroup);
		metadataHolderMCR.assertMethodNotCalled("getMetadataElement");
		groupRedactorMCR.assertMethodNotCalled("replaceChildrenForConstraintsWithoutPermissions");
		groupRedactorMCR.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
	}

	@Test
	public void testReplaceTwoGroupChildren() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);

		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, topDataGroupSpy, titleConstraints, emptyPermissions);
		assertSame(replacedDataGroup, topDataGroupSpy);
		DataGroupWrapperSpy wTopDataGroupSpy = wrapperFactory.factoredWrappers.get(0);
		groupRedactorMCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions", 0,
				originalDataGroup, wTopDataGroupSpy, titleConstraints, emptyPermissions);
		// assertGroupRedactorCalledForFirstChildGroup();
		// assertGroupRedactorCalledForSecondChildGroup();
		// assertMetadataHasBeenRequestedForAllProcessedGroups();
	}

	@Test
	public void testReplaceTwoGroupChildrenNoneRemovedFromTop() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		DataGroupWrapperSpy wrapper = wrapperFactory.factoredWrappers.get(0);
		assertSame(wrapper.dataGroup, updatedDataGroup);

		groupRedactorMCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions", 0,
				originalDataGroup, wrapper, titleConstraints, emptyPermissions);

		DataGroupForDataRedactorSpy groupReturnedFromRedactor = (DataGroupForDataRedactorSpy) groupRedactorMCR
				.getReturnValue("replaceChildrenForConstraintsWithoutPermissions", 0);

		assertCallToRedactorReplaceWasCalledForChildData(groupReturnedFromRedactor, 0, 1);
		assertCallToRedactorReplaceWasCalledForChildData(groupReturnedFromRedactor, 2, 2);

		assertMetadataHasBeenRequestedForAllProcessedGroups();
		assertMatcherWasCalledWithCorrectDataForUpdated(0);
		assertMatcherWasCalledWithCorrectDataForOriginal(1);
	}

	private void assertMatcherWasCalledWithCorrectDataForOriginal(int index) {
		MatcherSpy returnedMatcherForOriginal = matcherFactory.returnedMatchers.get(index);
		assertEquals(matcherFactory.dataGroups.get(index), originalDataGroup);
		MetadataGroup returnedMetadataChild = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", index);
		assertEquals(matcherFactory.metadataGroups.get(index), returnedMetadataChild);

		assertTrue(returnedMatcherForOriginal.hasMatchingChildWasCalled);
		assertTrue(returnedMatcherForOriginal.getMatchingChildWasCalled);
	}

	private void assertMatcherWasCalledWithCorrectDataForUpdated(int index) {
		DataGroupForDataRedactorSpy returnedRedactedGroup = (DataGroupForDataRedactorSpy) groupRedactorMCR
				.getReturnValue("replaceChildrenForConstraintsWithoutPermissions", index);

		MetadataGroup returnedMetadataChild = (MetadataGroup) metadataHolderMCR
				.getReturnValue("getMetadataElement", 1);

		assertEquals(matcherFactory.dataGroups.get(index), returnedRedactedGroup);
		assertEquals(matcherFactory.metadataGroups.get(index), returnedMetadataChild);
		MatcherSpy returnedMatcherForUpdated = matcherFactory.returnedMatchers.get(index);
		assertTrue(returnedMatcherForUpdated.hasMatchingChildWasCalled);
		assertTrue(returnedMatcherForUpdated.getMatchingChildWasCalled);
	}

	private void assertCallToRedactorReplaceWasCalledForChildData(
			DataGroupForDataRedactorSpy filteredDataGroup, int matcherIndex, int replaceCallIndex) {

		MatcherSpy matcherSpy = matcherFactory.returnedMatchers.get(matcherIndex);
		DataGroupForDataRedactorSpy replacedChild = matcherSpy.returnedDataGroup;

		MatcherSpy matcherSpy2 = matcherFactory.returnedMatchers.get(matcherIndex + 1);
		DataGroupForDataRedactorSpy originalChild = matcherSpy2.returnedDataGroup;

		Map<String, Object> parametersForMethodAndCallNumber = groupRedactorMCR
				.getParametersForMethodAndCallNumber(
						"replaceChildrenForConstraintsWithoutPermissions", replaceCallIndex);
		assertEquals(parametersForMethodAndCallNumber.get("originalDataGroup"), originalChild);

		DataGroupWrapperSpy wrapper = wrapperFactory.factoredWrappers.get(replaceCallIndex);

		assertEquals(wrapper.dataGroup, replacedChild);
		assertEquals(parametersForMethodAndCallNumber.get("recordPartConstraints"),
				titleConstraints);
		assertEquals(parametersForMethodAndCallNumber.get("recordPartPermissions"),
				emptyPermissions);
	}

	@Test
	public void testReplaceOneChildNotGroup() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 4);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 0);

	}

	@Test
	public void testReplaceOneChildGroupRepetable() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataTextVariable", "childText", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 0);

	}

	@Test
	public void testReplaceOneChildGroupNoMatchOnNameInData() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 2);

		groupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
	}

	@Test
	public void testReplaceOneChildGroupMatchOnNameInDataAndNoMatchOnEmptyAttributes() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		// List<DataAttribute> attributes = new ArrayList<>();
		// attributes.add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
		// List<List<DataAttribute>> listOfAttributes = new ArrayList<>();
		// listOfAttributes.add(attributes);
		// wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", listOfAttributes);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 2);

		groupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
	}

	@Test
	public void testReplaceOneChildGroupMatchOnNameInDataAndNoMatchAttributes() {

		// List<DataAttribute> replacedMetadataAttributes = new ArrayList<>();
		// replacedMetadataAttributes
		// .add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
		// matcherFactory.attributesToMatchedDataGroup = replacedMetadataAttributes;

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		// List<DataAttribute> attributes = new ArrayList<>();
		// attributes.add(new DataAttributeSpy("NOTsomeAttributeId", "NOTsomeAttributeValue"));
		// List<List<DataAttribute>> listOfAttributes = new ArrayList<>();
		// listOfAttributes.add(attributes);

		// wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", listOfAttributes);
		// List<DataAttribute> emptyAttributes = new ArrayList<>();
		// wrapperFactory.nameInDatasToRemove.get("childGroupNameInData").add(emptyAttributes);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 2);

		groupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
	}

	@Test
	public void testReplaceOneChildGroupWhenAlreadyBeenReplaced() {
		wrapperFactory.removeHasBeenCalled = true;

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);
		DataGroupWrapperSpy dataGroupWrapper = wrapperFactory.factoredWrappers.get(0);

		assertSame(dataGroupWrapper.dataGroup, updatedDataGroup);

		DataGroupForDataRedactorSpy dataGroupFromMatcher = matcherFactory.returnedMatchers
				.get(0).returnedDataGroup;
		DataGroupWrapperSpy dataGroupWrapperForChild = wrapperFactory.factoredWrappers.get(1);
		assertSame(dataGroupWrapperForChild.dataGroup, dataGroupFromMatcher);

		assertEquals(matcherFactory.returnedMatchers.size(), 2);
		groupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	}

	@Test
	public void testReplacedNotRecursivlyCalledForChildGroupWithoutData() {

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<Boolean> hasMatchingChildList = new ArrayList<>();
		hasMatchingChildList.add(false);
		hasMatchingChildList.add(true);
		matcherFactory.hasMatchingChildList = hasMatchingChildList;

		// wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", Collections.emptyList());

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 1);

		groupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	}

	// @Test
	// public void testReplacedNotRecursivlyCalledForChildGroupWithDataButNoPermission() {
	// // TODO: working on this test...
	// // wrapperFactory.removeHasBeenCalled = true;
	//
	// // MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// // createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);
	//
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// MetadataGroupSpy recordInfo = createAndAddChildDataGroup(topGroup, "metadataGroup",
	// "recordInfo", 0, 1);
	// createAndAddChildDataGroup(recordInfo, "metadataGroup", "dataDivider", 0, 1);
	//
	// List<Boolean> hasMatchingChildList = new ArrayList<>();
	// hasMatchingChildList.add(true);
	// hasMatchingChildList.add(false);
	// // matcherFactory.hasMatchingChildList = hasMatchingChildList;
	//
	// // wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", Collections.emptyList());
	//
	// DataGroup filteredDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
	// metadataId, originalDataGroup, updatedDataGroup, titleConstraints,
	// emptyPermissions);
	//
	// assertEquals(matcherFactory.returnedMatchers.size(), 1);
	//
	// groupRedactorMCR
	// .assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	// // groupRedactorMCR
	// // .assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
	// groupRedactorMCR.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
	//
	// assertReturnedDataIsFromGroupRedactor(filteredDataGroup);
	// // assertGroupRedactorCalledForTopLevelDataGroup();
	// // assertGroupRedactorCalledForFirstChildGroup();
	// // assertGroupRedactorCalledForFirstGrandChildGroup();
	// //
	// // assertMetadataHasBeenRequestedForAllProcessedGroupsForGrandChildTest();
	// }

	@Test
	public void testReplaceCallRedirectedToRemoveForChildGroupWithNoDataInOriginal() {

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<Boolean> hasMatchingChildList = new ArrayList<>();
		hasMatchingChildList.add(true);
		hasMatchingChildList.add(false);
		matcherFactory.hasMatchingChildList = hasMatchingChildList;

		// wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", Collections.emptyList());

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 2);
		MatcherSpy matcherSpy = matcherFactory.returnedMatchers.get(0);
		DataGroupForDataRedactorSpy returnedDataGroup = matcherSpy.returnedDataGroup;

		groupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
		groupRedactorMCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
		groupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 0,
				returnedDataGroup, titleConstraints, emptyPermissions);
	}

}
