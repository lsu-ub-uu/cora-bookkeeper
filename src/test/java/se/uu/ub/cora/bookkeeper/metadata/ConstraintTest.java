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

import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.testspies.data.DataChildFilterSpy;
import se.uu.ub.cora.testspies.data.DataFactorySpy;

public class ConstraintTest {

	private Constraint defaultConstraint;
	String nameInData = "someNameInData";
	ConstraintType type = ConstraintType.WRITE;
	private DataFactorySpy dataFactorySpy;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
		defaultConstraint = new Constraint(nameInData);
	}

	@Test
	public void testNameInData() {
		assertEquals(defaultConstraint.getNameInData(), nameInData);
		dataFactorySpy.MCR.assertParameters("factorDataChildFilterUsingNameInData", 0, nameInData);
	}

	@Test
	public void testSetType() throws Exception {
		defaultConstraint.setType(ConstraintType.WRITE);
		assertEquals(defaultConstraint.getType(), ConstraintType.WRITE);
	}

	@Test
	public void testGetDataChildFilter() {
		DataChildFilter childFilter = getCreatedChildFilterFromProviderSpy();
		assertEquals(defaultConstraint.getDataChildFilter(), childFilter);
	}

	private DataChildFilterSpy getCreatedChildFilterFromProviderSpy() {
		return (DataChildFilterSpy) dataFactorySpy.MCR
				.getReturnValue("factorDataChildFilterUsingNameInData", 0);
	}

	@Test
	public void testAddAttributeToConstraint() throws Exception {
		DataChildFilterSpy childFilter = getCreatedChildFilterFromProviderSpy();
		Set<String> possibleValues = Set.of("1", "2");
		defaultConstraint.addAttributeUsingNameInDataAndPossibleValues(nameInData, possibleValues);
		childFilter.MCR.assertParameters("addAttributeUsingNameInDataAndPossibleValues", 0,
				nameInData, possibleValues);
	}
}
