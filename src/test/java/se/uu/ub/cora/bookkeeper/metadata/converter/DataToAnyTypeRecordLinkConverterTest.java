/*
 * Copyright 2026 Uppsala University Library
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

import java.util.Collections;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.AnyTypeRecordLink;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToAnyTypeRecordLinkConverterTest {
	private DataRecordGroupSpy dataRecordGroup;
	private DataToAnyTypeRecordLinkConverter converter;
	private AnyTypeRecordLink recordLink;

	@BeforeMethod
	public void beforeMethod() {
		dataRecordGroup = DataToMetadataElementTestHelper.createDataRecordGroupForMetadata(
				"otherId", "other", "otherTextId", "otherDefTextId");
		addRecordLinkSpecificsToDataRecordGroup();

		converter = DataToAnyTypeRecordLinkConverter.fromDataRecordGroup(dataRecordGroup);
	}

	private void addRecordLinkSpecificsToDataRecordGroup() {
		DataToMetadataElementTestHelper.addLink(dataRecordGroup, "linkedRecordType",
				"someRecordType");
	}

	@Test
	public void testToMetadata() {
		recordLink = converter.toMetadata();

		DataToMetadataElementTestHelper
				.assertBasicMetadataElementForIdAndNameInDataAndTextIdAndDefTextIdAndAttributeReferences(
						recordLink, "otherId", "other", "otherTextId", "otherDefTextId",
						Collections.emptyList());
	}

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataToMetadataElementTestHelper.addAttributeReferenceIds(dataRecordGroup,
				"numberTypeCollectionVar", "someOtherCollectionVar");

		recordLink = converter.toMetadata();

		assertEquals(recordLink.getAttributeReferences().size(), 2);
		assertEquals(recordLink.getAttributeReferences().get(0), "numberTypeCollectionVar");
		assertEquals(recordLink.getAttributeReferences().get(1), "someOtherCollectionVar");
	}

}
