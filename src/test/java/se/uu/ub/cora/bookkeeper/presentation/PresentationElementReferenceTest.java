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

public class PresentationElementReferenceTest {
	private PresentationElementReference presentationElementReference;

	@BeforeMethod
	public void beforeMethod() {
		String elementRef = "elementRef";
		String presentationOf = "presentationOf";
		presentationElementReference = new PresentationElementReference(elementRef, presentationOf);
	}

	@Test
	public void testInit() {
		assertEquals(presentationElementReference.getElementRef(), "elementRef");
		assertEquals(presentationElementReference.getPresentationOf(), "presentationOf");
	}

	@Test
	public void testPresentationChildReference() {
		PresentationChildReference presentationChildReference = presentationElementReference;
		String refId = presentationChildReference.getReferenceId();
		assertEquals(refId, "elementRef");

	}

	@Test
	public void testElementRefMinimized() {
		String elementRefMinimized = "elementRefMinimized";
		presentationElementReference.setElementRefMinimized(elementRefMinimized);
		assertEquals(presentationElementReference.getElementRefMinimized(), "elementRefMinimized");
	}
}
