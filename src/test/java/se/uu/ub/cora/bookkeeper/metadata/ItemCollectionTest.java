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

package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class ItemCollectionTest {
	@Test
	public void testInit() {

		ItemCollection itemCollection = new ItemCollection("id", "nameInData", "textId",
				"defTextId");
		itemCollection.addItemReference("item1Ref");
		assertEquals(itemCollection.getId(), "id",
				"Id should have the value set in the constructor");

		assertEquals(itemCollection.getNameInData(), "nameInData",
				"NameInData should have the value set in the constructor");

		assertEquals(itemCollection.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		assertEquals(itemCollection.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

		assertNotNull(itemCollection.getCollectionItemReferences(),
				"CollectionItemReferences should not be null");

		assertTrue(itemCollection.getAttributeReferences().isEmpty());

	}
}
