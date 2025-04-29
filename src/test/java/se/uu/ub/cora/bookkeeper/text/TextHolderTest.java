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

package se.uu.ub.cora.bookkeeper.text;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;

public class TextHolderTest {
	private static final String NOT_EXISTING_TEXT_ID = "notExistingTextId";
	private static final String EXISTING_TEXT_ID = "existingTextId";
	private TextElementSpy textElement;
	private TextHolderImp textHolder;

	@BeforeMethod
	private void beforeMethod() {
		textElement = new TextElementSpy();
		textElement.MRV.setDefaultReturnValuesSupplier("getId", () -> EXISTING_TEXT_ID);
		textHolder = new TextHolderImp();
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Text with id: notExistingTextId could not be found in the text holder.")
	public void testTextElementMissing() {
		textHolder.getTextElement(NOT_EXISTING_TEXT_ID);
	}

	@Test
	public void testGetTextElement() {
		textHolder.addTextElement(textElement);

		TextElement textElementOut = textHolder.getTextElement(EXISTING_TEXT_ID);

		assertEquals(textElementOut, textElement);
	}

	@Test
	public void testContainsTextElement() {
		textHolder.addTextElement(textElement);

		assertFalse(textHolder.containsTextElement(NOT_EXISTING_TEXT_ID));
		assertTrue(textHolder.containsTextElement(EXISTING_TEXT_ID));
	}

	@Test
	public void testDeleteTextElement() {
		textHolder.addTextElement(textElement);

		assertTrue(textHolder.containsTextElement(EXISTING_TEXT_ID));
		textHolder.deleteTextElement(EXISTING_TEXT_ID);
		assertFalse(textHolder.containsTextElement(EXISTING_TEXT_ID));
	}

	@Test
	public void testDeleteNotExistingTextElement() {
		assertFalse(textHolder.containsTextElement(NOT_EXISTING_TEXT_ID));
		textHolder.deleteTextElement(NOT_EXISTING_TEXT_ID);
		assertFalse(textHolder.containsTextElement(NOT_EXISTING_TEXT_ID));
	}
}
