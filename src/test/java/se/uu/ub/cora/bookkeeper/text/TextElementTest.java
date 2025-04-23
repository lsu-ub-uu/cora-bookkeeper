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

package se.uu.ub.cora.bookkeeper.text;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;
import java.util.Set;

import org.testng.annotations.Test;

public class TextElementTest {
	@Test
	public void testAddAndReadTranslations() {
		TextElement textElement = TextElementImp.withId("textId");
		textElement.addTranslation("sv", "Testar en text");
		textElement.addTranslation("en", "Testing with a text");

		assertEquals(textElement.getId(), "textId");
		assertEquals(textElement.getTranslationByLanguage("sv"), "Testar en text");
		assertEquals(textElement.getTranslationByLanguage("en"), "Testing with a text");
	}

	@Test
	public void testAddAndReadAllTranslations() {
		TextElement textElement = TextElementImp.withId("textId");
		textElement.addTranslation("sv", "Testar en text");
		textElement.addTranslation("en", "Testing with a text");

		Set<Translation> translations = textElement.getTranslations();

		assertEquals(translations.size(), 2);
		Iterator<Translation> iterator = translations.iterator();
		assertEquals(iterator.next().language(), "en");
		assertEquals(iterator.next().language(), "sv");
	}
}
