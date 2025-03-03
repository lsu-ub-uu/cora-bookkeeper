/*
 * Copyright 2021, 2023 Uppsala University Library
 * Copyright 2025 Olov McKie
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
package se.uu.ub.cora.bookkeeper.validator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderProvider;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class DataValidatorFactoryTest {
	private DataValidatorFactoryImp factory;

	private MetadataStorageViewSpy metadataStorageView;
	private MetadataStorageViewInstanceProviderSpy metaStorageInstanceProviderSpy;
	private MetadataHolder metadataHolder;

	private DataGroupSpy recordTypeGroup1;
	private DataGroupSpy recordTypeGroup2;

	@BeforeMethod
	private void beforeMethod() {
		LoggerFactorySpy loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);

		metadataHolder = new MetadataHolderSpy();
		MetadataHolderProvider.onlyForTestSetHolder(metadataHolder);

		metadataStorageView = new MetadataStorageViewSpy();
		metaStorageInstanceProviderSpy = new MetadataStorageViewInstanceProviderSpy();
		metaStorageInstanceProviderSpy.MRV.setDefaultReturnValuesSupplier("getStorageView",
				() -> metadataStorageView);

		MetadataStorageProvider
				.onlyForTestSetMetadataStorageViewInstanceProvider(metaStorageInstanceProviderSpy);
		factory = new DataValidatorFactoryImp();
	}

	@Test
	public void testFactorNoRecordTypes() {

		DataValidatorImp dataValidator = (DataValidatorImp) factory.factor();

		assertTrue(dataValidator instanceof DataValidatorImp);

		assertDataElementValidatorFactory(dataValidator);
		assertDataValidatorCreatedWithCorrectInput(dataValidator);
	}

	private void assertDataValidatorCreatedWithCorrectInput(DataValidatorImp dataValidator) {
		Map<String, DataGroup> recordTypeHolder = dataValidator.onlyForTestGetRecordTypeHolder();
		assertTrue(recordTypeHolder instanceof HashMap<String, DataGroup>);
		assertEquals(recordTypeHolder.size(), 0);
	}

	private void assertDataElementValidatorFactory(DataValidatorImp dataValidator) {
		DataElementValidatorFactoryImp dataElementValidatorFactory = (DataElementValidatorFactoryImp) dataValidator
				.onlyForTestGetDataElementValidatorFactory();
		Map<String, DataGroup> recordTypeHolder = dataElementValidatorFactory
				.onlyForTestGetRecordTypeHolder();

		assertTrue(dataElementValidatorFactory instanceof DataElementValidatorFactoryImp);
		assertTrue(recordTypeHolder instanceof HashMap<String, DataGroup>);
		assertEquals(recordTypeHolder.size(), 0);

		assertSame(dataElementValidatorFactory.onlyForTestGetMetadataHolder(), metadataHolder);
	}

	@Test
	public void testCreateRecordTypeHolder() {

		prepareTestAddRecordTypesForCreationOfRecordTypeHolder();

		DataValidatorImp dataValidator = (DataValidatorImp) factory.factor();

		recordTypeGroup1.MCR.assertParameters("getFirstGroupWithNameInData", 0, "recordInfo");
		DataGroupSpy recordInfo1 = (DataGroupSpy) recordTypeGroup1.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		recordInfo1.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "id");

		recordTypeGroup2.MCR.assertParameters("getFirstGroupWithNameInData", 0, "recordInfo");
		DataGroupSpy recordInfo2 = (DataGroupSpy) recordTypeGroup2.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);
		recordInfo2.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "id");

		DataElementValidatorFactoryImp dataElementValidatorFactory = (DataElementValidatorFactoryImp) dataValidator
				.onlyForTestGetDataElementValidatorFactory();
		;
		Map<String, DataGroup> recordTypeHolderFromDataValidator = dataValidator
				.onlyForTestGetRecordTypeHolder();
		assertEquals(recordTypeHolderFromDataValidator.size(), 2);
		assertSame(recordTypeHolderFromDataValidator.get("someId1"), recordTypeGroup1);
		assertSame(recordTypeHolderFromDataValidator.get("someId2"), recordTypeGroup2);

		Map<String, DataGroup> recordTypeHolderFromDataElementValidatorFactory = dataElementValidatorFactory
				.onlyForTestGetRecordTypeHolder();
		assertEquals(recordTypeHolderFromDataElementValidatorFactory.get("someId1"),
				recordTypeGroup1);
		assertEquals(recordTypeHolderFromDataElementValidatorFactory.get("someId2"),
				recordTypeGroup2);
	}

	private void prepareTestAddRecordTypesForCreationOfRecordTypeHolder() {
		recordTypeGroup1 = new DataGroupSpy();
		DataGroupSpy recordInfoGroup1 = new DataGroupSpy();
		recordInfoGroup1.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "someId1", "id");
		recordTypeGroup1.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> recordInfoGroup1, "recordInfo");

		recordTypeGroup2 = new DataGroupSpy();
		DataGroupSpy recordInfoGroup2 = new DataGroupSpy();
		recordInfoGroup2.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "someId2", "id");
		recordTypeGroup2.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> recordInfoGroup2, "recordInfo");

		metadataStorageView.MRV.setDefaultReturnValuesSupplier("getRecordTypes",
				() -> List.of(recordTypeGroup1, recordTypeGroup2));
	}

}
