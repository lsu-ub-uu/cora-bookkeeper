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
	private MetadataHolderSpy metadataHolder;

	@BeforeMethod
	public void setUp() {
		metadataHolder = new MetadataHolderSpy();
		dataGroupRedactorSpy = new DataGroupRedactorSpy();
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
	public void testRemoveSomeConstraint() throws Exception {
		String metadataId = "someMetadataId";

		createMetadataForMetadataHolder(metadataId);

		DataGroup filteredDataGroup = dataRedactor.removeChildrenForConstraintsWithoutPermissions(
				metadataId, topDataGroupSpy, titleConstraints, emptyPermissions);
		dataGroupRedactorSpy.MCR.assertReturn("removeChildrenForConstraintsWithoutPermissions", 0,
				filteredDataGroup);
		metadataHolder.MCR.assertParameters("getMetadataElement", 0, metadataId);

		// run1 (topDataGroup)
		dataGroupRedactorSpy.MCR.assertParameters("removeChildrenForConstraintsWithoutPermissions",
				0, topDataGroupSpy, titleConstraints, emptyPermissions);
		DataGroupForDataRedactorSpy firstRedactedG = (DataGroupForDataRedactorSpy) dataGroupRedactorSpy.MCR
				.getReturnValue("removeChildrenForConstraintsWithoutPermissions", 0);
		// into loop (topDataGroup)
		metadataHolder.MCR.assertParameters("getMetadataElement", 1, "childDataGroup");
		DataGroupForDataRedactorSpy firstChildGroup = (DataGroupForDataRedactorSpy) firstRedactedG.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);

		// run2 (childDataGroup)
		dataGroupRedactorSpy.MCR.assertParameters("removeChildrenForConstraintsWithoutPermissions",
				1, firstChildGroup, titleConstraints, emptyPermissions);
		DataGroupForDataRedactorSpy secondRedactedG = (DataGroupForDataRedactorSpy) dataGroupRedactorSpy.MCR
				.getReturnValue("removeChildrenForConstraintsWithoutPermissions", 1);
		// into loop

		// continue loop (topDataGroup)
		DataGroupForDataRedactorSpy secondChildGroup = (DataGroupForDataRedactorSpy) firstRedactedG.MCR
				.getReturnValue("getFirstGroupWithNameInData", 1);
		metadataHolder.MCR.assertParameters("getMetadataElement", 2, "childDataGroup2");

		// run3 (childDataGroup2)
		dataGroupRedactorSpy.MCR.assertParameters("removeChildrenForConstraintsWithoutPermissions",
				2, secondChildGroup, titleConstraints, emptyPermissions);
		DataGroupForDataRedactorSpy thirdRedactedG = (DataGroupForDataRedactorSpy) dataGroupRedactorSpy.MCR
				.getReturnValue("removeChildrenForConstraintsWithoutPermissions", 2);
		// into loop (dataGroup2 child1)
		metadataHolder.MCR.assertParameters("getMetadataElement", 3, "dataDivider");
		DataGroupForDataRedactorSpy dataDividerGroup = (DataGroupForDataRedactorSpy) thirdRedactedG.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);

		// run4 (DataDivderGroup)
		dataGroupRedactorSpy.MCR.assertParameters("removeChildrenForConstraintsWithoutPermissions",
				3, dataDividerGroup, titleConstraints, emptyPermissions);
		// // into loop

		// continue loop (dataGroup2 child2)
		metadataHolder.MCR.assertParameters("getMetadataElement", 4, "type");
		thirdRedactedG.MCR.assertMethodWasCalled("containsChildWithNameInData");
		thirdRedactedG.MCR.assertNumberOfCallsToMethod("getFirstGroupWithNameInData", 1);
		// end checks
		metadataHolder.MCR.assertNumberOfCallsToMethod("getMetadataElement", 5);
		dataGroupRedactorSpy.MCR
				.assertNumberOfCallsToMethod("removeChildrenForConstraintsWithoutPermissions", 4);

	}

	private void createMetadataForMetadataHolder(String metadataId) {
		MetadataGroupSpy topGroup = new MetadataGroupSpy(metadataId, "someNameInData");
		topGroup.createChildReference("metadataGroup", "childDataGroup", 0, 1,
				ConstraintType.WRITE);

		topGroup.createChildReference("metadataGroup", "childDataGroup2", 0, 1);
		topGroup.createChildReference("metadataGroup", "childDataGroup3", 0, 3);
		topGroup.createChildReference("metadataTextVariable", "childTextVar", 0, 1);
		topGroup.createChildReference("metadataNumberVariable", "childNumVar", 0, 1,
				ConstraintType.WRITE);
		metadataHolder.elementsToReturn.put(metadataId, topGroup);

		MetadataGroupSpy metadataChild = new MetadataGroupSpy("childDataGroup",
				"childDataGroupNameInData");
		metadataHolder.elementsToReturn.put("childDataGroup", metadataChild);

		MetadataGroupSpy metadataChild2 = new MetadataGroupSpy("childDataGroup2",
				"childDataGroup2NameInData");
		metadataChild2.createChildReference("metadataGroup", "dataDivider", 0, 1);
		metadataChild2.createChildReference("metadataGroup", "type", 0, 1);
		metadataHolder.elementsToReturn.put("childDataGroup2", metadataChild2);

		MetadataGroupSpy dataDivider = new MetadataGroupSpy("dataDivider", "dataDivider");
		metadataHolder.elementsToReturn.put("dataDivider", dataDivider);
		MetadataGroupSpy type = new MetadataGroupSpy("type", "type");
		metadataHolder.elementsToReturn.put("type", type);

	}

	@Test
	public void testReplaceWhenNoConstraints() throws Exception {
		String metadataId = "";
		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, emptyConstraints,
				emptyPermissions);
		assertSame(replacedDataGroup, originalDataGroup);
		dataGroupRedactorSpy.MCR
				.assertMethodNotCalled("replaceChildrenForConstraintsWithoutPermissions");
	}

	@Test
	public void testReplaceWhenSomeConstraint() throws Exception {
		String metadataId = "";
		DataGroup replacedDataGroup = dataRedactor.replaceChildrenForConstraintsWithoutPermissions(
				metadataId, originalDataGroup, updatedDataGroup, titleConstraints,
				titlePermissions);

		dataGroupRedactorSpy.MCR.assertReturn("replaceChildrenForConstraintsWithoutPermissions", 0,
				replacedDataGroup);

		dataGroupRedactorSpy.MCR.assertParameters("replaceChildrenForConstraintsWithoutPermissions",
				0, originalDataGroup, updatedDataGroup, titleConstraints, titlePermissions);
	}

}
