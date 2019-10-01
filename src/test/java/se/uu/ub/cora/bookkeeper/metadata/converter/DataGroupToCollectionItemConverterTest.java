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
import se.uu.ub.cora.bookkeeper.metadata.CollectionItem;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupToCollectionItemConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = new DataGroupSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionItem");

		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(new DataAtomicSpy("nameInData", "other"));

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
		DataGroup text = new DataGroupSpy("textId");
		text.addChild(new DataAtomicSpy("linkedRecordType", "textSystemOne"));
		text.addChild(new DataAtomicSpy("linkedRecordId", "otherTextId"));
		dataGroup.addChild(text);

		DataGroup defText = new DataGroupSpy("defTextId");
		defText.addChild(new DataAtomicSpy("linkedRecordType", "textSystemOne"));
		defText.addChild(new DataAtomicSpy("linkedRecordId", "otherDefTextId"));
		dataGroup.addChild(defText);
	}
}
