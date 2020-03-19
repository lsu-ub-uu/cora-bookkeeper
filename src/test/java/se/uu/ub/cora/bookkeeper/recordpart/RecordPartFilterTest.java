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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class RecordPartFilterTest {

	private DataGroupForRecordPartFilterSpy dataGroupSpy;
	private RecordPartFilter recordPartFilter;
	private Set<String> emptyConstraints;
	private Set<String> emptyPermissions;
	private Set<String> titleConstraints;
	private Set<String> titlePermissions;
	private DataGroupForRecordPartFilterSpy originalDataGroup;
	private DataGroupForRecordPartFilterSpy updatedDataGroup;

	@BeforeMethod
	public void setUp() {
		dataGroupSpy = new DataGroupForRecordPartFilterSpy("someDataGroup");
		recordPartFilter = new RecordPartFilterImp();
		emptyConstraints = Collections.emptySet();
		emptyPermissions = Collections.emptySet();
		titleConstraints = createReadConstraintForTitle();
		titlePermissions = createReadPermissionForTitle();
		originalDataGroup = new DataGroupForRecordPartFilterSpy("originalDataGroup");
		updatedDataGroup = new DataGroupForRecordPartFilterSpy("changedDataGroup");
	}

	private Set<String> createReadConstraintForTitle() {
		Set<String> recordPartConstraints = new HashSet<>();
		recordPartConstraints.add("title");
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

		assertTrue(dataGroupSpy.containsChildWithNameInDataWasCalled);
		assertFalse(dataGroupSpy.removeAllChildrenWasCalled);
	}

	@Test
	public void testRemoveConstraintsNoPermissions() throws Exception {
		recordPartFilter.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy,
				titleConstraints, emptyPermissions);

		assertTrue(dataGroupSpy.containsChildWithNameInDataWasCalled);
		assertTrue(dataGroupSpy.removeAllChildrenWasCalled);
		assertEquals(dataGroupSpy.childNameInDataToRemove, "title");
	}

	@Test
	public void testRemoveMultipleConstraintsNoPermissions() throws Exception {
		titleConstraints.add("otherConstraint");

		recordPartFilter.removeChildrenForConstraintsWithoutPermissions(dataGroupSpy,
				titleConstraints, emptyPermissions);

		assertTrue(dataGroupSpy.containsChildWithNameInDataWasCalled);
		assertTrue(dataGroupSpy.removeAllChildrenWasCalled);
		List<String> childNamesInDataToRemoveAll = dataGroupSpy.childNamesInDataToRemoveAll;
		assertEquals(childNamesInDataToRemoveAll.get(0), "title");
		assertEquals(childNamesInDataToRemoveAll.get(1), "otherConstraint");
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
		titleConstraints.add("otherConstraint");

		recordPartFilter.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, titlePermissions);

		assertTrue(updatedDataGroup.containsChildWithNameInDataWasCalled);
		assertTrue(updatedDataGroup.removeAllChildrenWasCalled);
		assertTrue(updatedDataGroup.childNamesInDataToRemoveAll.contains("otherConstraint"));
		assertFalse(updatedDataGroup.childNamesInDataToRemoveAll.contains("title"));
		assertTrue(updatedDataGroup.nameInDatasContainsChildWithNameInData
				.contains("otherConstraint"));
		assertFalse(updatedDataGroup.nameInDatasContainsChildWithNameInData.contains("title"));

		assertSame(updatedDataGroup.addedChildrenCollections.iterator().next(),
				originalDataGroup.getAllChildrenWithNameInData("otherConstraint"));
	}

	@Test
	public void testReplaceConstraintsEmptyPermissions() throws Exception {
		recordPartFilter.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		// assertSame(replacedDataGroup, updatedDataGroup);
		assertTrue(updatedDataGroup.containsChildWithNameInDataWasCalled);
		assertEquals(updatedDataGroup.nameInDatasContainsChildWithNameInData.get(0), "title");
		assertEquals(updatedDataGroup.childNameInDataToRemove, "title");
		assertSame(updatedDataGroup.addedChildrenCollections.iterator().next(),
				originalDataGroup.getAllChildrenWithNameInData("title"));
		// assertEquals(updatedDataGroup.addChildNameInDatas(), "title");

	}

	@Test
	public void testReplaceMultipleConstraintsNoPermissions() throws Exception {
		titleConstraints.add("otherConstraint");

		recordPartFilter.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertEquals(updatedDataGroup.nameInDatasContainsChildWithNameInData.get(0), "title");
		assertEquals(updatedDataGroup.nameInDatasContainsChildWithNameInData.get(1),
				"otherConstraint");
		assertEquals(updatedDataGroup.childNamesInDataToRemoveAll.get(0), "title");
		assertEquals(updatedDataGroup.childNamesInDataToRemoveAll.get(1), "otherConstraint");

		Iterator<Collection<DataElement>> addedChildrenIterator = updatedDataGroup.addedChildrenCollections
				.iterator();
		assertSame(addedChildrenIterator.next(),
				originalDataGroup.getAllChildrenWithNameInData("title"));
		assertSame(addedChildrenIterator.next(),
				originalDataGroup.getAllChildrenWithNameInData("otherConstraint"));

	}

	@Test
	public void testReplaceNoChildToRemove() throws Exception {
		updatedDataGroup.childExists = false;

		recordPartFilter.replaceChildrenForConstraintsWithoutPermissions(originalDataGroup,
				updatedDataGroup, titleConstraints, emptyPermissions);

		assertTrue(updatedDataGroup.containsChildWithNameInDataWasCalled);
		assertFalse(updatedDataGroup.removeAllChildrenWasCalled);
	}

}
