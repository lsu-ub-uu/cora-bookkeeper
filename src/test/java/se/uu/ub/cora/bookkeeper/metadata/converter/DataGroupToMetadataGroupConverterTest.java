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

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupToMetadataGroupConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = createDataGroup();

		DataGroupToMetadataGroupConverter converter = DataGroupToMetadataGroupConverter
				.fromDataGroup(dataGroup);

		MetadataGroup metadataGroup = converter.toMetadata();
		assertMetadataGroupIsBasedOnDataGroup(metadataGroup);

	}

	private DataGroup createDataGroup() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "group");

		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		recordInfo.addChild(new DataAtomicOldSpy("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(new DataAtomicOldSpy("nameInData", "other"));
		addTexts(dataGroup);

		DataGroup childReferences = new DataGroupOldSpy("childReferences");
		dataGroup.addChild(childReferences);

		DataGroup childReference = new DataGroupOldSpy("childReference");
		DataGroup ref = new DataGroupOldSpy("ref");
		ref.addAttributeByIdWithValue("type", "group");
		ref.addChild(new DataAtomicOldSpy("linkedRecordType", "metadataGroup"));
		ref.addChild(new DataAtomicOldSpy("linkedRecordId", "otherMetadata"));
		childReference.addChild(ref);
		childReference.addChild(new DataAtomicOldSpy("repeatMin", "0"));
		childReference.addChild(new DataAtomicOldSpy("repeatMinKey", "SOME_KEY"));
		childReference.addChild(new DataAtomicOldSpy("repeatMax", "16"));
		childReference.addChild(new DataAtomicOldSpy("secret", "true"));
		childReference.addChild(new DataAtomicOldSpy("secretKey", "SECRET_KEY"));
		childReference.addChild(new DataAtomicOldSpy("readOnly", "true"));
		childReference.addChild(new DataAtomicOldSpy("readOnlyKey", "READONLY_KEY"));
		childReferences.addChild(childReference);
		return dataGroup;
	}

	private void addTexts(DataGroup dataGroup) {
		DataGroup text = new DataGroupOldSpy("textId");
		text.addChild(new DataAtomicOldSpy("linkedRecordType", "textSystemOne"));
		text.addChild(new DataAtomicOldSpy("linkedRecordId", "otherTextId"));
		dataGroup.addChild(text);

		DataGroup defText = new DataGroupOldSpy("defTextId");
		defText.addChild(new DataAtomicOldSpy("linkedRecordType", "textSystemOne"));
		defText.addChild(new DataAtomicOldSpy("linkedRecordId", "otherDefTextId"));
		dataGroup.addChild(defText);
	}

	private void assertMetadataGroupIsBasedOnDataGroup(MetadataGroup metadataGroup) {
		assertEquals(metadataGroup.getId(), "otherId");
		assertEquals(metadataGroup.getNameInData(), "other");
		assertEquals(metadataGroup.getTextId(), "otherTextId");
		assertEquals(metadataGroup.getDefTextId(), "otherDefTextId");

		Iterator<MetadataChildReference> childReferencesIterator = metadataGroup
				.getChildReferences().iterator();
		MetadataChildReference metadataChildReference = childReferencesIterator.next();
		assertEquals(metadataChildReference.getLinkedRecordId(), "otherMetadata");
		assertEquals(metadataChildReference.getLinkedRecordType(), "metadataGroup");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMax(), 16);
	}

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataGroup dataGroup = createDataGroup();
		DataCreator.addAttributesToDataGroup(dataGroup);

		DataGroupOldSpy attributesGroup = (DataGroupOldSpy) dataGroup
				.getFirstGroupWithNameInData("attributeReferences");
		attributesGroup.numOfGetAllGroupsWithNameInDataToReturn.put("ref", 3);

		DataGroupToMetadataConverter converter = DataGroupToMetadataGroupConverter
				.fromDataGroup(dataGroup);
		MetadataGroup metadataGroup = (MetadataGroup) converter.toMetadata();

		assertMetadataGroupIsBasedOnDataGroup(metadataGroup);
		assertAttributesBasedOnDataGroup(metadataGroup);
	}

	private void assertAttributesBasedOnDataGroup(MetadataGroup metadataGroup) {
		Iterator<String> attributeReferenceIterator = metadataGroup.getAttributeReferences()
				.iterator();
		assertEquals(attributeReferenceIterator.next(), "someLinkedRecordIdFromSpy");
		assertEquals(attributeReferenceIterator.next(), "someLinkedRecordIdFromSpy");
		assertEquals(attributeReferenceIterator.next(), "someLinkedRecordIdFromSpy");
	}

	@Test
	public void testToMetadataAsChildGroup() {
		DataGroup dataGroup = createDataGroup();
		DataGroup refParentId = new DataGroupOldSpy("refParentId");
		refParentId.addChild(new DataAtomicOldSpy("linkedRecordType", "metadataGroup"));
		refParentId.addChild(new DataAtomicOldSpy("linkedRecordId", "refParentId"));
		dataGroup.addChild(refParentId);

		DataGroupToMetadataGroupConverter converter = DataGroupToMetadataGroupConverter
				.fromDataGroup(dataGroup);

		MetadataGroup metadataGroup = converter.toMetadata();
		assertEquals(metadataGroup.getRefParentId(), "refParentId");
	}
}
