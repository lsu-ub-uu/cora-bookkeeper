/*
 * Copyright 2020, 2022 Uppsala University Library
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

import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConstraintTest {

	private Constraint defaultConstraint;
	String nameInData = "someNameInData";
	ConstraintType type = ConstraintType.WRITE;

	@BeforeMethod
	public void setUp() {
		defaultConstraint = new Constraint(nameInData);
	}

	@Test
	public void testNameInData() {
		assertEquals(defaultConstraint.getNameInData(), nameInData);
	}

	@Test
	public void testSetType() throws Exception {
		defaultConstraint.setType(ConstraintType.WRITE);
		assertEquals(defaultConstraint.getType(), ConstraintType.WRITE);
	}

	@Test
	public void testAddAttribute() {

		addAttributeToDefaultConstraint("someName", List.of("someValue1", "someValue2"));
		Set<Attribute> metadataAttributes = defaultConstraint.getAttributes();
		assertEquals(metadataAttributes.size(), 1);
		Attribute firstAttribute = metadataAttributes.iterator().next();

		assertEquals(firstAttribute.nameInData, "someName");
		assertEquals(firstAttribute.values.get(0), "someValue1");
		assertEquals(firstAttribute.values.get(1), "someValue2");
	}

	private void addAttributeToDefaultConstraint(String nameInData, List<String> value) {
		Attribute metadataAttribute = Attribute
				.createAttributeUsingNameInDataAndValueList(nameInData, value);

		defaultConstraint.addAttribute(metadataAttribute);
	}

}
