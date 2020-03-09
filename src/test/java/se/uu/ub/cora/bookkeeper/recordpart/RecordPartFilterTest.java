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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.data.DataGroup;

public class RecordPartFilterTest {

	private DataGroup dataGroup;
	// private DataGroup metadataGroup;
	private RecordPartFilter recordPartFilter;
	private String groupNameInData = "book";

	@BeforeMethod
	public void setUp() {
		// ska ta en map med nameInData på barnen, readRedcord och permissions
		dataGroup = new DataGroupSpy("someDataGroup");
		dataGroup.addChild(new DataAtomicSpy("title", "someChildValue"));
		// metadataGroup = createMetadataGroup();
		recordPartFilter = new RecordPartFilterImp();
	}

	// private DataGroup createMetadataGroup() {
	// DataGroup metadataGroup = new DataGroupSpy("metadata");
	// addChildReferences(metadataGroup);
	// metadataGroup.addChild(new DataAtomicSpy("title", "someId"));
	// return metadataGroup;
	// }

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
		Map<String, String> recordPartConstraints = Collections.emptyMap();
		DataGroup filteredDataGroup = recordPartFilter.filterReadRecordPartsUsingPermissions(
				groupNameInData, dataGroup, recordPartConstraints, Collections.emptyList());
		assertNotNull(filteredDataGroup);
		assertTrue(filteredDataGroup.containsChildWithNameInData("title"));
	}

	@Test
	public void testNoConstraintsButPermissions() throws Exception {
		Map<String, String> recordPartConstraints = Collections.emptyMap();
		List<String> recordPartPermissions = new ArrayList<>();
		recordPartPermissions.add("book.title");

		DataGroup filteredDataGroup = recordPartFilter.filterReadRecordPartsUsingPermissions(
				groupNameInData, dataGroup, recordPartConstraints, recordPartPermissions);
		assertNotNull(filteredDataGroup);
		assertTrue(filteredDataGroup.containsChildWithNameInData("title"));
	}

	@Test
	public void testReadWriteConstraintsAndPermissions() throws Exception {
		Map<String, String> recordPartConstraints = new HashMap<>();
		recordPartConstraints.put("title", "readWrite");
		List<String> recordPartPermissions = new ArrayList<>();
		recordPartPermissions.add("book.title");

		DataGroup filteredDataGroup = recordPartFilter.filterReadRecordPartsUsingPermissions(
				groupNameInData, dataGroup, recordPartConstraints, recordPartPermissions);
		assertNotNull(filteredDataGroup);
		assertTrue(filteredDataGroup.containsChildWithNameInData("title"));
	}

	@Test
	public void testReadWriteConstraintsNoPermissions() throws Exception {
		Map<String, String> recordPartConstraints = new HashMap<>();
		recordPartConstraints.put("title", "readWrite");
		List<String> recordPartPermissions = new ArrayList<>();

		DataGroup filteredDataGroup = recordPartFilter.filterReadRecordPartsUsingPermissions(
				groupNameInData, dataGroup, recordPartConstraints, recordPartPermissions);
		assertNotNull(filteredDataGroup);
		assertFalse(filteredDataGroup.containsChildWithNameInData("title"));
	}
	//
	// @Test
	// public void testWriteConstraintsNoPermissions() throws Exception {
	// Map<String, String> recordPartConstraints = new HashMap<>();
	// recordPartConstraints.put("title", "write");
	// List<String> recordPartPermissions = new ArrayList<>();
	// // recordPartPermissions.add("book.title");
	//
	// DataGroup filteredDataGroup = recordPartFilter.filterReadRecordPartsUsingPermissions(
	// groupNameInData, dataGroup, recordPartConstraints, recordPartPermissions);
	// assertNotNull(filteredDataGroup);
	// assertTrue(filteredDataGroup.containsChildWithNameInData("title"));
	// }
}
