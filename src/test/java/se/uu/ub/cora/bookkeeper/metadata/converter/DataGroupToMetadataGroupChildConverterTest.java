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
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroupChild;

import static org.testng.Assert.assertEquals;

public class DataGroupToMetadataGroupChildConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "groupChild");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("parentId", "parentMetadataId"));

		DataGroup attributeReferences = DataGroup.withNameInData("attributeReferences");
		attributeReferences.addChild(DataAtomic.withNameInDataAndValue("ref", "attribute1"));
		attributeReferences.addChild(DataAtomic.withNameInDataAndValue("ref", "attribute2"));
		attributeReferences.addChild(DataAtomic.withNameInDataAndValue("ref", "attribute3"));
		dataGroup.addChild(attributeReferences);

		// TODO: add childReferences
		DataGroup childReferences = DataGroup.withNameInData("childReferences");
		dataGroup.addChild(childReferences);

		DataGroup childReference = DataGroup.withNameInData("childReference");
		childReference.addChild(DataAtomic.withNameInDataAndValue("ref", "otherMetadata"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMinKey", "SOME_KEY"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "15"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("secret", "true"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("secretKey", "SECRET_KEY"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("readOnly", "true"));
		childReference.addChild(DataAtomic.withNameInDataAndValue("readOnlyKey", "READONLY_KEY"));
		childReferences.addChild(childReference);

		DataGroupToMetadataGroupChildConverter converter = DataGroupToMetadataGroupChildConverter
				.fromDataGroup(dataGroup);
		MetadataGroupChild metadataGroupChild = converter.toMetadata();

		assertEquals(metadataGroupChild.getId(), "otherId");
		assertEquals(metadataGroupChild.getNameInData(), "other");
		assertEquals(metadataGroupChild.getTextId(), "otherTextId");
		assertEquals(metadataGroupChild.getDefTextId(), "otherDefTextId");

		Iterator<String> iterator = metadataGroupChild.getAttributeReferences().iterator();
		assertEquals(iterator.next(), "attribute1");
		assertEquals(iterator.next(), "attribute2");
		assertEquals(iterator.next(), "attribute3");

		Iterator<MetadataChildReference> iterator2 = metadataGroupChild.getChildReferences()
				.iterator();
		MetadataChildReference metadataChildReference = iterator2.next();
		assertEquals(metadataChildReference.getReferenceId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMinKey(), "SOME_KEY");
		assertEquals(metadataChildReference.getRepeatMax(), 15);
		assertEquals(metadataChildReference.isSecret(), true);
		assertEquals(metadataChildReference.getSecretKey(), "SECRET_KEY");
		assertEquals(metadataChildReference.isReadOnly(), true);
		assertEquals(metadataChildReference.getReadOnlyKey(), "READONLY_KEY");
	}
}
