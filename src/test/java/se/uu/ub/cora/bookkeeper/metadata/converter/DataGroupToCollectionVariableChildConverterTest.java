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
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariableChild;

public class DataGroupToCollectionVariableChildConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionVar");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refCollectionId", "refCollection"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refParentId", "refParentId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("finalValue", "finalValue"));

		DataGroupToCollectionVariableChildConverter converter = DataGroupToCollectionVariableChildConverter
				.fromDataGroup(dataGroup);
		CollectionVariableChild collectionVariableChild = converter.toMetadata();

		assertEquals(collectionVariableChild.getId(), "otherId");
		assertEquals(collectionVariableChild.getNameInData(), "other");
		assertEquals(collectionVariableChild.getTextId(), "otherTextId");
		assertEquals(collectionVariableChild.getDefTextId(), "otherDefTextId");
		assertEquals(collectionVariableChild.getRefCollectionId(), "refCollection");
		assertEquals(collectionVariableChild.getRefParentId(), "refParentId");
		assertEquals(collectionVariableChild.getFinalValue(), "finalValue");
	}

	@Test
	public void testToMetadataWithoutFinalValue() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionVar");

		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withNameInDataAndValue("nameInData", "other"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refCollectionId", "refCollection"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("refParentId", "refParentId"));
		// dataGroup.addChild(DataAtomic.withNameInDataAndValue("finalValue",
		// "finalValue"));

		DataGroupToCollectionVariableChildConverter converter = DataGroupToCollectionVariableChildConverter
				.fromDataGroup(dataGroup);
		CollectionVariableChild collectionVariableChild = converter.toMetadata();

		assertEquals(collectionVariableChild.getId(), "otherId");
		assertEquals(collectionVariableChild.getNameInData(), "other");
		assertEquals(collectionVariableChild.getTextId(), "otherTextId");
		assertEquals(collectionVariableChild.getDefTextId(), "otherDefTextId");
		assertEquals(collectionVariableChild.getRefCollectionId(), "refCollection");
		assertEquals(collectionVariableChild.getRefParentId(), "refParentId");
		assertEquals(collectionVariableChild.getFinalValue(), null);
	}
}
