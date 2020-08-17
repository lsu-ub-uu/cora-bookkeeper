/*
 * Copyright 2020 Uppsala University Library
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAttributeSpy;
import se.uu.ub.cora.data.DataAttribute;

public class ConstraintTest {

	private Constraint constraint;
	String nameInData = "someNameInData";

	@BeforeMethod
	public void setUp() {
		constraint = new Constraint(nameInData);
	}

	@Test
	public void testNameInData() {
		assertEquals(constraint.getNameInData(), nameInData);
	}

	@Test
	public void testAddAttribute() {
		DataAttribute dataAttribute = new DataAttributeSpy("someName", "someValue");
		constraint.addAttribute(dataAttribute);
		List<DataAttribute> dataAttributes = constraint.getDataAttributes();
		assertEquals(dataAttributes.size(), 1);

		assertEquals(dataAttributes.get(0).getNameInData(), "someName");
		assertEquals(dataAttributes.get(0).getValue(), "someValue");
	}

	@Test
	public void testNOTEqualsNotAConstraint() {
		assertFalse(constraint.equals("someNameInData"));
	}

	// @Test
	// public void testNOTEqualsNull() {
	// Constraint secondConstraint = null;
	// assertFalse(constraint.equals(secondConstraint));
	// }

	@Test
	public void testEqualsNoAttributes() {
		Constraint secondConstraint = new Constraint("someNameInData");
		assertTrue(constraint.equals(secondConstraint));
	}

	@Test
	public void testNOTEqualsNoAttributes() {
		Constraint secondConstraint = new Constraint("someOtherNameInData");
		assertFalse(constraint.equals(secondConstraint));
	}

	@Test
	public void testNOTEqualsNoAttributesButOneAttributeInOther() {
		Constraint secondConstraint = new Constraint("someNameInData");
		DataAttribute dataAttribute = new DataAttributeSpy("someName", "someValue");
		secondConstraint.addAttribute(dataAttribute);
		assertFalse(constraint.equals(secondConstraint));
	}

}
