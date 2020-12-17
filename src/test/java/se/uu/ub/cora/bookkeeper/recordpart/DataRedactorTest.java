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

import se.uu.ub.cora.bookkeeper.DataAttributeSpy;
import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.spy.MethodCallRecorder;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;

public class DataRedactorTest {
	private DataRedactor dataRedactor;

	private DataGroupForDataRedactorSpy topDataGroupSpy;
	private Set<Constraint> emptyConstraints;
	private Set<String> emptyPermissions;
	private Set<Constraint> titleConstraints;
	private DataGroupForDataRedactorSpy originalDataGroup;
	private DataGroupForDataRedactorSpy updatedDataGroup;
	private DataGroupRedactorSpy dataGroupRedactorSpy;
	private MethodCallRecorder groupRedactorMCR;
	private MetadataHolderSpy metadataHolder;

	private String metadataId = "someMetadataId";

	private MetadataMatchFactorySpy matchFactory;

	private DataGroupWrapperFactorySpy wrapperFactory;

	private MatcherFactorySpy matcherFactory;

	@BeforeMethod
	public void setUp() {
		metadataHolder = new MetadataHolderSpy();
		dataGroupRedactorSpy = new DataGroupRedactorSpy();
		groupRedactorMCR = dataGroupRedactorSpy.MCR;
		matchFactory = new MetadataMatchFactorySpy();
		wrapperFactory = new DataGroupWrapperFactorySpy();
		matcherFactory = new MatcherFactorySpy();
		dataRedactor = new DataRedactorImp(metadataHolder, dataGroupRedactorSpy, matchFactory,
				wrapperFactory, matcherFactory);
		topDataGroupSpy = new DataGroupForDataRedactorSpy("someDataGroup");
		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		titleConstraints = createReadConstraintForTitle();
		originalDataGroup = new DataGroupForDataRedactorSpy("originalDataGroup");
		updatedDataGroup = new DataGroupForDataRedactorSpy("changedDataGroup");
	}

	private Set<Constraint> createReadConstraintForTitle() {
		Set<Constraint> recordPartConstraints = new HashSet<>();
		Constraint constraint = new Constraint("title");
		recordPartConstraints.add(constraint);
		return recordPartConstraints;
	}

