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

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;

public class DataGroupToCollectionVariableConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = createDataGroup();

		DataGroupToCollectionVariableConverter converter = DataGroupToCollectionVariableConverter
				.fromDataGroup(dataGroup);
		CollectionVariable collectionVariable = converter.toMetadata();

		assertBasicCollectionVariableValuesAreCorrect(collectionVariable);
		assertEquals(collectionVariable.getFinalValue(), null);
		assertEquals(collectionVariable.getRefParentId(), null);
	}

	private DataGroup createDataGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionVar");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		DataCreator.addTextToGroupWithNameInDataLinkedIdAndLinkedType(dataGroup, "textId", "otherTextId", "testSystem");
		DataCreator.addTextToGroupWithNameInDataLinkedIdAndLinkedType(dataGroup, "defTextId", "otherDefTextId", "testSystem");
//		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
//		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));
		
		DataGroup refCollection = DataGroup.withNameInData("refCollection");
		refCollection.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "metadataItemCollection"));
		refCollection.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "refCollection"));
		dataGroup.addChild(refCollection);

		
		return dataGroup;
	}

	private void assertBasicCollectionVariableValuesAreCorrect(
			CollectionVariable collectionVariable) {
		assertEquals(collectionVariable.getId(), "otherId");
		assertEquals(collectionVariable.getNameInData(), "other");

		assertEquals(collectionVariable.getTextId(), "otherTextId");
		assertEquals(collectionVariable.getDefTextId(), "otherDefTextId");
		assertEquals(collectionVariable.getRefCollectionId(), "refCollection");
	}

	@Test
	public void testToMetadataWithRefParentId() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refParentId", "refParentId"));

		DataGroupToCollectionVariableConverter converter = DataGroupToCollectionVariableConverter
				.fromDataGroup(dataGroup);
		CollectionVariable collectionVariable = converter.toMetadata();

		assertBasicCollectionVariableValuesAreCorrect(collectionVariable);
		assertEquals(collectionVariable.getRefParentId(), "refParentId");
	}

	@Test
	public void testToMetadataWithFinalValue() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("finalValue", "finalValue"));

		DataGroupToCollectionVariableConverter converter = DataGroupToCollectionVariableConverter
				.fromDataGroup(dataGroup);
		CollectionVariable collectionVariable = converter.toMetadata();

		assertBasicCollectionVariableValuesAreCorrect(collectionVariable);
		assertEquals(collectionVariable.getFinalValue(), "finalValue");
	}
}
