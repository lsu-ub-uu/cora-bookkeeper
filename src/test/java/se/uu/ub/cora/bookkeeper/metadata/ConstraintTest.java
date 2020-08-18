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

import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAttributeSpy;
import se.uu.ub.cora.data.DataAttribute;

public class ConstraintTest {

	private Constraint defaultConstraint;
	String nameInData = "someNameInData";

	@BeforeMethod
	public void setUp() {
		defaultConstraint = new Constraint(nameInData);
	}

	@Test
	public void testNameInData() {
		assertEquals(defaultConstraint.getNameInData(), nameInData);
	}

	@Test
	public void testAddAttribute() {
		addAttributeToDefaultConstraint("someName", "someValue");
		Set<DataAttribute> dataAttributes = defaultConstraint.getDataAttributes();
		assertEquals(dataAttributes.size(), 1);

		assertEquals(dataAttributes.iterator().next().getNameInData(), "someName");
		assertEquals(dataAttributes.iterator().next().getValue(), "someValue");
	}

	@Test
	public void testNOTEqualsNotAConstraint() {
		assertFalse(defaultConstraint.equals("someNameInData"));
	}

	// @Test
	// public void testNOTEqualsNull() {
	// Constraint secondConstraint = null;
	// assertFalse(constraint.equals(secondConstraint));
	// }

	@Test
	public void testNOTEqualsNoAttributes() {
		Constraint secondConstraint = new Constraint("someOtherNameInData");
		assertFalse(defaultConstraint.equals(secondConstraint));
	}

	@Test
	public void testNOTEqualsNoAttributesButOneAttributeInOther() {
		Constraint secondConstraint = new Constraint("someNameInData");
		DataAttribute dataAttribute = new DataAttributeSpy("someName", "someValue");
		secondConstraint.addAttribute(dataAttribute);
		assertFalse(defaultConstraint.equals(secondConstraint));
	}

	private void addAttributeToDefaultConstraint(String nameInData, String value) {
		DataAttribute dataAttribute = new DataAttributeSpy(nameInData, value);
		defaultConstraint.addAttribute(dataAttribute);
	}

	@Test
	public void testNOTEqualsDifferentAttributeName() {
		addAttributeToDefaultConstraint("someName", "someValue");

		Constraint secondConstraint = new Constraint("someNameInData");
		DataAttribute dataAttribute = new DataAttributeSpy("someOtherName", "someValue");

		secondConstraint.addAttribute(dataAttribute);
		assertFalse(defaultConstraint.equals(secondConstraint));
	}

	@Test
	public void testNOTEqualsDifferentAttributeValue() {
		addAttributeToDefaultConstraint("someName", "someValue");

		Constraint secondConstraint = new Constraint("someNameInData");
		DataAttribute dataAttribute = new DataAttributeSpy("someName", "someOtherValue");

		secondConstraint.addAttribute(dataAttribute);
		assertFalse(defaultConstraint.equals(secondConstraint));
	}

	@Test
	public void testNOTEqualsOneSameOneDifferentAttributeName() {
		addAttributeToDefaultConstraint("someName", "someValue");
		addAttributeToDefaultConstraint("someOtherName", "someOtherValue");

		Constraint secondConstraint = new Constraint("someNameInData");
		addAttributeToConstraint(secondConstraint, "someName", "someValue");
		addAttributeToConstraint(secondConstraint, "someName", "someOtherValue");

		assertFalse(defaultConstraint.equals(secondConstraint));
	}

	@Test
	public void testNOTEqualsOneSameOneDifferentAttributeValue() {
		addAttributeToDefaultConstraint("someName", "someValue");
		addAttributeToDefaultConstraint("someOtherName", "someOtherValue");

		Constraint secondConstraint = new Constraint("someNameInData");
		addAttributeToConstraint(secondConstraint, "someName", "someValue");
		addAttributeToConstraint(secondConstraint, "someOtherName", "someNotSameValue");

		assertFalse(defaultConstraint.equals(secondConstraint));
	}

	@Test
	public void testEqualsNoAttributes() {
		Constraint secondConstraint = new Constraint("someNameInData");
		assertTrue(defaultConstraint.equals(secondConstraint));
	}

	@Test
	public void testEqualsOneAttribute() {
		addAttributeToDefaultConstraint("someName", "someValue");

		Constraint secondConstraint = new Constraint("someNameInData");
		DataAttribute dataAttribute2 = new DataAttributeSpy("someName", "someValue");
		secondConstraint.addAttribute(dataAttribute2);
		assertTrue(defaultConstraint.equals(secondConstraint));
	}

	@Test
	public void testEqualsTwoAttributes() {
		addAttributeToDefaultConstraint("someName", "someValue");
		addAttributeToDefaultConstraint("someOtherName", "someOtherValue");

		Constraint secondConstraint = new Constraint("someNameInData");
		addAttributeToConstraint(secondConstraint, "someOtherName", "someOtherValue");
		addAttributeToConstraint(secondConstraint, "someName", "someValue");

		assertTrue(defaultConstraint.equals(secondConstraint));
	}

	private void addAttributeToConstraint(Constraint constraint, String nameInData, String value) {
		DataAttribute dataAttribute = new DataAttributeSpy(nameInData, value);
		constraint.addAttribute(dataAttribute);
	}

}
