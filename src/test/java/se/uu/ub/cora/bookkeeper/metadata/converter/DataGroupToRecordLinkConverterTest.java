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

import static org.testng.Assert.assertEquals;

import java.util.Iterator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

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
		addTexts(dataGroup);

		DataGroup linkedRecordType = DataGroup.withNameInData("linkedRecordType");
		linkedRecordType
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		linkedRecordType
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "someRecordType"));
		dataGroup.addChild(linkedRecordType);
		// dataGroup.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType",
		// "someRecordType"));
		return dataGroup;
	}

	private void addTexts(DataGroup dataGroup) {
		DataGroup text = DataGroup.withNameInData("textId");
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "textSystemOne"));
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "otherTextId"));
		dataGroup.addChild(text);

		DataGroup defText = DataGroup.withNameInData("defTextId");
		defText.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "textSystemOne"));
		defText.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "otherDefTextId"));
		dataGroup.addChild(defText);
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
		DataGroup refParentId = DataGroup.withNameInData("refParentId");
		refParentId.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataRecordLink"));
		refParentId.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "someParent"));
		dataGroup.addChild(refParentId);

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getRefParentId(), "someParent");
	}

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataGroup recordLinkDataGroup = createDataGroupContainingRecordLink();
		DataCreator.addAttributesToDataGroup(recordLinkDataGroup);

		DataGroupToMetadataConverter converter = DataGroupToRecordLinkConverter
				.fromDataGroup(recordLinkDataGroup);
		RecordLink recordLink = (RecordLink) converter.toMetadata();

		assertEquals(recordLink.getId(), "otherId");
		assertEquals(recordLink.getNameInData(), "other");
		assertEquals(recordLink.getTextId(), "otherTextId");
		assertEquals(recordLink.getDefTextId(), "otherDefTextId");
		assertEquals(recordLink.getLinkedRecordType(), "someRecordType");
		assertAttributesBasedOnDataGroup(recordLink);
	}

	private void assertAttributesBasedOnDataGroup(RecordLink recordLink) {
		Iterator<String> attributeReferenceIterator = recordLink.getAttributeReferences()
				.iterator();
		assertEquals(attributeReferenceIterator.next(), "attribute1");
		assertEquals(attributeReferenceIterator.next(), "attribute2");
		assertEquals(attributeReferenceIterator.next(), "attribute3");
	}

}
