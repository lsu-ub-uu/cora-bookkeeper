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
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupToItemCollectionConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = new DataGroupOldSpy("metadata");
		dataGroup.addAttributeByIdWithValue("type", "itemCollection");

		DataGroup recordInfo = new DataGroupOldSpy("recordInfo");
		recordInfo.addChild(new DataAtomicOldSpy("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(new DataAtomicOldSpy("nameInData", "other"));

		DataGroup text = new DataGroupOldSpy("textId");
		text.addChild(new DataAtomicOldSpy("linkedRecordType", "textSystemOne"));
		text.addChild(new DataAtomicOldSpy("linkedRecordId", "otherTextId"));
		dataGroup.addChild(text);

		DataGroup defText = new DataGroupOldSpy("defTextId");
		defText.addChild(new DataAtomicOldSpy("linkedRecordType", "textSystemOne"));
		defText.addChild(new DataAtomicOldSpy("linkedRecordId", "otherDefTextId"));
		dataGroup.addChild(defText);

		DataGroup collectionItemReferences = new DataGroupOldSpy("collectionItemReferences");

		createAndAddItemReference(collectionItemReferences, "choice1", "one");
		createAndAddItemReference(collectionItemReferences, "choice2", "two");
		createAndAddItemReference(collectionItemReferences, "choice3", "three");

		dataGroup.addChild(collectionItemReferences);

		DataGroupToItemCollectionConverter converter = DataGroupToItemCollectionConverter
				.fromDataGroup(dataGroup);
		ItemCollection itemCollection = converter.toMetadata();

		assertEquals(itemCollection.getId(), "otherId");
		assertEquals(itemCollection.getNameInData(), "other");
		assertEquals(itemCollection.getTextId(), "otherTextId");
		assertEquals(itemCollection.getDefTextId(), "otherDefTextId");

		Iterator<String> iterator = itemCollection.getCollectionItemReferences().iterator();
		assertEquals(iterator.next(), "choice1");
		assertEquals(iterator.next(), "choice2");
		assertEquals(iterator.next(), "choice3");
	}

	private void createAndAddItemReference(DataGroup collectionItemReferences,
			String linkedRecordId, String repeatId) {
		DataGroup ref1 = new DataGroupOldSpy("ref");
		ref1.setRepeatId(repeatId);
		ref1.addChild(new DataAtomicOldSpy("linkedRecordType", "metadataCollectionItem"));
		ref1.addChild(new DataAtomicOldSpy("linkedRecordId", linkedRecordId));
		collectionItemReferences.addChild(ref1);
	}
}
