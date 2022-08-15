/*
 * Copyright 2022 Uppsala University Library
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

import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

public class AttributeTest {

	@Test
	public void testAttributeHasEmptyListWhenCreated() {
		Attribute attribute = new Attribute();
		// attribute.values =
		List<String> values = attribute.values;
		assertEquals(values, Collections.emptyList());
	}

	@Test
	public void testAttributeAddValuesToList() {
		Attribute attribute = new Attribute();
		attribute.values = List.of("red", "green", "yellow");
		assertEquals(attribute.values, List.of("red", "green", "yellow"));

	}

	@Test
	public void testAddNameInDataInAttribute() {
		Attribute attribute = new Attribute();
		assertEquals(attribute.nameInData, "");

		String someNameInData = "someNameInData";
		attribute.nameInData = someNameInData;
		assertEquals(attribute.nameInData, someNameInData);
	}

	@Test
	public void testConstructor() {

		String someNameInData = "someNameInData";
		List<String> values = List.of("red", "green", "yellow");
		Attribute attribute = Attribute.createAttributeUsingNameInDataAndValueList(someNameInData,
				values);

		assertEquals(attribute.nameInData, someNameInData);
		assertEquals(attribute.values, List.of("red", "green", "yellow"));
	}

}
