/*
 * Copyright 2015, 2019 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupToRecordLinkConverterTest {
	private DataGroupToRecordLinkConverter converter;
	private DataGroup dataGroup;

	@BeforeMethod
	public void setUp() {
		dataGroup = createDataGroupContainingRecordLink();
		converter = DataGroupToRecordLinkConverter.fromDataGroup(dataGroup);

	}

	private DataGroup createDataGroupContainingRecordLink() {
		DataGroup dataGroup = new DataGroupSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "recordLink");
		dataGroup.addChild(new DataAtomicSpy("nameInData", "other"));

		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(new DataAtomicSpy("nameInData", "other"));
		addTexts(dataGroup);

		DataGroup linkedRecordType = new DataGroupSpy("linkedRecordType");
		linkedRecordType.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		linkedRecordType.addChild(new DataAtomicSpy("linkedRecordId", "someRecordType"));
		dataGroup.addChild(linkedRecordType);
		// dataGroup.addChild(new DataAtomicSpy("linkedRecordType",
		// "someRecordType"));
		return dataGroup;
	}

	private void addTexts(DataGroup dataGroup) {
		DataGroup text = new DataGroupSpy("textId");
		text.addChild(new DataAtomicSpy("linkedRecordType", "textSystemOne"));
		text.addChild(new DataAtomicSpy("linkedRecordId", "otherTextId"));
		dataGroup.addChild(text);

		DataGroup defText = new DataGroupSpy("defTextId");
		defText.addChild(new DataAtomicSpy("linkedRecordType", "textSystemOne"));
		defText.addChild(new DataAtomicSpy("linkedRecordId", "otherDefTextId"));
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
		dataGroup.addChild(new DataGroupSpy("linkedPath"));

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getLinkedPath().getNameInData(), "linkedPath");
	}

	@Test
	public void testToMetadataWithFinalValue() {
		dataGroup.addChild(new DataAtomicSpy("finalValue", "someInstance"));

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getFinalValue(), "someInstance");
	}

	@Test
	public void testToMetadataWithRefParentId() {
		DataGroup refParentId = new DataGroupSpy("refParentId");
		refParentId.addChild(new DataAtomicSpy("linkedRecordType", "metadataRecordLink"));
		refParentId.addChild(new DataAtomicSpy("linkedRecordId", "someParent"));
		dataGroup.addChild(refParentId);

		RecordLink recordLink = converter.toMetadata();

		assertEquals(recordLink.getRefParentId(), "someParent");
	}

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataGroup recordLinkDataGroup = createDataGroupContainingRecordLink();
		DataCreator.addAttributesToDataGroup(recordLinkDataGroup);

		DataGroupSpy attributesGroup = (DataGroupSpy) recordLinkDataGroup
				.getFirstGroupWithNameInData("attributeReferences");
		attributesGroup.numOfGetAllGroupsWithNameInDataToReturn.put("ref", 3);

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
		assertEquals(attributeReferenceIterator.next(), "someLinkedRecordIdFromSpy");
		assertEquals(attributeReferenceIterator.next(), "someLinkedRecordIdFromSpy");
		assertEquals(attributeReferenceIterator.next(), "someLinkedRecordIdFromSpy");
	}

}
