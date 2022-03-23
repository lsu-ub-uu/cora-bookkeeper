/*
 * Copyright 2015, 2022 Uppsala University Library
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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TextVariableTest {
	private TextVariable textVar;
	private String regularExpression;

	@BeforeMethod
	public void setUp() {
		regularExpression = "((^(([0-1][0-9])|([2][0-3])):[0-5][0-9]$|^$){1}";
		textVar = TextVariable.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("id",
				"nameInData", "textId", "defTextId", regularExpression);
	}

	@Test
	public void testRegExVariableInit() {
		assertEquals(textVar.getId(), "id", "Id should have the value set in the constructor");

		assertEquals(textVar.getNameInData(), "nameInData",
				"NameInData should have the value set in the constructor");

		assertEquals(textVar.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		assertEquals(textVar.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

		assertEquals(textVar.getRegularExpression(), regularExpression,
				"RegularExpression should have the value set in the constructor");
	}

	@Test
	public void testWithRefParentId() {
		textVar.setRefParentId("refParentId");
		assertEquals(textVar.getRefParentId(), "refParentId");
	}

	@Test
	public void testWithFinalValue() {
		textVar.setFinalValue("finalValue");
		assertEquals(textVar.getFinalValue(), "finalValue");
	}

	@Test
	public void testGetAttributeReferencesNoAttributes() {
		assertTrue(textVar.getAttributeReferences().isEmpty());
	}

	@Test
	public void testAddAttributeReference() {
		textVar.addAttributeReference("type");
		assertEquals(textVar.getAttributeReferences().iterator().next(), "type");
	}

}
