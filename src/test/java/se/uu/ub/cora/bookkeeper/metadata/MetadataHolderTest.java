/*
 * Copyright 2015, 2025 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MetadataHolderTest {
	private static final String SOME_ID = "someId";
	private MetadataHolder metadataHolder;
	private MetadataElement element;

	@BeforeMethod
	public void beforeMethod() {
		metadataHolder = new MetadataHolderImp();

		element = TextVariable.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(SOME_ID,
				"nameInData", "textId", "defTextId", "someRegularExpression");
	}

	@Test
	public void testAddMetadataElement() {
		metadataHolder.addMetadataElement(element);

		assertEquals(metadataHolder.getMetadataElement(SOME_ID), element,
				"textElement should be the same one that was entered");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "MetadataElement with id " + SOME_ID + " is missing")
	public void testDeleteMetadataElement() {
		metadataHolder.addMetadataElement(element);

		metadataHolder.deleteMetadataElement(SOME_ID);

		metadataHolder.getMetadataElement(SOME_ID);
	}

	@Test
	public void testContainsElement() {
		assertEquals(metadataHolder.containsElement(SOME_ID), false);

		metadataHolder.addMetadataElement(element);

		assertEquals(metadataHolder.containsElement(SOME_ID), true);

	}
}
