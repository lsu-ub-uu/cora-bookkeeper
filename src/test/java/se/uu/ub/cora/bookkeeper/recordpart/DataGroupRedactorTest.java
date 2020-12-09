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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAttributeSpy;
import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupRedactorTest {
	private DataGroupRedactor dataGroupRedactor;
	private DataGroupForDataRedactorSpy dataGroupSpy;
	private Set<Constraint> emptyConstraints;
	private Set<String> emptyPermissions;
	private Set<Constraint> titleConstraints;
	private Set<String> titlePermissions;
	private DataGroupForDataRedactorSpy originalDataGroup;
	private DataGroupForDataRedactorSpy updatedDataGroup;

	@BeforeMethod
	public void setUp() {
		dataGroupRedactor = new DataGroupRedactorImp();
		dataGroupSpy = new DataGroupForDataRedactorSpy("someDataGroup");
		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		titleConstraints = createReadConstraintForTitle();
		titlePermissions = createReadPermissionForTitle();
		originalDataGroup = new DataGroupForDataRedactorSpy("originalDataGroup");
		updatedDataGroup = new DataGroupForDataRedactorSpy("changedDataGroup");
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
	public void testRemoveNoConstraintsNoPermissions() throws Exception {
		DataGroup filteredDataGroup = dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(
				dataGroupSpy, emptyConstraints, emptyPermissions);

		assertSame(filteredDataGroup, dataGroupSpy);
		assertFalse(dataGroupSpy.removeAllChildrenWithAttributeWasCalled);
	}

	@Test
	public void testRemoveNoConstraintsButPermissions() throws Exception {
		DataGroup filteredDataGroup = dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(
				dataGroupSpy, emptyConstraints, titlePermissions);

		assertSame(filteredDataGroup, dataGroupSpy);
		assertFalse(dataGroupSpy.removeAllChildrenWithAttributeWasCalled);
	}

	@Test
	public void testRemoveConstraintsAndPermissions() throws Exception {
		DataGroup filteredDataGroup = dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(
				dataGroupSpy, titleConstraints, titlePermissions);

		assertSame(filteredDataGroup, dataGroupSpy);
		assertFalse(dataGroupSpy.removeAllChildrenWithAttributeWasCalled);
	}

	@Test
	public void testRemoveNoChildToRemove() throws Exception {
		dataGroupSpy.childExists = false;

		dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy, titleConstraints,
				emptyPermissions);

		assertTrue(dataGroupSpy.removeAllChildrenWithAttributeWasCalled);
		assertEquals(dataGroupSpy.childNameInDataWithAttributesToRemove, "title");
	}

	@Test
	public void testRemoveConstraintsNoPermissions() throws Exception {
		dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy, titleConstraints,
				emptyPermissions);

		assertTrue(dataGroupSpy.removeAllChildrenWithAttributeWasCalled);
		assertEquals(dataGroupSpy.childNameInDataWithAttributesToRemove, "title");
	}

	@Test
	public void testRemoveMultipleConstraintsNoPermissions() throws Exception {
		titleConstraints.add(new Constraint("otherConstraint"));

		dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy, titleConstraints,
				emptyPermissions);

		assertTrue(dataGroupSpy.removeAllChildrenWithAttributeWasCalled);
		List<String> childNamesInDataToRemoveAll = dataGroupSpy.childNamesInDataWithAttributesToRemoveAll;
		assertTrue(childNamesInDataToRemoveAll.contains("title"));
		assertTrue(childNamesInDataToRemoveAll.contains("otherConstraint"));

		assertTrue(dataGroupSpy.usedAttributesForRemove.isEmpty());
	}

	@Test
	public void testRemoveConstraintsWithAttributesAndPermissions() throws Exception {
		Set<Constraint> constraints = createSetWithOneConstraintOneAttribute();

		DataGroup filteredDataGroup = dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(
				dataGroupSpy, constraints, titlePermissions);

		assertSame(filteredDataGroup, dataGroupSpy);
		assertFalse(dataGroupSpy.containsChildWithNameInDataWasCalled);
		assertFalse(dataGroupSpy.removeAllChildrenWithAttributeWasCalled);
	}

	private Set<Constraint> createSetWithOneConstraintOneAttribute() {
		Set<Constraint> recordPartConstraints = new HashSet<>();
		Constraint constraint = new Constraint("title");
		constraint.addAttribute(new DataAttributeSpy("someId", "someValue"));
		recordPartConstraints.add(constraint);
		return recordPartConstraints;
	}

	@Test
	public void testRemoveConstraintsWithAttributesNoPermissions() throws Exception {
		Set<Constraint> constraints = createSetWithOneConstraintOneAttribute();
		dataGroupRedactor.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy, constraints,
				emptyPermissions);

		assertTrue(dataGroupSpy.removeAllChildrenWithAttributeWasCalled);
		assertTrue(dataGroupSpy.usedAttributesForRemove.containsKey("someId"));
		assertEquals(dataGroupSpy.childNameInDataWithAttributesToRemove, "title");
	}

	@Test
	public void testReplaceNoContrainsNoPermissions() throws Exception {
		DataGroup replacedDataGroup = dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(
				originalDataGroup, updatedDataGroup, emptyConstraints, emptyPermissions);

		assertSame(replacedDataGroup, updatedDataGroup);
		assertFalse(originalDataGroup.getAllChildrenWithNameInDataAndAttributesWasCalled);
		assertFalse(updatedDataGroup.removeAllChildrenWithAttributeWasCalled);
	}

	@Test
	public void testReplaceNoConstraintsButPermissions() throws Exception {
		DataGroup replacedDataGroup = dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(
				originalDataGroup, updatedDataGroup, emptyConstraints, titlePermissions);

		assertSame(replacedDataGroup, updatedDataGroup);
		assertFalse(originalDataGroup.getAllChildrenWithNameInDataAndAttributesWasCalled);
		assertFalse(updatedDataGroup.removeAllChildrenWithAttributeWasCalled);
	}

	@Test
	public void testReplaceConstraintsWithMatchingPermissions() throws Exception {
		DataGroup replacedDataGroup = dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(
				originalDataGroup, updatedDataGroup, titleConstraints, titlePermissions);

		assertSame(replacedDataGroup, updatedDataGroup);
		assertFalse(originalDataGroup.getAllChildrenWithNameInDataAndAttributesWasCalled);
		assertFalse(updatedDataGroup.removeAllChildrenWithAttributeWasCalled);
	}

	@Test
	public void testReplaceMultipleConstraintsMatchingMultiplePermissions() throws Exception {
		titleConstraints.add(new Constraint("otherConstraint"));

		dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, titlePermissions);

		assertTrue(updatedDataGroup.removeAllChildrenWithAttributeWasCalled);
		assertTrue(originalDataGroup.getAllChildrenWithNameInDataAndAttributesWasCalled);

		assertTrue(updatedDataGroup.childNamesInDataWithAttributesToRemoveAll
				.contains("otherConstraint"));
		assertFalse(updatedDataGroup.childNamesInDataWithAttributesToRemoveAll.contains("title"));

		assertSame(updatedDataGroup.addedChildrenCollections.iterator().next(),
				originalDataGroup.getAllChildrenWithNameInDataAndAttributes("otherConstraint"));
	}

	@Test
	public void testReplaceConstraintsEmptyPermissions() throws Exception {
		dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(updatedDataGroup.childNameInDataWithAttributesToRemove, "title");
		assertSame(updatedDataGroup.addedChildrenCollections.iterator().next(),
				originalDataGroup.getAllChildrenWithNameInData("title"));
		assertTrue(originalDataGroup.getAllChildrenWithNameInDataAndAttributesWasCalled);

	}

	@Test
	public void testReplaceConstraintsWithAttributeEmptyPermissions() throws Exception {
		Set<Constraint> constraints = createSetWithOneConstraintOneAttribute();
		dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, constraints, emptyPermissions);

		assertTrue(originalDataGroup.getAllChildrenWithNameInDataAndAttributesWasCalled);

		assertEquals(updatedDataGroup.childNameInDataWithAttributesToRemove, "title");
		assertSame(updatedDataGroup.addedChildrenCollections.iterator().next(),
				originalDataGroup.getAllChildrenWithNameInData("title"));

		assertTrue(updatedDataGroup.usedAttributesForRemove.containsKey("someId"));
	}

	@Test
	public void testReplaceMultipleConstraintsNoPermissions() throws Exception {
		titleConstraints.add(new Constraint("otherConstraint"));

		dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		System.out.println(
				"size " + updatedDataGroup.childNamesInDataWithAttributesToRemoveAll.size());

		assertTrue(originalDataGroup.getAllChildrenWithNameInDataAndAttributesWasCalled);

		assertTrue(updatedDataGroup.childNamesInDataWithAttributesToRemoveAll.contains("title"));
		assertTrue(updatedDataGroup.childNamesInDataWithAttributesToRemoveAll
				.contains("otherConstraint"));

		List<Collection<DataElement>> addedChildrenCollections = updatedDataGroup.addedChildrenCollections;
		Collection<DataElement> titleCollection = getCollectionContainingChildrenWithNameInData(
				"title", addedChildrenCollections);
		assertSame(titleCollection, originalDataGroup.getAllChildrenWithNameInData("title"));

		Collection<DataElement> otherConstraintCollection = getCollectionContainingChildrenWithNameInData(
				"otherConstraint", addedChildrenCollections);
		assertSame(otherConstraintCollection,
				originalDataGroup.getAllChildrenWithNameInData("otherConstraint"));
	}

	private Collection<DataElement> getCollectionContainingChildrenWithNameInData(String nameInData,
			List<Collection<DataElement>> addedChildrenCollections) {
		for (Collection<DataElement> collection : addedChildrenCollections) {
			for (DataElement dataElement : collection) {
				if (nameInData.equals(dataElement.getNameInData())) {
					return collection;
				}
			}
		}
		return null;
	}

	@Test
	public void testReplaceNoChildToRemove() throws Exception {
		updatedDataGroup.childExists = false;
		dataGroupRedactor.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertTrue(updatedDataGroup.removeAllChildrenWithAttributeWasCalled);
		assertTrue(updatedDataGroup.childNamesInDataWithAttributesToRemoveAll.contains("title"));
		assertTrue(originalDataGroup.getAllChildrenWithNameInDataAndAttributesWasCalled);

	}
}
