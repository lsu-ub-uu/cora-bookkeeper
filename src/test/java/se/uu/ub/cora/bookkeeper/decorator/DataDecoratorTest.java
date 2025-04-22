/*
 * Copyright 2025 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.decorator;

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.spies.DataGroupSpy;

public class DataDecoratorTest {
	private DataDecoratorImp dataDecorator;
	private DataGroup dataGroupToDecorate;
	private DataChildDecoratorFactorySpy decoratorFactory;

	@BeforeMethod
	public void setUp() {
		decoratorFactory = new DataChildDecoratorFactorySpy();
		dataDecorator = new DataDecoratorImp(decoratorFactory);
		dataGroupToDecorate = new DataGroupSpy();
	}

	@Test
	public void testCallDecorateData() {
		dataDecorator.decorateData("someMetadataId", dataGroupToDecorate);

		var dataChildDecorator = (DataChildDecoratorSpy) decoratorFactory.MCR
				.assertCalledParametersReturn("factor", "someMetadataId");
		dataChildDecorator.MCR.assertParameters("decorateData", 0, dataGroupToDecorate);
	}

	@Test
	public void testGetDataDecoratorFactory() {
		assertSame(dataDecorator.onlyForTestGetDataChildDecoratorFactory(), decoratorFactory);
	}

}