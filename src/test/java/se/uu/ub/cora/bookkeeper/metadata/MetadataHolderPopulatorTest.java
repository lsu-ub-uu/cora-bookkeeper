/*
 * Copyright 2023, 2025 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.converter.DataToMetadataConverter;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataToMetadataConverterProvider;
import se.uu.ub.cora.bookkeeper.metadata.spy.DataToMetadataConverterFactorySpy;
import se.uu.ub.cora.bookkeeper.metadata.spy.DataToMetadataConverterSpy;
import se.uu.ub.cora.bookkeeper.metadata.spy.MetadataElementSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class MetadataHolderPopulatorTest {
	private MetadataHolderPopulatorImp populator;
	private MetadataStorageViewInstanceProviderSpy instanceProvider;
	private MetadataStorageViewSpy metadataStorageView;
	private List<DataRecordGroup> metadataElementsAsDataRecordGroup;
	private DataToMetadataConverterFactorySpy factory;

	@BeforeMethod
	private void beforeMethod() {
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		setUpMetadataStorageProviderToReturnStorageViewSpy();

		populator = new MetadataHolderPopulatorImp();
		factory = new DataToMetadataConverterFactorySpy();
		factory.MRV.setDefaultReturnValuesSupplier("factorForDataContainingMetadata",
				this::createDataGroupToMetadataConverter);

		DataToMetadataConverterProvider.onlyForTestSetDataGroupToMetadataConverterFactory(factory);
	}

	@AfterMethod
	public void afterMethod() {
		DataToMetadataConverterProvider.onlyForTestSetDataGroupToMetadataConverterFactory(null);
	}

	int noSups = 0;

	private DataToMetadataConverter createDataGroupToMetadataConverter() {
		noSups++;
		DataToMetadataConverterSpy dataGroupToMetadataConverterSpy = new DataToMetadataConverterSpy();
		MetadataElementSpy metadataElementSpy = new MetadataElementSpy();
		String elementId = "someId" + noSups;
		metadataElementSpy.MRV.setDefaultReturnValuesSupplier("getId", () -> elementId);
		dataGroupToMetadataConverterSpy.MRV.setDefaultReturnValuesSupplier("toMetadata",
				() -> metadataElementSpy);
		return dataGroupToMetadataConverterSpy;
	}

	private void setUpMetadataStorageProviderToReturnStorageViewSpy() {
		instanceProvider = new MetadataStorageViewInstanceProviderSpy();
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instanceProvider);
		metadataStorageView = new MetadataStorageViewSpy();
		instanceProvider.MRV.setDefaultReturnValuesSupplier("getStorageView",
				() -> metadataStorageView);

		metadataElementsAsDataRecordGroup = new ArrayList<>();
		metadataStorageView.MRV.setDefaultReturnValuesSupplier("getMetadataElements",
				() -> metadataElementsAsDataRecordGroup);
		metadataElementsAsDataRecordGroup.add(new DataRecordGroupSpy());
		metadataElementsAsDataRecordGroup.add(new DataRecordGroupSpy());
	}

	@Test
	public void testLazyInit() {
		assertNothingIsInitialized();
	}

	private void assertNothingIsInitialized() {
		instanceProvider.MCR.assertMethodNotCalled("getStorageView");
	}

	@Test
	public void testCreateLoadsAndUsesMetadataStorage() {
		MetadataHolder mh = populator.createAndPopulateMetadataHolderFromMetadataStorage();

		assertMetadataStorageCreatedAndUsed(mh);
	}

	private void assertMetadataStorageCreatedAndUsed(MetadataHolder mh) {
		instanceProvider.MCR.assertMethodWasCalled("getStorageView");

		metadataStorageView.MCR.assertMethodWasCalled("getMetadataElements");
		int no = 0;
		for (DataRecordGroup dataRecordGroup : metadataElementsAsDataRecordGroup) {
			assertMetadataElementIsFactoredFromDataGroupAndAddedToReturnedMetadataHolder(mh, no,
					dataRecordGroup);
			no++;
		}
	}

	private void assertMetadataElementIsFactoredFromDataGroupAndAddedToReturnedMetadataHolder(
			MetadataHolder mh, int no, DataRecordGroup dataRecordGroup) {
		factory.MCR.assertParameters("factorForDataContainingMetadata", no, dataRecordGroup);
		DataToMetadataConverterSpy converterSpy = (DataToMetadataConverterSpy) factory.MCR
				.getReturnValue("factorForDataContainingMetadata", no);

		converterSpy.MCR.assertMethodWasCalled("toMetadata");
		MetadataElementSpy elementSpy = (MetadataElementSpy) converterSpy.MCR
				.getReturnValue("toMetadata", 0);
		assertEquals(mh.getMetadataElement(elementSpy.getId()), elementSpy);
	}

}
