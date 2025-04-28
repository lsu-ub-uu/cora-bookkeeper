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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordSpy;

public class DataDecoratorTest {
	private DataDecoratorImp dataDecorator;
	private DataGroup dataGroupToDecorate;
	private DataChildDecoratorFactorySpy decoratorFactory;
	private DataRecordSpy recordToDecorated;
	private DataFactorySpy dataFactorySpy;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
		decoratorFactory = new DataChildDecoratorFactorySpy();
		dataDecorator = new DataDecoratorImp(decoratorFactory);
		dataGroupToDecorate = new DataGroupSpy();
		recordToDecorated = new DataRecordSpy();
	}

	@Test
	public void testCallDecorateGroup() {
		dataDecorator.decorateDataGroup("someMetadataId", dataGroupToDecorate);

		var dataChildDecorator = (DataChildDecoratorSpy) decoratorFactory.MCR
				.assertCalledParametersReturn("factor", "someMetadataId");
		dataChildDecorator.MCR.assertParameters("decorateData", 0, dataGroupToDecorate);
	}

	@Test
	public void testGetDataDecoratorFactory() {
		assertSame(dataDecorator.onlyForTestGetDataChildDecoratorFactory(), decoratorFactory);
	}

	@Test
	public void testDecorateDataGroup_GoesWrong() {
		RuntimeException thrownException = new RuntimeException("someException");
		decoratorFactory.MRV.setAlwaysThrowException("factor", thrownException);

		try {
			dataDecorator.decorateDataGroup("someMetadataId", dataGroupToDecorate);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof DataDecaratorException);
			assertEquals(e.getMessage(),
					"Failed to decorate record using metadataid: someMetadataId");
			assertEquals(e.getCause(), thrownException);
		}
	}

	@Test
	public void testDecorateRecord_GoesWrong() {
		RuntimeException thrownException = new RuntimeException("someException");
		decoratorFactory.MRV.setAlwaysThrowException("factor", thrownException);

		try {
			dataDecorator.decorateRecord("someMetadataId", recordToDecorated);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof DataDecaratorException);
			assertEquals(e.getMessage(),
					"Failed to decorate record using metadataid: someMetadataId");
			assertEquals(e.getCause(), thrownException);
		}
	}

	@Test
	public void testCallDecorateRecord() {
		dataDecorator.decorateRecord("someMetadataId", recordToDecorated);
		var dataRecordGroup = (DataRecordGroupSpy) recordToDecorated.MCR
				.getReturnValue("getDataRecordGroup", 0);
		var dataGroup = (DataGroup) dataFactorySpy.MCR
				.assertCalledParametersReturn("factorGroupFromDataRecordGroup", dataRecordGroup);

		var dataChildDecorator = (DataChildDecoratorSpy) decoratorFactory.MCR
				.assertCalledParametersReturn("factor", "someMetadataId");
		dataChildDecorator.MCR.assertParameters("decorateData", 0, dataGroup);

		var decoratedRecordGroup = (DataRecordGroup) dataFactorySpy.MCR
				.assertCalledParametersReturn("factorRecordGroupFromDataGroup", dataGroup);
		recordToDecorated.MCR.assertParameters("setDataRecordGroup", 0, decoratedRecordGroup);

	}

}