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

package se.uu.ub.cora.bookkeeper;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TextElementTest {
	@Test
	public void testInit() {
		TranslationHolder translationHolder = new TranslationHolder();
		translationHolder.addTranslation("sv", "Testar en text");
		translationHolder.addTranslation("en", "Testing with a text");
		TextElement textElement = TextElement.withIdAndTranslationHolder("textId", translationHolder);
		
		assertEquals(textElement.getId(), "textId",
				"TextId should be the same as the one set in the constructor");

		assertEquals(textElement.getTranslations(), translationHolder,
				"Translations should be the same as the one set in the constructor");
		
		assertEquals(textElement.getTranslationByLanguage("sv"),
				"Testar en text", "The fetched translated text is not correct");
	}
}
