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

public class PresentationVariableTest {
	private PresentationVariable presentationVariable;

	@BeforeMethod
	public void beforeMethod() {
		String id = "presentationVariableId";
		String refVarId = "presentationRefVarId";
		presentationVariable = new PresentationVariable(id, refVarId,
				PresentationVariable.Mode.INPUT);
	}

	@Test
	public void testInitInput() {
		assertEquals(presentationVariable.getId(), "presentationVariableId");
		assertEquals(presentationVariable.getRefVarId(), "presentationRefVarId");
		assertEquals(presentationVariable.getMode(), PresentationVariable.Mode.INPUT);
		// small hack to get 100% coverage on enum
		PresentationVariable.Mode.valueOf(PresentationVariable.Mode.INPUT.toString());
	}

	@Test
	public void testModeInput() {
		PresentationVariable.Mode mode = PresentationVariable.Mode.INPUT;
		assertEquals(mode.getValue(), "input");
	}

	@Test
	public void testModeOutput() {
		PresentationVariable.Mode mode = PresentationVariable.Mode.OUTPUT;
		assertEquals(mode.getValue(), "output");
	}

	@Test
	public void testPresentationElement() {
		PresentationElement presentationElement = presentationVariable;
		assertEquals(presentationElement.getId(), "presentationVariableId");
	}
}
