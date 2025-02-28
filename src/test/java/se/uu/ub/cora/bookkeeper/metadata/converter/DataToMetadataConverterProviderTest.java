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
package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.spy.DataGroupToMetadataConverterFactorySpy;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToMetadataConverterProviderTest {

	private DataFactorySpy dataFactory;
	private DataRecordGroupSpy dataRecordGroup;
	private DataGroupToMetadataConverterFactorySpy dataGroupToMetadataConverterFactory;

	@BeforeMethod
	private void beforeMetohd() {
		setUpDataFactory();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		dataRecordGroup = new DataRecordGroupSpy();
		dataGroupToMetadataConverterFactory = new DataGroupToMetadataConverterFactorySpy();
	}

	private void setUpDataFactory() {
		dataFactory = new DataFactorySpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorGroupFromDataRecordGroup",
				this::createDataGroupForConverter);
	}

	private DataGroupSpy createDataGroupForConverter() {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();
		dataGroupSpy.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "metadata");
		DataAttributeSpy dataAttributeSpy = new DataAttributeSpy();
		dataAttributeSpy.MRV.setDefaultReturnValuesSupplier("getValue", () -> "group");
		dataGroupSpy.MRV.setDefaultReturnValuesSupplier("getAttribute", () -> dataAttributeSpy);
		return dataGroupSpy;
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
		DataGroupToMetadataConverter converter = DataToMetadataConverterProvider
				.getConverter(dataRecordGroup);

		assertTrue(converter instanceof DataGroupToMetadataConverter);
	}

	@Test
	public void testConvertDataGroupToDataRecordGroup() {
		DataToMetadataConverterProvider.onlyForTestSetDataGroupToMetadataConverterFactory(
				dataGroupToMetadataConverterFactory);
		DataGroupToMetadataConverter converterReturned = DataToMetadataConverterProvider
				.getConverter(dataRecordGroup);

		var dataGroup = dataFactory.MCR
				.assertCalledParametersReturn("factorGroupFromDataRecordGroup", dataRecordGroup);
		dataGroupToMetadataConverterFactory.MCR
				.assertCalledParameters("factorForDataGroupContainingMetadata", dataGroup);
		dataGroupToMetadataConverterFactory.MCR.assertReturn("factorForDataGroupContainingMetadata",
				0, converterReturned);
	}

	@Test
	public void testDataGroupToMetadataConverterFactory() throws Exception {
		DataToMetadataConverterProvider.getConverter(dataRecordGroup);

		var factory = DataToMetadataConverterProvider
				.onlyForTestGetDataGroupToMetadataConverterFactory();

		assertTrue(factory instanceof DataGroupToMetadataConverterFactoryImp);

	}
}
