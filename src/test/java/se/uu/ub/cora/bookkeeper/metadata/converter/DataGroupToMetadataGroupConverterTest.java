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

import java.util.Iterator;

import org.testng.annotations.Test;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;

import static org.testng.Assert.assertEquals;

public class DataGroupToMetadataGroupConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "group");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));

		DataGroup attributeReference = DataGroup.withNameInData("attributeReferences");
		attributeReference.addChild(DataAtomic.withNameInDataAndValue("ref", "attribute1"));
		attributeReference.addChild(DataAtomic.withNameInDataAndValue("ref", "attribute2"));
		attributeReference.addChild(DataAtomic.withNameInDataAndValue("ref", "attribute3"));
		dataGroup.addChild(attributeReference);

		DataGroup childReferences = DataGroup.withNameInData("childReferences");
		dataGroup.addChild(childReferences);

		DataGroup childReference = DataGroup.withNameInData("childReference");
		childReference.addChild(DataAtomic.withNameInDataAndValue("ref", "otherMetadata"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMinKey", "SOME_KEY"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "16"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("secret", "true"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("secretKey", "SECRET_KEY"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("readOnly", "true"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("readOnlyKey", "READONLY_KEY"));
		childReferences.addChild(childReference);

		DataGroupToMetadataConverter converter = DataGroupToMetadataGroupConverter
				.fromDataGroup(dataGroup);

		MetadataGroup metadataGroup = (MetadataGroup) converter.toMetadata();
		assertEquals(metadataGroup.getId(), "otherId");
		assertEquals(metadataGroup.getNameInData(), "other");
		assertEquals(metadataGroup.getTextId(), "otherTextId");
		assertEquals(metadataGroup.getDefTextId(), "otherDefTextId");

		Iterator<String> attributeReferenceIterator = metadataGroup.getAttributeReferences()
				.iterator();
		assertEquals(attributeReferenceIterator.next(), "attribute1");
		assertEquals(attributeReferenceIterator.next(), "attribute2");
		assertEquals(attributeReferenceIterator.next(), "attribute3");

		Iterator<MetadataChildReference> childReferencesIterator = metadataGroup
				.getChildReferences().iterator();
		MetadataChildReference metadataChildReference = childReferencesIterator.next();
		assertEquals(metadataChildReference.getReferenceId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMinKey(), "SOME_KEY");
		assertEquals(metadataChildReference.getRepeatMax(), 16);
		assertEquals(metadataChildReference.isSecret(), true);
		assertEquals(metadataChildReference.getSecretKey(), "SECRET_KEY");
		assertEquals(metadataChildReference.isReadOnly(), true);
		assertEquals(metadataChildReference.getReadOnlyKey(), "READONLY_KEY");

	}

	@Test
	public void testToMetadataNoAttributeRefrences() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "group");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));

		DataGroup childReferences = DataGroup.withNameInData("childReferences");
		dataGroup.addChild(childReferences);

		DataGroup childReference = DataGroup.withNameInData("childReference");
		childReference.addChild(DataAtomic.withNameInDataAndValue("ref", "otherMetadata"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMinKey", "SOME_KEY"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "16"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("secret", "true"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("secretKey", "SECRET_KEY"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("readOnly", "true"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("readOnlyKey", "READONLY_KEY"));
		childReferences.addChild(childReference);

		DataGroupToMetadataGroupConverter converter = DataGroupToMetadataGroupConverter
				.fromDataGroup(dataGroup);

		MetadataGroup metadataGroup = converter.toMetadata();
		assertEquals(metadataGroup.getId(), "otherId");
		assertEquals(metadataGroup.getNameInData(), "other");
		assertEquals(metadataGroup.getTextId(), "otherTextId");
		assertEquals(metadataGroup.getDefTextId(), "otherDefTextId");

		Iterator<MetadataChildReference> childReferencesIterator = metadataGroup
				.getChildReferences().iterator();
		MetadataChildReference metadataChildReference = childReferencesIterator.next();
		assertEquals(metadataChildReference.getReferenceId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMinKey(), "SOME_KEY");
		assertEquals(metadataChildReference.getRepeatMax(), 16);
		assertEquals(metadataChildReference.isSecret(), true);
		assertEquals(metadataChildReference.getSecretKey(), "SECRET_KEY");
		assertEquals(metadataChildReference.isReadOnly(), true);
		assertEquals(metadataChildReference.getReadOnlyKey(), "READONLY_KEY");

	}
}
