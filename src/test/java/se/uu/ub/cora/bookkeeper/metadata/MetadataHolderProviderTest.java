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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.spy.MetadataHolderPopulatorSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
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
	public void testGetHolderWithVirtualThreads() throws Exception {
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		Future<MetadataHolder> future1 = executor.submit(MetadataHolderProvider::getHolder);
		Future<MetadataHolder> future2 = executor.submit(MetadataHolderProvider::getHolder);

		MetadataHolder holder1 = future1.get();
		MetadataHolder holder2 = future2.get();

		assertNotNull(holder1);
		assertNotNull(holder2);
		assertSame(holder1, holder2);

		executor.shutdown();
	}

	@Test
	public void testGetHolderWithVirtualThreads_synchronizeGetHolderAndDataChanged_delete()
			throws Exception {
		MetadataHolderPopulatorSpy metadataHolderPopulatorSpy = new MetadataHolderPopulatorSpy();
		metadataHolderPopulatorSpy.MRV.setDefaultReturnValuesSupplier(
				"createAndPopulateMetadataHolderFromMetadataStorage",
				this::createNewMetadataHolderSpyWithDelay);
		MetadataHolderProvider.onlyForTestSetMetadataHolderPopulator(metadataHolderPopulatorSpy);

		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		Future<?> getHolder = executor.submit(MetadataHolderProvider::getHolder);
		Future<?> dataChanged = executor
				.submit(() -> MetadataHolderProvider.dataChanged("someId", "delete"));

		waitToComplete(getHolder, dataChanged);

		MetadataHolderSpy holderSpy = (MetadataHolderSpy) metadataHolderPopulatorSpy.MCR
				.getReturnValue("createAndPopulateMetadataHolderFromMetadataStorage", 0);

		holderSpy.MCR.assertParameter("deleteMetadataElement", 0, "elementId", "someId");

		executor.shutdown();
	}

	private void waitToComplete(Future<?> future1, Future<?> future2)
			throws InterruptedException, ExecutionException {
		future1.get();
		future2.get();
	}

	private Object createNewMetadataHolderSpyWithDelay() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new MetadataHolderSpy();
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

	@Test
	public void testCallDataChanged_notLoaded_shouldNotThrowException() {
		MetadataHolderProvider.dataChanged("someId", "create");
		MetadataHolderProvider.dataChanged("someId", "update");
		MetadataHolderProvider.dataChanged("someId", "delete");

		assertTrue(true);
	}

	@Test
	public void testCallDataChanged_delete() {
		MetadataHolderSpy holder = new MetadataHolderSpy();
		MetadataHolderProvider.onlyForTestSetHolder(holder);

		MetadataHolderProvider.dataChanged("someId", "delete");

		holder.MCR.assertParameter("deleteMetadataElement", 0, "elementId", "someId");
	}

	@Test
	public void testCallDataChanged_create() {
		MetadataHolderSpy holder = new MetadataHolderSpy();
		MetadataHolderProvider.onlyForTestSetHolder(holder);
		MetadataStorageViewInstanceProviderSpy instanceProvider = new MetadataStorageViewInstanceProviderSpy();
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instanceProvider);

		MetadataHolderProvider.dataChanged("someId", "create");

		MetadataStorageViewSpy storageView = (MetadataStorageViewSpy) instanceProvider.MCR
				.getReturnValue("getStorageView", 0);

		storageView.MCR.assertMethodWasCalled("getMetadataElement");
		var metadataElement = storageView.MCR.assertCalledParametersReturn("getMetadataElement",
				"someId");
		holder.MCR.assertParameters("addMetadataElement", 0, metadataElement);
	}

	@Test
	public void testCallDataChanged_update() {
		MetadataHolderSpy holder = new MetadataHolderSpy();
		MetadataHolderProvider.onlyForTestSetHolder(holder);
		MetadataStorageViewInstanceProviderSpy instanceProvider = new MetadataStorageViewInstanceProviderSpy();
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instanceProvider);

		MetadataHolderProvider.dataChanged("someId", "update");

		MetadataStorageViewSpy storageView = (MetadataStorageViewSpy) instanceProvider.MCR
				.getReturnValue("getStorageView", 0);

		storageView.MCR.assertMethodWasCalled("getMetadataElement");
		var metadataElement = storageView.MCR.assertCalledParametersReturn("getMetadataElement",
				"someId");
		holder.MCR.assertParameters("addMetadataElement", 0, metadataElement);
	}
}
