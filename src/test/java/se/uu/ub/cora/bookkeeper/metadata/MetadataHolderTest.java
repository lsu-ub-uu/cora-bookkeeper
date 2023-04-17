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

public class MetadataHolderTest {
	@Test
	public void testInit() {
		MetadataHolder metadataHolder = new MetadataHolderImp();
		String regularExpression = "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}";
		MetadataElement textElement = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("id", "nameInData",
						"textId", "defTextId", regularExpression);
		metadataHolder.addMetadataElement(textElement);
		assertEquals(metadataHolder.getMetadataElement("id"), textElement,
				"textElement should be the same one that was entered");
	}
}
