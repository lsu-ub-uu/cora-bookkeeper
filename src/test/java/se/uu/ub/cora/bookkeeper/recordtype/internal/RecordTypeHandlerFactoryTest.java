/*
 * Copyright 2020, 2023 Uppsala University Library
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.DataMissingException;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandlerFactory;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandlerFactoryImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.storage.spies.RecordStorageInstanceProviderSpy;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class RecordTypeHandlerFactoryTest {

	private RecordStorageSpy recordStorage;
	private RecordTypeHandlerFactoryImp factory;
	private RecordStorageInstanceProviderSpy instanceProvider;
	private MetadataStorageViewSpy metadataStorageViewSpy;
	private MetadataStorageViewInstanceProviderSpy metaStorageInstProviderSpy;

	@BeforeMethod
	private void beforeMethod() {
		LoggerFactorySpy loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);

		instanceProvider = new RecordStorageInstanceProviderSpy();
		RecordStorageProvider.onlyForTestSetRecordStorageInstanceProvider(instanceProvider);

		recordStorage = new RecordStorageSpy();
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", DataGroupSpy::new);
		instanceProvider.MRV.setDefaultReturnValuesSupplier("getRecordStorage",
				() -> recordStorage);

		metadataStorageViewSpy = new MetadataStorageViewSpy();
		metadataStorageViewSpy.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> Optional.of(new ValidationType("someTypeToValidate", "someCreateDefinitionId",
						"someUpdateDefinitionId")));

		metaStorageInstProviderSpy = new MetadataStorageViewInstanceProviderSpy();
		metaStorageInstProviderSpy.MRV.setDefaultReturnValuesSupplier("getStorageView",
				() -> metadataStorageViewSpy);
		MetadataStorageProvider
				.onlyForTestSetMetadataStorageViewInstanceProvider(metaStorageInstProviderSpy);

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
		factory.factorUsingDataRecordGroup(new DataRecordGroupSpy());
		factory.factorUsingDataRecordGroup(new DataRecordGroupSpy());

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

	@Test
	public void testFactorUsingDataRecordGroupWithValidationType() throws Exception {
		String validationTypeId = "someValidationTypeId";
		DataRecordGroupSpy dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getValidationType",
				() -> validationTypeId);

		RecordTypeHandlerFactory factoryInterface = factory;
		RecordTypeHandlerImp recordTypeHandler = (RecordTypeHandlerImp) factoryInterface
				.factorUsingDataRecordGroup(dataRecordGroup);

		assertSame(recordTypeHandler.getRecordTypeHandlerFactory(), factory);
		assertSame(recordTypeHandler.onlyForTestGetRecordStorage(), recordStorage);
		assertSame(recordTypeHandler.onlyForTestGetMetadataStorage(), metadataStorageViewSpy);
		assertSame(recordTypeHandler.onlyForTestGetValidationTypeId(), validationTypeId);
	}

	@Test
	public void testFactorUsingDataRecordGroupWithoutValidationTypeSholdThrowError()
			throws Exception {
		DataRecordGroupSpy dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setAlwaysThrowException("getValidationType",
				new se.uu.ub.cora.data.DataMissingException("someMessage"));

		RecordTypeHandlerFactory factoryInterface = factory;
		try {
			factoryInterface.factorUsingDataRecordGroup(dataRecordGroup);
			fail("an exception should have been thrown");
		} catch (Exception e) {
			assertTrue(e instanceof DataMissingException);
			assertEquals(e.getMessage(),
					"RecordTypeHandler could not be created because of missing data: someMessage");
		}
	}

	@Test
	public void testMetadatatorageLoadedOnceOnFirstCallToFactor() throws Exception {
		metaStorageInstProviderSpy.MCR.assertMethodNotCalled("getStorageView");

		factory.factorUsingDataRecordGroup(new DataRecordGroupSpy());
		factory.factorUsingDataRecordGroup(new DataRecordGroupSpy());

		metaStorageInstProviderSpy.MCR.assertNumberOfCallsToMethod("getStorageView", 1);
	}
}