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

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class PresentationGroupTest {
	private PresentationGroup presentationGroup;

	@BeforeMethod
	public void beforeMethod() {
		String id = "presentationGroupId";
		String refGroupId = "presentationRefGroupId";
		presentationGroup = new PresentationGroup(id, refGroupId);
	}

	@Test
	public void testInit() {
		assertEquals(presentationGroup.getId(), "presentationGroupId");
		assertEquals(presentationGroup.getRefGroupId(), "presentationRefGroupId");
	}

	@Test
	public void testAddChild() {
		PresentationChildReference textRef = new PresentationTextReference("textRef");
		presentationGroup.addChild(textRef);
		List<PresentationChildReference> childReferences = presentationGroup.getChildReferences();
		PresentationChildReference childReference = childReferences.iterator().next();
		assertEquals(childReference.getReferenceId(), "textRef");
	}

	@Test
	public void testPresentationElement() {
		PresentationElement presentationElement = presentationGroup;
		assertEquals(presentationElement.getId(), "presentationGroupId");
	}
}
