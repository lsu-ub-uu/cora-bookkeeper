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
package se.uu.ub.cora.bookkeeper.text;

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

import se.uu.ub.cora.bookkeeper.decorator.TextHolderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewSpy;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class TextHolderProviderTest {
	@BeforeMethod
	public void beforeMethod() {
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		MetadataStorageViewInstanceProviderSpy instance = new MetadataStorageViewInstanceProviderSpy();
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instance);

	}

	@AfterMethod
	public void afterMethod() {
		resetTextHolderProviderToDefaultState();
	}

	private void resetTextHolderProviderToDefaultState() {
		TextHolderProvider.onlyForTestSetHolder(null);
		TextHolderProvider.onlyForTestSetTextHolderPopulator(null);
	}

	@Test
	public void testGetHolderWithVirtualThreads() throws Exception {
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		Future<TextHolder> future1 = executor.submit(TextHolderProvider::getHolder);
		Future<TextHolder> future2 = executor.submit(TextHolderProvider::getHolder);

		TextHolder holder1 = future1.get();
		TextHolder holder2 = future2.get();

		assertNotNull(holder1);
		assertNotNull(holder2);
		assertSame(holder1, holder2);

		executor.shutdown();
	}

	@Test
	public void testGetHolderWithVirtualThreads_synchronizeGetHolderAndDataChanged_delete()
			throws Exception {
		TextHolderPopulatorSpy textHolderPopulatorSpy = new TextHolderPopulatorSpy();
		textHolderPopulatorSpy.MRV.setDefaultReturnValuesSupplier(
				"createAndPopulateTextHolderFromTextStorage",
				this::createNewTextHolderSpyWithDelay);
		TextHolderProvider.onlyForTestSetTextHolderPopulator(textHolderPopulatorSpy);

		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		Future<?> getHolder = executor.submit(TextHolderProvider::getHolder);
		Future<?> dataChanged = executor
				.submit(() -> TextHolderProvider.dataChanged("someId", "delete"));

		waitToComplete(getHolder, dataChanged);

		TextHolderSpy holderSpy = (TextHolderSpy) textHolderPopulatorSpy.MCR
				.getReturnValue("createAndPopulateTextHolderFromMetadataStorage", 0);

		holderSpy.MCR.assertParameter("deleteTextElement", 0, "elementId", "someId");

		executor.shutdown();
	}

	private void waitToComplete(Future<?> future1, Future<?> future2)
			throws InterruptedException, ExecutionException {
		future1.get();
		future2.get();
	}

	private Object createNewTextHolderSpyWithDelay() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new TextHolderSpy();
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<TextHolderProvider> constructor = TextHolderProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<TextHolderProvider> constructor = TextHolderProvider.class
				.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testInit() {
		TextHolder textHolder = TextHolderProvider.getHolder();
		assertTrue(textHolder instanceof TextHolderImp);

		TextHolderPopulator textHolderPopulator = TextHolderProvider
				.onlyForTestGetTextHolderPopulator();
		assertTrue(textHolderPopulator instanceof TextHolderPopulatorImp);
	}

	@Test
	public void testOnlyForTestSetHolder() {
		TextHolder holder = new TextHolderSpy();
		TextHolderProvider.onlyForTestSetHolder(holder);

		assertSame(TextHolderProvider.getHolder(), holder);
	}

	@Test
	public void testOnlyOneInstanceOfTextHolder() {
		TextHolder textHolder1 = TextHolderProvider.getHolder();
		TextHolder textHolder2 = TextHolderProvider.getHolder();

		assertSame(textHolder1, textHolder2);
	}

	@Test
	public void testStartPopulateTextHolder() {
		TextHolderPopulatorSpy textHolderPopulatorSpy = new TextHolderPopulatorSpy();
		TextHolderProvider.onlyForTestSetTextHolderPopulator(textHolderPopulatorSpy);

		TextHolder textHolder = TextHolderProvider.getHolder();

		textHolderPopulatorSpy.MCR.assertReturn("createAndPopulateTextHolderFromMetadataStorage", 0,
				textHolder);
	}

	@Test
	public void testCallDataChanged_notLoaded_shouldNotThrowException() {
		TextHolderProvider.dataChanged("someId", "create");
		TextHolderProvider.dataChanged("someId", "update");
		TextHolderProvider.dataChanged("someId", "delete");

		assertTrue(true);
	}

	@Test
	public void testCallDataChanged_delete() {
		TextHolderSpy holder = new TextHolderSpy();
		TextHolderProvider.onlyForTestSetHolder(holder);

		TextHolderProvider.dataChanged("someId", "delete");

		holder.MCR.assertParameter("deleteTextElement", 0, "elementId", "someId");
	}

	@Test
	public void testCallDataChanged_create() {
		TextHolderSpy holder = new TextHolderSpy();
		TextHolderProvider.onlyForTestSetHolder(holder);
		MetadataStorageViewInstanceProviderSpy instanceProvider = new MetadataStorageViewInstanceProviderSpy();
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instanceProvider);

		TextHolderProvider.dataChanged("someId", "create");

		MetadataStorageViewSpy storageView = (MetadataStorageViewSpy) instanceProvider.MCR
				.getReturnValue("getStorageView", 0);

		storageView.MCR.assertMethodWasCalled("getTextElement");
		var textElement = storageView.MCR.assertCalledParametersReturn("getTextElement", "someId");
		holder.MCR.assertParameters("addTextElement", 0, textElement);
	}

	@Test
	public void testCallDataChanged_update() {
		TextHolderSpy holder = new TextHolderSpy();
		TextHolderProvider.onlyForTestSetHolder(holder);
		MetadataStorageViewInstanceProviderSpy instanceProvider = new MetadataStorageViewInstanceProviderSpy();
		MetadataStorageProvider.onlyForTestSetMetadataStorageViewInstanceProvider(instanceProvider);

		TextHolderProvider.dataChanged("someId", "update");

		MetadataStorageViewSpy storageView = (MetadataStorageViewSpy) instanceProvider.MCR
				.getReturnValue("getStorageView", 0);

		storageView.MCR.assertMethodWasCalled("getTextElement");
		var textElement = storageView.MCR.assertCalledParametersReturn("getTextElement", "someId");
		holder.MCR.assertParameters("addTextElement", 0, textElement);
	}
}
