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

package se.uu.ub.cora.bookkeeper.presentation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class PresentationTextReferenceTest {
	private PresentationTextReference presentationTextReference;

	@BeforeMethod
	public void beforeMethod() {
		String textRef = "textRef";
		presentationTextReference = new PresentationTextReference(textRef);
	}

	@Test
	public void testInit() {
		assertEquals(presentationTextReference.getTextRef(), "textRef");
	}

	@Test
	public void testPresentationChildReference() {
		PresentationChildReference presentationChildReference = presentationTextReference;
		String refId = presentationChildReference.getReferenceId();
		assertEquals(refId, "textRef");
	}
}
