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

import static org.testng.Assert.assertSame;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.metadata.ConstraintType;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.spy.MethodCallRecorder;
import se.uu.ub.cora.data.DataGroup;

public class DataRedactorTest {
	private DataRedactor dataRedactor;

	private DataGroupForDataRedactorSpy topDataGroupSpy;
	private Set<Constraint> emptyConstraints;
	private Set<String> emptyPermissions;
	private Set<Constraint> titleConstraints;
	private Set<String> titlePermissions;
	private DataGroupSpy originalDataGroup;
	private DataGroupSpy updatedDataGroup;
	private DataGroupRedactorSpy dataGroupRedactorSpy;
	private MethodCallRecorder groupRedactorMCR;
	private MetadataHolderSpy metadataHolder;

	private String metadataId = "someMetadataId";

	@BeforeMethod
	public void setUp() {
		metadataHolder = new MetadataHolderSpy();
		dataGroupRedactorSpy = new DataGroupRedactorSpy();
		groupRedactorMCR = dataGroupRedactorSpy.MCR;
		dataRedactor = new DataRedactorImp(metadataHolder, dataGroupRedactorSpy);
		topDataGroupSpy = new DataGroupForDataRedactorSpy("someDataGroup");
		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		titleConstraints = createReadConstraintForTitle();
		titlePermissions = createReadPermissionForTitle();
		originalDataGroup = new DataGroupSpy("originalDataGroup");
		updatedDataGroup = new DataGroupSpy("changedDataGroup");
	}

	private Set<Constraint> createReadConstraintForTitle() {
		Set<Constraint> recordPartConstraints = new HashSet<>();
		Constraint constraint = new Constraint("title");
		recordPartConstraints.add(constraint);
		return recordPartConstraints;
	}

	private Set<String> createReadPermissionForTitle() {
		Set<String> recordPartPermissions = new HashSet<>();
		recordPartPermissions.add("title");
		return recordPartPermissions;
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
	public void testReplaceWhenNoConstraints() throws Exception {
		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, emptyConstraints,
				emptyPermissions);
		assertSame(replacedDataGroup, originalDataGroup);
		dataGroupRedactorSpy.MCR
				.assertMethodNotCalled("replaceChildrenForConstraintsWithoutPermissions");
	}

	@Test
	public void testReplaceWhenSomeConstraint() throws Exception {
		MetadataGroupSpy topGroup = createAndAddTopGroup(metadataId);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "childDataGroup", 0, 1);
		createAndAddChildDataGroup(topGroup, "metadataGroup", "recordInfo", 0, 1);

		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, titleConstraints,
				titlePermissions);

		dataGroupRedactorSpy.MCR.assertReturn("replaceChildrenForConstraintsWithoutPermissions", 0,
				replacedDataGroup);

		dataGroupRedactorSpy.MCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions",
				0, originalDataGroup, updatedDataGroup, titleConstraints, titlePermissions);
	}

}
