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

import org.testng.Assert;
import org.testng.annotations.Test;

public class TextHolderTest {
	@Test
	public void testInit() {
		TextHolder textHolder = new TextHolder();

		TranslationHolder translationHolder = new TranslationHolder();
		translationHolder.addTranslation("sv", "En text på svenska");
		translationHolder.addTranslation("en", "A text in english");

		TextElement textElement = TextElement.withIdAndTranslationHolder("textId", translationHolder);

		textHolder.addTextElement(textElement);

		TextElement textElementOut = textHolder.getTextElement("textId");

		Assert.assertEquals(textElementOut, textElement,
				"The element should be the same as the one entered");

	}
}
