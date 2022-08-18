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
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
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
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionVar");

		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(new DataAtomicSpy("nameInData", "other"));
		DataCreator.addTextToGroupWithNameInDataLinkedIdAndLinkedType(dataGroup, "textId",
				"otherTextId", "testSystem");
		DataCreator.addTextToGroupWithNameInDataLinkedIdAndLinkedType(dataGroup, "defTextId",
				"otherDefTextId", "testSystem");

		DataGroup refCollection = new DataGroupOldSpy("refCollection");
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
		DataGroup refParentGroup = new DataGroupOldSpy("refParentId");
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

	@Test
	public void testToMetadataWithAttributeReferences() {
		DataGroup dataGroup = createDataGroup();
		createAndAddAttributeReferences(dataGroup);

		DataGroupToCollectionVariableConverter converter = DataGroupToCollectionVariableConverter
				.fromDataGroup(dataGroup);
		CollectionVariable collectionVariable = converter.toMetadata();
		assertEquals(collectionVariable.getAttributeReferences().size(), 2);
		assertEquals(collectionVariable.getAttributeReferences().get(0), "numberTypeCollectionVar");
		assertEquals(collectionVariable.getAttributeReferences().get(1), "someOtherCollectionVar");
	}

	private void createAndAddAttributeReferences(DataGroup dataGroup) {
		DataGroup attributeReferences = new DataGroupOldSpy("attributeReferences");
		attributeReferences.addChild(createRef("numberTypeCollectionVar", "0"));
		attributeReferences.addChild(createRef("someOtherCollectionVar", "1"));
		dataGroup.addChild(attributeReferences);
	}

	private DataGroupOldSpy createRef(String linkedRecordId, String repeatId) {
		DataGroupOldSpy ref = new DataGroupOldSpy("ref");
		ref.addChild(new DataAtomicSpy("linkedRecordType", "metadataCollectionVariable"));
		ref.addChild(new DataAtomicSpy("linkedRecordId", linkedRecordId));
		ref.setRepeatId(repeatId);
		return ref;
	}
}
