/*
 * Copyright 2015 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;

import static org.testng.Assert.assertEquals;

public class DataGroupToRecordLinkConverterTest {
	private DataGroupToRecordLinkConverter converter;
	private DataGroup dataGroup;

	@BeforeMethod
	public void setUp() {
		dataGroup = createDataGroupContainingRecordLink();
		converter = DataGroupToRecordLinkConverter.fromDataGroup(dataGroup);

	}

	private DataGroup createDataGroupContainingRecordLink() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "recordLink");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "someRecordType"));
		return dataGroup;
	}

	@Test
	public void testToMetadata() {
		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getId(), "otherId");
		assertEquals(recordLink.getNameInData(), "other");
		assertEquals(recordLink.getTextId(), "otherTextId");
		assertEquals(recordLink.getDefTextId(), "otherDefTextId");
		assertEquals(recordLink.getLinkedRecordType(), "someRecordType");
	}

	@Test
	public void testToMetadataWithLinkedPath() {
		dataGroup.addChild(DataGroup.withNameInData("linkedPath"));

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getLinkedPath().getNameInData(), "linkedPath");
	}

	@Test
	public void testToMetadataWithFinalValue() {
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("finalValue", "someInstance"));

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getFinalValue(), "someInstance");
	}

	@Test
	public void testToMetadataWithRefParentId() {
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refParentId", "someParent"));

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getRefParentId(), "someParent");
	}

}
