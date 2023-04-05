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

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupToCollectionItemConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		recordInfo.addChild(new DataAtomicOldSpy("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(new DataAtomicOldSpy("nameInData", "other"));

		addTexts(dataGroup);

		DataGroupToCollectionItemConverter converter = DataGroupToCollectionItemConverter
				.fromDataGroup(dataGroup);
		CollectionItem collectionItem = converter.toMetadata();

		assertEquals(collectionItem.getId(), "otherId");
		assertEquals(collectionItem.getNameInData(), "other");
		assertEquals(collectionItem.getTextId(), "otherTextId");
		assertEquals(collectionItem.getDefTextId(), "otherDefTextId");
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
}
