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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataGroup;

public class RecordPartFilterTest {

	private DataGroup dataGroup;
	private DataGroup metadataGroup;
	private RecordPartFilter recordPartFilter;

	@BeforeMethod
	public void setUp() {
		// ska ta en map med nameInData på barnen, readRedcord och permissions
		dataGroup = new DataGroupSpy("someDataGroup");
		dataGroup.addChild(new DataAtomicSpy("nameInData", "book"));
		dataGroup.addChild(new DataAtomicSpy("someChildId", "someChildValue"));
		metadataGroup = createMetadataGroup();
		recordPartFilter = new RecordPartFilterImp();
	}

	private DataGroup createMetadataGroup() {
		DataGroup metadataGroup = new DataGroupSpy("metadata");
		addChildReferences(metadataGroup);
		metadataGroup.addChild(new DataAtomicSpy("id", "someId"));
		return metadataGroup;
	}

	private void addChildReferences(DataGroup metadataGroup) {
		DataGroupSpy childReferences = new DataGroupSpy("childReferences");
		DataGroupSpy childReference = new DataGroupSpy("childReference");
		createAndAddRef(childReference);
		childReferences.addChild(childReference);
		metadataGroup.addChild(childReferences);
	}

	private void createAndAddRef(DataGroupSpy childReference) {
		DataGroupSpy ref = new DataGroupSpy("ref");
		ref.addChild(new DataAtomicSpy("linkedRecordType", "metadataTextVariable"));
		ref.addChild(new DataAtomicSpy("linkedRecordId", "idTextVar"));
		childReference.addChild(ref);
	}

	@Test
	public void testNoConstraintsNoPermissions() throws Exception {

		RecordPartFilter recordPartFilter = new RecordPartFilterImp();
		DataGroup filteredDataGroup = recordPartFilter.filterReadRecorPartsUsingPermissions(
				metadataGroup, dataGroup, Collections.emptyList());
		assertNotNull(filteredDataGroup);
		assertTrue(dataGroup.containsChildWithNameInData("someChildId"));
	}

	@Test
	public void testNoConstraintsButPermissions() throws Exception {
		List<String> recordPartPermissions = new ArrayList<>();
		recordPartPermissions.add("book.id");

		DataGroup filteredDataGroup = recordPartFilter.filterReadRecorPartsUsingPermissions(
				metadataGroup, dataGroup, recordPartPermissions);
		assertNotNull(filteredDataGroup);
		assertTrue(dataGroup.containsChildWithNameInData("someChildId"));
	}

	@Test
	public void testReadWriteConstraintsAndPermissions() throws Exception {
		addReadWriteReferenceToIdChildReference();

		List<String> recordPartPermissions = new ArrayList<>();
		recordPartPermissions.add("book.id");

		DataGroup filteredDataGroup = recordPartFilter.filterReadRecorPartsUsingPermissions(
				metadataGroup, dataGroup, recordPartPermissions);
		assertNotNull(filteredDataGroup);
		assertTrue(dataGroup.containsChildWithNameInData("someChildId"));
	}

	private void addReadWriteReferenceToIdChildReference() {
		DataGroup childReferences = metadataGroup.getFirstGroupWithNameInData("childReferences");
		DataGroup childReference = childReferences.getFirstGroupWithNameInData("childReference");
		childReference.addChild(new DataAtomicSpy("recordPartConstraint", "readWrite"));
	}
}
