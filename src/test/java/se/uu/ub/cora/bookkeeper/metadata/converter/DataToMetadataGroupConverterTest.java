/*
 * Copyright 2015, 2019 Uppsala University Library
 * Copyright 2025 Olov McKie
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

package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToMetadataGroupConverterTest {
	private DataRecordGroupSpy dataRecordGroup;
	private DataToMetadataGroupConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		dataRecordGroup = DataToMetadataElementTestHelper.createDataRecordGroupForMetadata(
				"otherId", "other", "otherTextId", "otherDefTextId");
		addChildReferencesToDataRecordGroupUsingId("otherMetadata");

		converter = DataToMetadataGroupConverter.fromDataRecordGroup(dataRecordGroup);
	}

	@Test
	public void testToMetadata() {
		MetadataGroup metadataGroup = converter.toMetadata();

		DataToMetadataElementTestHelper
				.assertBasicMetadataElementForIdAndNameInDataAndTextIdAndDefTextIdAndAttributeReferences(
						metadataGroup, "otherId", "other", "otherTextId", "otherDefTextId",
						Collections.emptyList());

		assertMetadataGroupIsBasedOnDataGroup(metadataGroup);
	}

	private void addChildReferencesToDataRecordGroupUsingId(String... childReferenceIds) {
		DataGroupSpy childReferencesGroup = new DataGroupSpy();
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "childReferences");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> childReferencesGroup, "childReferences");

		List<DataGroup> childReferences = new ArrayList<>();
		for (String ref : childReferenceIds) {
			String repeatMin = "0";
			String repeatMax = "16";
			DataGroupSpy childReferenceGroup = createChildReference(ref, repeatMin, repeatMax);
			childReferences.add(childReferenceGroup);
		}
		childReferencesGroup.MRV.setDefaultReturnValuesSupplier("getChildren",
				() -> childReferences);
	}

	private DataGroupSpy createChildReference(String ref, String repeatMin, String repeatMax) {
		DataGroupSpy childReferenceGroup = new DataGroupSpy();
		DataToMetadataElementTestHelper.addLink(childReferenceGroup, "ref", "metadata", ref);
		DataToMetadataElementTestHelper.addAtomic(childReferenceGroup, "repeatMin", repeatMin);
		DataToMetadataElementTestHelper.addAtomic(childReferenceGroup, "repeatMax", repeatMax);
		return childReferenceGroup;
	}

	private void assertMetadataGroupIsBasedOnDataGroup(MetadataGroup metadataGroup) {
		Iterator<MetadataChildReference> childReferencesIterator = metadataGroup
				.getChildReferences().iterator();
		MetadataChildReference metadataChildReference = childReferencesIterator.next();
		assertEquals(metadataChildReference.getLinkedRecordType(), "metadata");
		assertEquals(metadataChildReference.getLinkedRecordId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMax(), 16);
	}

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataToMetadataElementTestHelper.addAttributeReferenceIds(dataRecordGroup,
				"numberTypeCollectionVar", "someOtherCollectionVar");

		MetadataGroup metadataGroup = converter.toMetadata();

		assertAttributes(metadataGroup);
	}

	private void assertAttributes(MetadataGroup metadataGroup) {
		assertEquals(metadataGroup.getAttributeReferences().size(), 2);
		assertEquals(metadataGroup.getAttributeReferences().get(0), "numberTypeCollectionVar");
		assertEquals(metadataGroup.getAttributeReferences().get(1), "someOtherCollectionVar");
	}

	@Test
	public void testToMetadataAsChildGroup() {
		DataToMetadataElementTestHelper.addLink(dataRecordGroup, "refParentId", "refParentId");

		MetadataGroup metadataGroup = converter.toMetadata();

		assertEquals(metadataGroup.getRefParentId(), "refParentId");
	}
}
