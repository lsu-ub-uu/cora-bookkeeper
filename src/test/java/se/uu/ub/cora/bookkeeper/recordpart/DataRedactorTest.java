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

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class DataRedactorTest {

	private DataGroupForRecordPartFilterSpy dataGroupSpy;
	private DataRedactor recordPartFilter;
	private Set<Constraint> emptyConstraints;
	private Set<String> emptyPermissions;
	private Set<Constraint> titleConstraints;
	private Set<String> titlePermissions;
	private DataGroupForRecordPartFilterSpy originalDataGroup;
	private DataGroupForRecordPartFilterSpy updatedDataGroup;

	@BeforeMethod
	public void setUp() {
		dataGroupSpy = new DataGroupForRecordPartFilterSpy("someDataGroup");
		recordPartFilter = new DataRedactorImp();
		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		titleConstraints = createReadConstraintForTitle();
		titlePermissions = createReadPermissionForTitle();
		originalDataGroup = new DataGroupForRecordPartFilterSpy("originalDataGroup");
		updatedDataGroup = new DataGroupForRecordPartFilterSpy("changedDataGroup");
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
		DataGroup filteredDataGroup = recordPartFilter
				.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy, emptyConstraints,
						emptyPermissions);

		assertSame(filteredDataGroup, dataGroupSpy);
		assertFalse(dataGroupSpy.containsChildWithNameInDataWasCalled);
		assertFalse(dataGroupSpy.removeAllChildrenWasCalled);
	}

	@Test
	public void testRemoveNoConstraintsButPermissions() throws Exception {
		DataGroup filteredDataGroup = recordPartFilter
				.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy, emptyConstraints,
						titlePermissions);

		assertSame(filteredDataGroup, dataGroupSpy);
		assertFalse(dataGroupSpy.containsChildWithNameInDataWasCalled);
		assertFalse(dataGroupSpy.removeAllChildrenWasCalled);
	}

	@Test
	public void testRemoveConstraintsAndPermissions() throws Exception {
		DataGroup filteredDataGroup = recordPartFilter
				.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy, titleConstraints,
						titlePermissions);

		assertSame(filteredDataGroup, dataGroupSpy);
		assertFalse(dataGroupSpy.containsChildWithNameInDataWasCalled);
		assertFalse(dataGroupSpy.removeAllChildrenWasCalled);
	}

	@Test
	public void testRemoveNoChildToRemove() throws Exception {
		dataGroupSpy.childExists = false;

		recordPartFilter.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy,
				titleConstraints, emptyPermissions);

		assertTrue(dataGroupSpy.removeAllChildrenWasCalled);
		assertEquals(dataGroupSpy.childNameInDataToRemove, "title");
	}

	@Test
	public void testRemoveConstraintsNoPermissions() throws Exception {
		recordPartFilter.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy,
				titleConstraints, emptyPermissions);

		assertTrue(dataGroupSpy.removeAllChildrenWasCalled);
		assertEquals(dataGroupSpy.childNameInDataToRemove, "title");
	}

	@Test
	public void testRemoveMultipleConstraintsNoPermissions() throws Exception {
		titleConstraints.add(new Constraint("otherConstraint"));

		recordPartFilter.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy,
				titleConstraints, emptyPermissions);

		assertTrue(dataGroupSpy.removeAllChildrenWasCalled);
		List<String> childNamesInDataToRemoveAll = dataGroupSpy.childNamesInDataToRemoveAll;
		assertTrue(childNamesInDataToRemoveAll.contains("title"));
		assertTrue(childNamesInDataToRemoveAll.contains("otherConstraint"));
	}

	@Test
	public void testReplaceNoContrainsNoPermissions() throws Exception {
		DataGroup replacedDataGroup = recordPartFilter
				.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
						updatedDataGroup, emptyConstraints, emptyPermissions);

		assertSame(replacedDataGroup, updatedDataGroup);
		assertFalse(updatedDataGroup.containsChildWithNameInDataWasCalled);
		assertFalse(updatedDataGroup.removeAllChildrenWasCalled);
	}

	@Test
	public void testReplaceNoConstraintsButPermissions() throws Exception {
		DataGroup replacedDataGroup = recordPartFilter
				.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
						updatedDataGroup, emptyConstraints, titlePermissions);

		assertSame(replacedDataGroup, updatedDataGroup);
		assertFalse(updatedDataGroup.containsChildWithNameInDataWasCalled);
		assertFalse(updatedDataGroup.removeAllChildrenWasCalled);
	}

	@Test
	public void testReplaceConstraintsWithMatchingPermissions() throws Exception {
		DataGroup replacedDataGroup = recordPartFilter
				.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
						updatedDataGroup, titleConstraints, titlePermissions);

		assertSame(replacedDataGroup, updatedDataGroup);
		assertFalse(updatedDataGroup.containsChildWithNameInDataWasCalled);
		assertFalse(updatedDataGroup.removeAllChildrenWasCalled);
	}

	@Test
	public void testReplaceMultipleConstraintsMatchingMultiplePermissions() throws Exception {
		titleConstraints.add(new Constraint("otherConstraint"));

		recordPartFilter.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, titlePermissions);

		assertTrue(updatedDataGroup.removeAllChildrenWasCalled);
		assertTrue(updatedDataGroup.childNamesInDataToRemoveAll.contains("otherConstraint"));
		assertFalse(updatedDataGroup.childNamesInDataToRemoveAll.contains("title"));

		assertSame(updatedDataGroup.addedChildrenCollections.iterator().next(),
				originalDataGroup.getAllChildrenWithNameInData("otherConstraint"));
	}

	@Test
	public void testReplaceConstraintsEmptyPermissions() throws Exception {
		recordPartFilter.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(updatedDataGroup.childNameInDataToRemove, "title");
		assertSame(updatedDataGroup.addedChildrenCollections.iterator().next(),
				originalDataGroup.getAllChildrenWithNameInData("title"));

	}

	@Test
	public void testReplaceMultipleConstraintsNoPermissions() throws Exception {
		titleConstraints.add(new Constraint("otherConstraint"));

		recordPartFilter.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		System.out.println("size " + updatedDataGroup.childNamesInDataToRemoveAll.size());

		assertTrue(updatedDataGroup.childNamesInDataToRemoveAll.contains("title"));
		assertTrue(updatedDataGroup.childNamesInDataToRemoveAll.contains("otherConstraint"));

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

		recordPartFilter.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertTrue(updatedDataGroup.removeAllChildrenWasCalled);
		assertTrue(updatedDataGroup.childNamesInDataToRemoveAll.contains("title"));
	}
}
