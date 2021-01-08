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

import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAttributeSpy;
import se.uu.ub.cora.data.DataAttribute;

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
		addAttributeToDefaultConstraint("someName", "someValue");
		Set<DataAttribute> dataAttributes = defaultConstraint.getDataAttributes();
		assertEquals(dataAttributes.size(), 1);

		assertEquals(dataAttributes.iterator().next().getNameInData(), "someName");
		assertEquals(dataAttributes.iterator().next().getValue(), "someValue");
	}

	private void addAttributeToDefaultConstraint(String nameInData, String value) {
		DataAttribute dataAttribute = new DataAttributeSpy(nameInData, value);
		defaultConstraint.addAttribute(dataAttribute);
	}

}
