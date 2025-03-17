/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.spy.DataToMetadataConverterFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToMetadataConverterProviderTest {

	private DataRecordGroupSpy dataRecordGroup;
	private DataToMetadataConverterFactorySpy dataGroupToMetadataConverterFactory;

	@BeforeMethod
	private void beforeMetohd() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "metadata");
		// dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getType", () -> "group");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of("group"), "type");
		dataGroupToMetadataConverterFactory = new DataToMetadataConverterFactorySpy();
	}

	@AfterMethod
	private void afterMethod() {
		DataToMetadataConverterProvider.onlyForTestSetDataGroupToMetadataConverterFactory(null);
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<DataToMetadataConverterProvider> constructor = DataToMetadataConverterProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<DataToMetadataConverterProvider> constructor = DataToMetadataConverterProvider.class
				.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testInit() {
		DataToMetadataConverter converter = DataToMetadataConverterProvider
				.getConverter(dataRecordGroup);

		assertTrue(converter instanceof DataToMetadataConverter);
	}

	@Test
	public void testConvertDataGroupToDataRecordGroup() {
		DataToMetadataConverterProvider.onlyForTestSetDataGroupToMetadataConverterFactory(
				dataGroupToMetadataConverterFactory);
		DataToMetadataConverter converterReturned = DataToMetadataConverterProvider
				.getConverter(dataRecordGroup);

		dataGroupToMetadataConverterFactory.MCR
				.assertCalledParameters("factorForDataContainingMetadata", dataRecordGroup);
		dataGroupToMetadataConverterFactory.MCR.assertReturn("factorForDataContainingMetadata", 0,
				converterReturned);
	}

	@Test
	public void testDataGroupToMetadataConverterFactory() {
		DataToMetadataConverterProvider.getConverter(dataRecordGroup);

		var factory = DataToMetadataConverterProvider
				.onlyForTestGetDataGroupToMetadataConverterFactory();

		assertTrue(factory instanceof DataToMetadataConverterFactoryImp);

	}
}
