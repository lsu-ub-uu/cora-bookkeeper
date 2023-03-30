/*
 * Copyright 2023 Uppsala University Library
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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverter;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverterFactory;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverterFactoryImp;
import se.uu.ub.cora.bookkeeper.metadata.spy.DataGroupToMetadataConverterFactorySpy;
import se.uu.ub.cora.bookkeeper.metadata.spy.DataGroupToMetadataConverterSpy;
import se.uu.ub.cora.bookkeeper.metadata.spy.MetadataElementSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class MetadataHolderPopulatorTest {
	private MetadataHolderPopulatorImp populator;
	private MetadataStorageViewInstanceProviderSpy instanceProvider;
	private MetadataStorageViewSpy metadataStorageView;
	private List<DataGroup> metadataElementsAsDataGroup;
	private DataGroupToMetadataConverterFactorySpy factory;

	@BeforeMethod
	private void beforeMethod() {
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		setUpMetadataStorageProviderToReturnStorageViewSpy();

		populator = new MetadataHolderPopulatorImp();
		factory = new DataGroupToMetadataConverterFactorySpy();
		factory.MRV.setDefaultReturnValuesSupplier("factorForDataGroupContainingMetadata",
				() -> sup());
	}

	int noSups = 0;

	private DataGroupToMetadataConverter sup() {
		noSups++;
		DataGroupToMetadataConverterSpy dataGroupToMetadataConverterSpy = new DataGroupToMetadataConverterSpy();
		dataGroupToMetadataConverterSpy.MRV.setDefaultReturnValuesSupplier("toMetadata",
				() -> new MetadataElementSpy("someId" + noSups, null, null, null));
		return dataGroupToMetadataConverterSpy;
	}

	private void setUpMetadataStorageProviderToReturnStorageViewSpy() {
		instanceProvider = new MetadataStorageViewInstanceProviderSpy();
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instanceProvider);
		metadataStorageView = new MetadataStorageViewSpy();
		instanceProvider.MRV.setDefaultReturnValuesSupplier("getStorageView",
				() -> metadataStorageView);

		metadataElementsAsDataGroup = new ArrayList<>();
		metadataStorageView.MRV.setDefaultReturnValuesSupplier("getMetadataElements",
				() -> metadataElementsAsDataGroup);
		metadataElementsAsDataGroup.add(new DataGroupSpy());
		metadataElementsAsDataGroup.add(new DataGroupSpy());
	}

	@Test
	public void testDefaultDataGroupToMetadataConverter() throws Exception {
		DataGroupToMetadataConverterFactory factory = populator
				.onlyForTestGetDataGroupToMetadataConverterFactory();
		assertTrue(factory instanceof DataGroupToMetadataConverterFactoryImp);
	}

	@Test
	public void testOnlyForTestSetDataGroupToMetadataConverter() throws Exception {
		populator.onlyForTestSetDataGroupToMetadataConverter(factory);
		assertSame(populator.onlyForTestGetDataGroupToMetadataConverterFactory(), factory);
	}

	@Test
	public void testLazyInit() throws Exception {
		assertNothingIsInitialized();
	}

	private void assertNothingIsInitialized() {
		instanceProvider.MCR.assertMethodNotCalled("getStorageView");
	}

	@Test
	public void testCreateLoadsAndUsesMetadataStorage() throws Exception {
		populator.onlyForTestSetDataGroupToMetadataConverter(factory);

		MetadataHolder mh = populator.createAndPopulateMetadataHolderFromMetadataStorage();

		assertMetadataStorageCreatedAndUsed(mh);
	}

	private void assertMetadataStorageCreatedAndUsed(MetadataHolder mh) {
		instanceProvider.MCR.assertMethodWasCalled("getStorageView");

		metadataStorageView.MCR.assertMethodWasCalled("getMetadataElements");
		int no = 0;
		for (DataGroup dataGroup : metadataElementsAsDataGroup) {
			assertMetadataElementIsFactoredFromDataGroupAndAddedToReturnedMetadataHolder(mh, no, dataGroup);
			no++;
		}
	}

	private void assertMetadataElementIsFactoredFromDataGroupAndAddedToReturnedMetadataHolder(MetadataHolder mh, int no, DataGroup dataGroup) {
		factory.MCR.assertParameters("factorForDataGroupContainingMetadata", no, dataGroup);
		DataGroupToMetadataConverterSpy converterSpy = (DataGroupToMetadataConverterSpy) factory.MCR
				.getReturnValue("factorForDataGroupContainingMetadata", no);

		converterSpy.MCR.assertMethodWasCalled("toMetadata");
		MetadataElementSpy elementSpy = (MetadataElementSpy) converterSpy.MCR
				.getReturnValue("toMetadata", 0);
		assertEquals(mh.getMetadataElement(elementSpy.getId()), elementSpy);
	}

}
