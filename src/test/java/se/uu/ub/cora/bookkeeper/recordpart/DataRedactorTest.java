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
	// private Set<String> titlePermissions;
	private DataGroupForDataRedactorSpy originalDataGroup;
	private DataGroupForDataRedactorSpy updatedDataGroup;
	private DataGroupRedactorSpy dataGroupRedactorSpy;
	private MethodCallRecorder groupRedactorMCR;
	private MetadataHolderSpy metadataHolder;

	private String metadataId = "someMetadataId";

	private MetadataMatchFactorySpy matchFactory;

	private DataGroupWrapperFactorySpy wrapperFactory;

	@BeforeMethod
	public void setUp() {
		metadataHolder = new MetadataHolderSpy();
		dataGroupRedactorSpy = new DataGroupRedactorSpy();
		groupRedactorMCR = dataGroupRedactorSpy.MCR;
		matchFactory = new MetadataMatchFactorySpy();
		wrapperFactory = new DataGroupWrapperFactorySpy();
		dataRedactor = new DataRedactorImp(metadataHolder, dataGroupRedactorSpy, matchFactory,
				wrapperFactory);
		topDataGroupSpy = new DataGroupForDataRedactorSpy("someDataGroup");
		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		titleConstraints = createReadConstraintForTitle();
		// titlePermissions = createReadPermissionForTitle();
		originalDataGroup = new DataGroupForDataRedactorSpy("originalDataGroup");
		updatedDataGroup = new DataGroupForDataRedactorSpy("changedDataGroup");
	}

	private Set<Constraint> createReadConstraintForTitle() {
		Set<Constraint> recordPartConstraints = new HashSet<>();
		Constraint constraint = new Constraint("title");
		recordPartConstraints.add(constraint);
		return recordPartConstraints;
	}

	// private Set<String> createReadPermissionForTitle() {
	// Set<String> recordPartPermissions = new HashSet<>();
	// recordPartPermissions.add("title");
	// return recordPartPermissions;
	// }

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
	public void testReplaceWhenNoConstraints() throws Exception {
		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, emptyConstraints,
				emptyPermissions);
		assertSame(replacedDataGroup, originalDataGroup);
		dataGroupRedactorSpy.MCR
				.assertMethodNotCalled("replaceChildrenForConstraintsWithoutPermissions");
	}

	// @Test
	// public void testReplaceWhenSomeConstraint() throws Exception {
	// MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
	// createAndAddChildDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
	// createAndAddChildDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);
	//
	// DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
	// metadataId, originalDataGroup, updatedDataGroup, titleConstraints,
	// titlePermissions);
	//
	// dataGroupRedactorSpy.MCR.assertReturn("replaceChildrenForConstraintsWithoutPermissions", 0,
	// replacedDataGroup);
	//
	// dataGroupRedactorSpy.MCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions",
	// 0, originalDataGroup, updatedDataGroup, titleConstraints, titlePermissions);
	// }

	@Test
	public void testReplaceTwoGroupChildrenNoneRemovedFromTop() {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);

		DataGroupForDataRedactorSpy filteredDataGroup = (DataGroupForDataRedactorSpy) dataRedactor
				.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
						updatedDataGroup, titleConstraints, emptyPermissions);

		DataGroupWrapper wrapper = wrapperFactory.factoredWrappers.get(0);
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

		DataGroupWrapper wrapper = (DataGroupWrapper) parametersForMethodAndCallNumber
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
		assertEquals(matchFactory.returnedMatchers.size(), 4);

		assertChildIsSentToMatcherTwice(0, 0, 1);
		assertChildIsSentToMatcherTwice(2, 1, 2);
	}

	private void assertChildIsSentToMatcherTwice(int matcherIndex, int originalIndex,
			int metadataHolderIndex) {
		MetadataMatchDataSpy matcherOriginal = (MetadataMatchDataSpy) matchFactory.returnedMatchers
				.get(matcherIndex);
		assertSame(matcherOriginal.metadataElement,
				metadataHolder.MCR.getReturnValue("getMetadataElement", metadataHolderIndex));

		List<?> originalChildren = (List<?>) originalDataGroup.MCR
				.getReturnValue("getAllChildrenWithNameInData", originalIndex);
		assertSame(matcherOriginal.dataElement, originalChildren.get(0));

		MetadataMatchDataSpy matcherRedacted = (MetadataMatchDataSpy) matchFactory.returnedMatchers
				.get(matcherIndex + 1);
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

		// We fill replacedNamesInData with one list, it should not match therefore it can be
		// replaced
		List<DataAttribute> attributes = new ArrayList<>();
		attributes.add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", attributes);

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

		// We fill replacedNamesInData with two list, one of them is empty, which should match with
		// the empty child.
		List<DataAttribute> attributes = new ArrayList<>();
		attributes.add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", attributes);
		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", Collections.emptyList());

		dataRedactor.replaceChildrenForConstraintsWithoutPermissions(metadataId, originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 2);

		groupRedactorMCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	}

	@Test
	public void testReplaceOneChildGroupMatchOnNameInDataAndNoMatchAttributes() {
		DataGroupRedactorSpy dataGroupRedactorSpyWithAttributes = new DataGroupRedactorSpy();
		// Här vill jag kunna sätta nya attributter till metadataGroup som inte matchar. Vet inte
		// riktig hur, gör jag det
		List<DataAttribute> replacedMetadataAttributes = new ArrayList<>();
		replacedMetadataAttributes
				.add(new DataAttributeSpy("someAttributeId", "someAttributeValue"));
		dataGroupRedactorSpyWithAttributes.attributesToReplacedDataGroup = replacedMetadataAttributes;
		DataRedactorImp dataRedactorForReplacedAttributes = new DataRedactorImp(metadataHolder,
				dataGroupRedactorSpyWithAttributes, matchFactory, wrapperFactory);

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<DataAttribute> attributes = new ArrayList<>();
		attributes.add(new DataAttributeSpy("AttributeId", "AttributeValue"));
		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", attributes);
		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", Collections.emptyList());

		dataRedactorForReplacedAttributes.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, titleConstraints,
				emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 2);

		dataGroupRedactorSpyWithAttributes.MCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 2);
	}

	@Test
	public void testReplaceOneChildGroupMatchOnNameInDataAndMatchAttributes() {
		DataGroupRedactorSpy dataGroupRedactorSpyWithAttributes = new DataGroupRedactorSpy();
		// Här vill jag kunna sätta nya attributter till metadataGroup som matchar. Vet inte riktig
		// hur, gör jag det
		List<DataAttribute> replacedMetadataAttributes = new ArrayList<>();
		replacedMetadataAttributes.add(new DataAttributeSpy("AttributeId", "AttributeValue"));
		dataGroupRedactorSpyWithAttributes.attributesToReplacedDataGroup = replacedMetadataAttributes;
		DataRedactorImp dataRedactorForReplacedAttributes = new DataRedactorImp(metadataHolder,
				dataGroupRedactorSpyWithAttributes, matchFactory, wrapperFactory);

		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childGroup", 0, 1);

		List<DataAttribute> attributes = new ArrayList<>();
		attributes.add(new DataAttributeSpy("AttributeId", "AttributeValue"));
		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", attributes);
		wrapperFactory.nameInDatasToRemove.put("childGroupNameInData", Collections.emptyList());

		dataRedactorForReplacedAttributes.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, titleConstraints,
				emptyPermissions);

		assertEquals(matchFactory.returnedMatchers.size(), 2);

		dataGroupRedactorSpyWithAttributes.MCR
				.assertNumberOfCallsToMethod("replaceChildrenForConstraintsWithoutPermissions", 1);
	}

}
