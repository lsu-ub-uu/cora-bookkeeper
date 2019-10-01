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

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupSpy;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.bookkeeper.testdata.DataCreator;
import se.uu.ub.cora.data.DataGroup;

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
		DataGroup dataGroup = new DataGroupSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionVar");

		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(new DataAtomicSpy("nameInData", "other"));
		DataCreator.addTextToGroupWithNameInDataLinkedIdAndLinkedType(dataGroup, "textId",
				"otherTextId", "testSystem");
		DataCreator.addTextToGroupWithNameInDataLinkedIdAndLinkedType(dataGroup, "defTextId",
				"otherDefTextId", "testSystem");
		// dataGroup.addChild(new DataAtomicSpy("textId", "otherTextId"));
		// dataGroup.addChild(new DataAtomicSpy("defTextId", "otherDefTextId"));

		DataGroup refCollection = new DataGroupSpy("refCollection");
		refCollection.addChild(new DataAtomicSpy("linkedRecordType", "metadataItemCollection"));
		refCollection.addChild(new DataAtomicSpy("linkedRecordId", "refCollection"));
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
		DataGroup refParentGroup = new DataGroupSpy("refParentId");
		refParentGroup
				.addChild(new DataAtomicSpy("linkedRecordType", "metadataCollectionVariable"));
		refParentGroup.addChild(new DataAtomicSpy("linkedRecordId", "refParentId"));
		dataGroup.addChild(refParentGroup);

		DataGroupToCollectionVariableConverter converter = DataGroupToCollectionVariableConverter
				.fromDataGroup(dataGroup);
		CollectionVariable collectionVariable = converter.toMetadata();

		assertBasicCollectionVariableValuesAreCorrect(collectionVariable);
		assertEquals(collectionVariable.getRefParentId(), "refParentId");
	}

	@Test
	public void testToMetadataWithFinalValue() {
		DataGroup dataGroup = createDataGroup();
		dataGroup.addChild(new DataAtomicSpy("finalValue", "finalValue"));

		DataGroupToCollectionVariableConverter converter = DataGroupToCollectionVariableConverter
				.fromDataGroup(dataGroup);
		CollectionVariable collectionVariable = converter.toMetadata();

		assertBasicCollectionVariableValuesAreCorrect(collectionVariable);
		assertEquals(collectionVariable.getFinalValue(), "finalValue");
	}
}