	@Test
	public void testRemoveWhenNoConstraints() throws Exception {
		String metadataId = "someMetadataId";
		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, emptyConstraints, emptyPermissions);
		dataGroupRedactorSpy.MCR
				.assertMethodNotCalled("removeChildrenForConstraintsWithoutPermissions");
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
		DataGroupForDataRedactorSpy firstChildGroup = getGroupRequestedFromRedactedGroupWithCallNumber(
				0, 0);
		groupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 1,
				firstChildGroup, titleConstraints, emptyPermissions);
	}

	private void assertGroupRedactorCalledForSecondChildGroup() {
		DataGroupForDataRedactorSpy secondChildGroup = getGroupRequestedFromRedactedGroupWithCallNumber(
				0, 1);
		groupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 2,
				secondChildGroup, titleConstraints, emptyPermissions);
	}

	private void assertMetadataHasBeenRequestedForAllProcessedGroups() {
		metadataHolder.MCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolder.MCR.assertParameters("getMetadataElement", 1, "childDataGroup");
		metadataHolder.MCR.assertParameters("getMetadataElement", 2, "recordInfo");
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

	private DataGroupForDataRedactorSpy getGroupRequestedFromRedactedGroupWithCallNumber(
			int removeNumber, int firstGroupNumber) {
		DataGroupForDataRedactorSpy redactedGroup = (DataGroupForDataRedactorSpy) dataGroupRedactorSpy.MCR
				.getReturnValue("removeChildrenForConstraintsWithoutPermissions", removeNumber);
		DataGroupForDataRedactorSpy firstChildGroup = (DataGroupForDataRedactorSpy) redactedGroup.MCR
				.getReturnValue("getFirstGroupWithNameInData", firstGroupNumber);
		return firstChildGroup;
	}

	@Test
	public void testOneGroupChildNoMatchingData() {
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
		metadataHolder.MCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolder.MCR.assertParameters("getMetadataElement", 1, "type");
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
		metadataHolder.MCR.assertNumberOfCallsToMethod("getMetadataElement", 1);
		metadataHolder.MCR.assertParameters("getMetadataElement", 0, metadataId);
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
		DataGroupForDataRedactorSpy grandChildGroup = getGroupRequestedFromRedactedGroupWithCallNumber(
				1, 0);

		groupRedactorMCR.assertParameters("removeChildrenForConstraintsWithoutPermissions", 2,
				grandChildGroup, titleConstraints, emptyPermissions);
	}

	private void assertMetadataHasBeenRequestedForAllProcessedGroupsForGrandChildTest() {
		metadataHolder.MCR.assertParameters("getMetadataElement", 0, metadataId);
		metadataHolder.MCR.assertParameters("getMetadataElement", 1, "recordInfo");
		metadataHolder.MCR.assertParameters("getMetadataElement", 2, "dataDivider");
	}

	@Test
	public void testRemoveChildrenWithNoAttributesMAtch() {

		List<DataAttribute> attributestoRemove = new ArrayList<>();
		attributestoRemove.add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
		dataGroupRedactorSpy.attributesToReplacedDataGroup = attributestoRemove;

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<DataAttribute> attributes = new ArrayList<>();
		attributes.add(new DataAttributeSpy("NOTsomeAttributeId", "NOTsomeAttributeValue"));
		List<List<DataAttribute>> listOfAttributes = new ArrayList<>();
		listOfAttributes.add(attributes);

		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", listOfAttributes);
		List<DataAttribute> emptyAttributes = new ArrayList<>();
		wrapperFactory.nameInDatasToRemove.get("childGroupNameInData").add(emptyAttributes);

		dataRedactor.removeChildrenForConstraintsWithoutPermissions(metadataId, updatedDataGroup,
				titleConstraints, emptyPermissions);

		// assertEquals(matchFactory.returnedMatchers.size(), 2);
		assertEquals(matcherFactory.returnedMatchers.size(), 2);

		dataGroupRedactorSpy.MCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
	}

	@Test
	public void testReplaceWhenNoConstraints() throws Exception {
		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, emptyConstraints,
				emptyPermissions);
		assertSame(replacedDataGroup, originalDataGroup);
		dataGroupRedactorSpy.MCR
				.assertMethodNotCalled("replaceChildrenForConstraintsWithoutPermissions");
	}

	@Test
	public void testReplaceTwoGroupChildrenNoneRemovedFromTop2() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertMatcherWasCalledWithCorrectDataForUpdated(0);
		assertMatcherWasCalledWithCorrectDataForOriginal(1);
	}

	private void assertMatcherWasCalledWithCorrectDataForOriginal(int index) {
		MatcherSpy returnedMatcherForOriginal = matcherFactory.returnedMatchers.get(index);
		assertEquals(matcherFactory.dataGroups.get(index), originalDataGroup);
		MetadataGroup returnedMetadataChild = (MetadataGroup) metadataHolder.MCR
				.getReturnValue("getMetadataElement", index);
		assertEquals(matcherFactory.metadataGroups.get(index), returnedMetadataChild);

		assertTrue(returnedMatcherForOriginal.hasMatchingChildWasCalled);
		assertTrue(returnedMatcherForOriginal.getMatchingChildWasCalled);
	}

	private void assertMatcherWasCalledWithCorrectDataForUpdated(int index) {
		DataGroupForDataRedactorSpy returnedRedactedGroup = (DataGroupForDataRedactorSpy) groupRedactorMCR
				.getReturnValue("replaceChildrenForConstraintsWithoutPermissions", index);

		MetadataGroup returnedMetadataChild = (MetadataGroup) metadataHolder.MCR
				.getReturnValue("getMetadataElement", 1);

		assertEquals(matcherFactory.dataGroups.get(index), returnedRedactedGroup);
		assertEquals(matcherFactory.metadataGroups.get(index), returnedMetadataChild);
		MatcherSpy returnedMatcherForUpdated = matcherFactory.returnedMatchers.get(index);
		assertTrue(returnedMatcherForUpdated.hasMatchingChildWasCalled);
		assertTrue(returnedMatcherForUpdated.getMatchingChildWasCalled);
	}

	@Test
	public void testReplaceTwoGroupChildrenNoneRemovedFromTop() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		DataGroupWrapperImp wrapper = wrapperFactory.factoredWrappers.get(0);
		assertSame(wrapper.getDataGroup(), updatedDataGroup);

		dataGroupRedactorSpy.MCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions",
				0, originalDataGroup, wrapper, titleConstraints, emptyPermissions);

		assertBothChildrenAreSentToMatcherTwice();

		DataGroupForDataRedactorSpy groupReturnedFromRedactor = (DataGroupForDataRedactorSpy) dataGroupRedactorSpy.MCR
				.getReturnValue("replaceChildrenForConstraintsWithoutPermissions", 0);

		assertCallToRedactorReplaceWasCalledForChildData(groupReturnedFromRedactor, 0, 1);
		assertCallToRedactorReplaceWasCalledForChildData(groupReturnedFromRedactor, 1, 2);

		assertMetadataHasBeenRequestedForAllProcessedGroups();
	}

	private void assertCallToRedactorReplaceWasCalledForChildData(
			DataGroupForDataRedactorSpy filteredDataGroup, int getAllChildrenIndex,
			int replaceCallIndex) {
		DataGroupForDataRedactorSpy originalChild = getOriginalChild(getAllChildrenIndex);

		DataGroupForDataRedactorSpy replacedChild = getUpdatedChild(filteredDataGroup,
				getAllChildrenIndex);

		Map<String, Object> parametersForMethodAndCallNumber = groupRedactorMCR
				.getParametersForMethodAndCallNumber(
						"replaceChildrenForConstraintsWithoutPermissions", replaceCallIndex);
		assertEquals(parametersForMethodAndCallNumber.get("originalDataGroup"), originalChild);

		DataGroupWrapperImp wrapper = (DataGroupWrapperImp) parametersForMethodAndCallNumber
				.get("changedDataGroup");

		assertEquals(wrapper.dataGroup, replacedChild);
		assertEquals(parametersForMethodAndCallNumber.get("recordPartConstraints"),
				titleConstraints);
		assertEquals(parametersForMethodAndCallNumber.get("recordPartPermissions"),
				emptyPermissions);
	}

	private DataGroupForDataRedactorSpy getUpdatedChild(
			DataGroupForDataRedactorSpy filteredDataGroup, int getAllChildrenIndex) {
		List<?> replacedChildren = (List<?>) filteredDataGroup.MCR
				.getReturnValue("getAllChildrenWithNameInData", getAllChildrenIndex);
		DataGroupForDataRedactorSpy replacedChild = (DataGroupForDataRedactorSpy) replacedChildren
				.get(0);
		return replacedChild;
	}

	private DataGroupForDataRedactorSpy getOriginalChild(int getAllChildrenIndex) {
		List<?> originalChildren = (List<?>) originalDataGroup.MCR
				.getReturnValue("getAllChildrenWithNameInData", getAllChildrenIndex);
		DataGroupForDataRedactorSpy originalChild = (DataGroupForDataRedactorSpy) originalChildren
				.get(0);
		return originalChild;
	}

	private void assertBothChildrenAreSentToMatcherTwice() {
		assertMatcherWasCalledWithCorrectDataForUpdated(0);
		assertMatcherWasCalledWithCorrectDataForOriginal(1);

		// assertEquals(matchFactory.returnedMatchers.size(), 4);
		//
		// assertChildIsSentToMatcherTwice(0, 0, 1);
		// assertChildIsSentToMatcherTwice(2, 1, 2);
	}

	private void assertChildIsSentToMatcherTwice(int matcherIndex, int originalIndex,
			int metadataHolderIndex) {

		MetadataMatchDataSpy matcherOriginal = (MetadataMatchDataSpy) matchFactory.returnedMatchers
				.get(matcherIndex + 1);
		assertSame(matcherOriginal.metadataElement,
				metadataHolder.MCR.getReturnValue("getMetadataElement", metadataHolderIndex));

		List<?> originalChildren = (List<?>) originalDataGroup.MCR
				.getReturnValue("getAllChildrenWithNameInData", originalIndex);
		assertSame(matcherOriginal.dataElement, originalChildren.get(0));

		MetadataMatchDataSpy matcherRedacted = (MetadataMatchDataSpy) matchFactory.returnedMatchers
				.get(matcherIndex);
		assertSame(matcherRedacted.metadataElement,
				metadataHolder.MCR.getReturnValue("getMetadataElement", metadataHolderIndex));

		DataGroupForDataRedactorSpy returnedRedacted = (DataGroupForDataRedactorSpy) groupRedactorMCR
				.getReturnValue("replaceChildrenForConstraintsWithoutPermissions", 0);

		List<?> redactedChildren = (List<?>) returnedRedacted.MCR
				.getReturnValue("getAllChildrenWithNameInData", originalIndex);
		assertSame(matcherRedacted.dataElement, redactedChildren.get(0));
	}

	@Test
	public void testReplaceOneChildNotGroup() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 4);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 0);

	}

	@Test
	public void testReplaceOneChildGroupRepetable() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataTextVariable", "childText", 0, 1);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 0);

	}

	@Test
	public void testReplaceOneChildGroupNoMatchOnNameInData() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		wrapperFactory.nameInDatasToRemove.put("someOtherChildGroupNameInData",
				Collections.emptyList());

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 2);

		groupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
	}

	@Test
	public void testReplaceOneChildGroupMatchOnNameInDataAndNoMatchOnEmptyAttributes() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<DataAttribute> attributes = new ArrayList<>();
		attributes.add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
		List<List<DataAttribute>> listOfAttributes = new ArrayList<>();
		listOfAttributes.add(attributes);
		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", listOfAttributes);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 2);

		groupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
	}

	@Test
	public void testReplaceOneChildGroupMatchOnNameInDataAndMatchOnEmptyAttributes() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<DataAttribute> attributes = new ArrayList<>();
		attributes.add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
		List<List<DataAttribute>> listOfAttributes = new ArrayList<>();
		listOfAttributes.add(attributes);

		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", listOfAttributes);
		List<DataAttribute> emptyAttributes = new ArrayList<>();
		wrapperFactory.nameInDatasToRemove.get("childGroupNameInData").add(emptyAttributes);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 2);

		groupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	}

	@Test
	public void testReplaceOneChildGroupMatchOnNameInDataAndNoMatchAttributes() {

		List<DataAttribute> replacedMetadataAttributes = new ArrayList<>();
		replacedMetadataAttributes
				.add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
		dataGroupRedactorSpy.attributesToReplacedDataGroup = replacedMetadataAttributes;

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<DataAttribute> attributes = new ArrayList<>();
		attributes.add(new DataAttributeSpy("NOTsomeAttributeId", "NOTsomeAttributeValue"));
		List<List<DataAttribute>> listOfAttributes = new ArrayList<>();
		listOfAttributes.add(attributes);

		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", listOfAttributes);
		List<DataAttribute> emptyAttributes = new ArrayList<>();
		wrapperFactory.nameInDatasToRemove.get("childGroupNameInData").add(emptyAttributes);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 2);

		dataGroupRedactorSpy.MCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
	}

	@Test
	public void testReplaceOneChildGroupMatchOnNameInDataAndMatchAttributes() {

		List<DataAttribute> replacedMetadataAttributes = new ArrayList<>();
		replacedMetadataAttributes.add(new DataAttributeSpy("attributeId", "attributeValue"));
		dataGroupRedactorSpy.attributesToReplacedDataGroup = replacedMetadataAttributes;

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<List<DataAttribute>> listOfAttributes = new ArrayList<>();

		List<DataAttribute> attributesList1 = new ArrayList<>();
		attributesList1.add(new DataAttributeSpy("attributeId", "anotherAttributeValue"));
		listOfAttributes.add(attributesList1);

		List<DataAttribute> attributesList2 = new ArrayList<>();
		attributesList2.add(new DataAttributeSpy("attributeId", "attributeValue"));
		listOfAttributes.add(attributesList2);

		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", listOfAttributes);
		List<DataAttribute> emptyAttributes = new ArrayList<>();
		wrapperFactory.nameInDatasToRemove.get("childGroupNameInData").add(emptyAttributes);

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 2);

		dataGroupRedactorSpy.MCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	}

	@Test
	public void testReplacedNotRecursivlyCalledForChildGroupWithoutData() {

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<Boolean> isValidList = new ArrayList<>();
		isValidList.add(false);
		isValidList.add(true);
		matchFactory.isValidList = isValidList;

		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", Collections.emptyList());

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 1);

		dataGroupRedactorSpy.MCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	}

	@Test
	public void testReplaceCallRedirectedToRemoveForChildGroupWithNoDataInOriginal() {

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<Boolean> isValidList = new ArrayList<>();
		isValidList.add(true);
		isValidList.add(false);
		matchFactory.isValidList = isValidList;

		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", Collections.emptyList());

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matcherFactory.returnedMatchers.size(), 2);

		dataGroupRedactorSpy.MCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
		dataGroupRedactorSpy.MCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 1);
	}

}
