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

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CollectionVariableChildTest {
	@Test
	public void testInit() {
		CollectionVariableChild childVar = new CollectionVariableChild("id", "nameInData", "textId",
				"defTextId", "refCollection", "collectionVarId");

		assertEquals(childVar.getId(), "id", "Id should have the value set in the constructor");

		assertEquals(childVar.getNameInData(), "nameInData",
				"NameInData should have the value set in the constructor");

		assertEquals(childVar.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		assertEquals(childVar.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

		assertEquals(childVar.getRefCollectionId(), "refCollection",
				"RefCollectionId should have the value set in the constructor");

		assertEquals(childVar.getRefParentId(), "collectionVarId",
				"refParentId should have the value set in the constructor");

	}
}
