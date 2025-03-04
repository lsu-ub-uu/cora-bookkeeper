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
package se.uu.ub.cora.bookkeeper.metadata;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.spy.MetadataHolderPopulatorSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class MetadataHolderProviderTest {
	@BeforeMethod
	public void beforeMethod() {
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		MetadataStorageViewInstanceProviderSpy instance = new MetadataStorageViewInstanceProviderSpy();
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instance);

	}

	@AfterMethod
	public void afterMethod() {
		resetMetadataHolderProviderToDefaultState();
	}

	private void resetMetadataHolderProviderToDefaultState() {
		MetadataHolderProvider.onlyForTestSetHolder(null);
		MetadataHolderProvider.onlyForTestSetMetadataHolderPopulator(null);
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<MetadataHolderProvider> constructor = MetadataHolderProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<MetadataHolderProvider> constructor = MetadataHolderProvider.class
				.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testInit() {
		MetadataHolder metadataHolder = MetadataHolderProvider.getHolder();
		assertTrue(metadataHolder instanceof MetadataHolderImp);

		MetadataHolderPopulator metadataHolderPopulator = MetadataHolderProvider
				.onlyForTestGetMetadataHolderPopulator();
		assertTrue(metadataHolderPopulator instanceof MetadataHolderPopulatorImp);
	}

	@Test
	public void testOnlyForTestSetHolder() {
		MetadataHolder holder = new MetadataHolderSpy();
		MetadataHolderProvider.onlyForTestSetHolder(holder);

		assertSame(MetadataHolderProvider.getHolder(), holder);
	}

	@Test
	public void testOnlyOneInstanceOfMetadataHolder() {
		MetadataHolder metadataHolder1 = MetadataHolderProvider.getHolder();
		MetadataHolder metadataHolder2 = MetadataHolderProvider.getHolder();

		assertSame(metadataHolder1, metadataHolder2);
	}

	@Test
	public void testStartPopulateMetadataHolder() {
		MetadataHolderPopulatorSpy metadataHolderPopulatorSpy = new MetadataHolderPopulatorSpy();
		MetadataHolderProvider.onlyForTestSetMetadataHolderPopulator(metadataHolderPopulatorSpy);

		MetadataHolder metadataHolder = MetadataHolderProvider.getHolder();

		metadataHolderPopulatorSpy.MCR.assertReturn(
				"createAndPopulateMetadataHolderFromMetadataStorage", 0, metadataHolder);
	}
}
