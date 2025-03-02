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
import static org.testng.Assert.assertSame;

import java.util.Collections;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToRecordLinkConverterTest {
	private DataRecordGroupSpy dataRecordGroup;
	private DataToRecordLinkConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		dataRecordGroup = DataToMetadataElementTestHelper.createDataRecordGroupForMetadata(
				"otherId", "other", "otherTextId", "otherDefTextId");
		addRecordLinkSpecificsToDataRecordGroup();

		converter = DataToRecordLinkConverter.fromDataRecordGroup(dataRecordGroup);
	}

	private void addRecordLinkSpecificsToDataRecordGroup() {
		DataToMetadataElementTestHelper.addLink(dataRecordGroup, "linkedRecordType",
				"someRecordType");
	}

	@Test
	public void testToMetadata() {
		RecordLink recordLink = converter.toMetadata();

		DataToMetadataElementTestHelper
				.assertBasicMetadataElementForIdAndNameInDataAndTextIdAndDefTextIdAndAttributeReferences(
						recordLink, "otherId", "other", "otherTextId", "otherDefTextId",
						Collections.emptyList());
		assertEquals(recordLink.getLinkedRecordType(), "someRecordType");
	}

	@Test
	public void testToMetadataWithLinkedPath() {
		DataGroupSpy group = new DataGroupSpy();
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "linkedPath");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> group, "linkedPath");

		RecordLink recordLink = converter.toMetadata();

		assertSame(recordLink.getLinkedPath(), group);
	}

	@Test
	public void testToMetadataWithFinalValue() {
		DataToMetadataElementTestHelper.addAtomic(dataRecordGroup, "finalValue", "someInstance");

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getFinalValue(), "someInstance");
	}

	@Test
	public void testToMetadataWithRefParentId() {
		DataToMetadataElementTestHelper.addLink(dataRecordGroup, "refParentId", "someParent");

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getRefParentId(), "someParent");
	}

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataToMetadataElementTestHelper.addAttributeReferenceIds(dataRecordGroup,
				"numberTypeCollectionVar", "someOtherCollectionVar");

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getAttributeReferences().size(), 2);
		assertEquals(recordLink.getAttributeReferences().get(0), "numberTypeCollectionVar");
		assertEquals(recordLink.getAttributeReferences().get(1), "someOtherCollectionVar");
	}

}
