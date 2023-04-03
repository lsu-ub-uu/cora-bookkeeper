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
package se.uu.ub.cora.bookkeeper.recordtype.internal;

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandlerFactoryImp;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.storage.spies.RecordStorageInstanceProviderSpy;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class RecordTypeHandlerFactoryTest {

	private RecordStorage recordStorage;
	private RecordTypeHandlerFactoryImp factory;
	private RecordStorageInstanceProviderSpy instanceProvider;

	@BeforeMethod
	private void beforeMethod() {
		LoggerFactorySpy loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);

		instanceProvider = new RecordStorageInstanceProviderSpy();
		RecordStorageProvider.onlyForTestSetRecordStorageInstanceProvider(instanceProvider);

		recordStorage = new RecordStorageSpy();
		instanceProvider.MRV.setDefaultReturnValuesSupplier("getRecordStorage",
				() -> recordStorage);

		factory = new RecordTypeHandlerFactoryImp();
	}

	@Test
	public void testRecordStorageLoadedOnceOnFirstCallToFactor() throws Exception {
		DataGroupSpy dataGroup = createTopDataGroup();

		instanceProvider.MCR.assertMethodNotCalled("getRecordStorage");

		factory.factorUsingDataGroup(dataGroup);
		factory.factorUsingDataGroup(dataGroup);
		factory.factorUsingRecordTypeId("someRecordTypeId1");
		factory.factorUsingRecordTypeId("someRecordTypeId2");

		instanceProvider.MCR.assertNumberOfCallsToMethod("getRecordStorage", 1);
	}

	@Test
	public void testFactorUsingDataGroup() {
		DataGroupSpy dataGroup = createTopDataGroup();

		RecordTypeHandlerImp recordTypeHandler = (RecordTypeHandlerImp) factory
				.factorUsingDataGroup(dataGroup);

		assertSame(recordTypeHandler.onlyForTestGetRecordStorage(), recordStorage);
		assertSame(recordTypeHandler.getRecordTypeHandlerFactory(), factory);
	}

	private DataGroupSpy createTopDataGroup() {
		return new DataGroupSpy();
	}

	@Test
	public void testFactorUsingRecordTypeId() throws Exception {

		String recordTypeId = "someRecordTypeId";
		RecordTypeHandlerImp recordTypeHandler = (RecordTypeHandlerImp) factory
				.factorUsingRecordTypeId(recordTypeId);

		assertSame(recordTypeHandler.onlyForTestGetRecordStorage(), recordStorage);
		assertSame(recordTypeHandler.onlyForTestGetRecordTypeId(), recordTypeId);
		assertSame(recordTypeHandler.getRecordTypeHandlerFactory(), factory);
	}

}
