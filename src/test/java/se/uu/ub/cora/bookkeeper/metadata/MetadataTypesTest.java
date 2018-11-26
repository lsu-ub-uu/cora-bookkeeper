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

import org.testng.annotations.Test;

public class MetadataTypesTest {
	@Test
	public void testEnum() {
		// small hack to get 100% coverage on enum
		MetadataTypes.valueOf(MetadataTypes.GROUP.toString());
		assertEquals(MetadataTypes.GROUP.type, "metadataGroup");
		assertEquals(MetadataTypes.TEXTVARIABLE.type, "metadataTextVariable");
		assertEquals(MetadataTypes.NUMBERVARIABLE.type, "metadataNumberVariable");
		assertEquals(MetadataTypes.COLLECTIONVARIABLE.type, "metadataCollectionVariable");
		assertEquals(MetadataTypes.ITEMCOLLECTION.type, "metadataItemCollection");
		assertEquals(MetadataTypes.COLLECTIONITEM.type, "metadataCollectionItem");
		assertEquals(MetadataTypes.RECORDLINK.type, "metadataRecordLink");
		assertEquals(MetadataTypes.RESOURCELINK.type, "metadataResourceLink");
	}
}
