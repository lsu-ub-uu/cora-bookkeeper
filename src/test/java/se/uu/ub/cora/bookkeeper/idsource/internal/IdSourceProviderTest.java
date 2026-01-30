/*
 * Copyright 2026 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.idsource.internal;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.idsource.IdSource;
import se.uu.ub.cora.bookkeeper.idsource.IdSourceInstanceProvider;
import se.uu.ub.cora.bookkeeper.idsource.IdSourceInstanceProviderSpy;
import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.initialize.ModuleInitializer;
import se.uu.ub.cora.initialize.ModuleInitializerImp;
import se.uu.ub.cora.initialize.spies.InitializedTypesSpy;
import se.uu.ub.cora.initialize.spies.ModuleInitializerSpy;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class IdSourceProviderTest {
	String idSourceType = "someType";

	LoggerFactory loggerFactory = new LoggerFactorySpy();
	private ModuleInitializerSpy moduleInitializerSpy;
	private InitializedTypesSpy<IdSourceInstanceProvider> initializedTypes;
	private IdSourceInstanceProviderSpy instanceProviderSpy;

	private RecordType recordType;

	@BeforeMethod
	public void beforeMethod() {

		createRecordTypeIdSource(idSourceType);

		LoggerProvider.setLoggerFactory(loggerFactory);
		IdSourceProvider.onlyForTestSetInitializedTypes(null);
	}

	private void createRecordTypeIdSource(String idSource) {
		boolean isPublic = true;
		boolean usePermissionUnit = true;
		boolean useVisibility = true;
		boolean useTrashBin = true;
		boolean storeInArchive = true;
		recordType = new RecordType("someRecordTypeId", "someDefinitionId", Optional.empty(),
				idSource, Optional.of("sequenceId"), Collections.emptyList(), isPublic,
				usePermissionUnit, useVisibility, useTrashBin, storeInArchive);
	}

	private void setupModuleInstanceProviderToReturnAppTokenStorageViewFactorySpy() {
		initializedTypes = new InitializedTypesSpy<>();
		moduleInitializerSpy = new ModuleInitializerSpy();
		instanceProviderSpy = new IdSourceInstanceProviderSpy();
		moduleInitializerSpy.MRV.setDefaultReturnValuesSupplier("loadOneImplementationOfEachType",
				() -> initializedTypes);
		initializedTypes.MRV.setDefaultReturnValuesSupplier("getImplementationByType",
				() -> instanceProviderSpy);

		IdSourceProvider.onlyForTestSetModuleInitializer(moduleInitializerSpy);
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<IdSourceProvider> constructor = IdSourceProvider.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<IdSourceProvider> constructor = IdSourceProvider.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testDefaultInitializerIsModuleInitalizer() {
		ModuleInitializer initializer = IdSourceProvider.onlyForTestGetModuleInitializer();
		assertNotNull(initializer);
		assertTrue(initializer instanceof ModuleInitializerImp);
	}

	@Test
	public void testGetRecordStorageIsSynchronized_toPreventProblemsWithFindingImplementations()
			throws Exception {
		Method getStorageView = IdSourceProvider.class.getMethod("getIdSource", RecordType.class);
		assertTrue(Modifier.isSynchronized(getStorageView.getModifiers()));
	}

	@Test
	public void testGetIdSourceUsesModuleInitializerToGetFactory() {
		setupModuleInstanceProviderToReturnAppTokenStorageViewFactorySpy();

		IdSource idSource = IdSourceProvider.getIdSource(recordType);

		moduleInitializerSpy.MCR.assertParameters("loadOneImplementationOfEachType", 0,
				IdSourceInstanceProvider.class);
		initializedTypes.MCR.assertCalledParameters("getImplementationByType", idSourceType);

		instanceProviderSpy.MCR.assertCalledParameters("getIdSource");
		instanceProviderSpy.MCR.assertReturn("getIdSource", 0, idSource);
	}

	@Test
	public void testOnlyForTestSetImplementationForTypes() {
		InitializedTypesSpy<IdSourceInstanceProvider> implementationForTypes2 = new InitializedTypesSpy<>();
		implementationForTypes2.MRV.setDefaultReturnValuesSupplier("getImplementationByType",
				IdSourceInstanceProviderSpy::new);
		IdSourceProvider.onlyForTestSetInitializedTypes(implementationForTypes2);

		IdSource idSource = IdSourceProvider.getIdSource(recordType);

		var idSourceInstanceProvider2 = (IdSourceInstanceProviderSpy) implementationForTypes2.MCR
				.assertCalledParametersReturn("getImplementationByType", idSourceType);
		idSourceInstanceProvider2.MCR.assertReturn("getIdSource", 0, idSource);
	}

	@Test
	public void testMultipleCallsToGetStorageViewOnlyLoadsImplementationsOnce() {
		setupModuleInstanceProviderToReturnAppTokenStorageViewFactorySpy();

		IdSourceProvider.getIdSource(recordType);
		IdSourceProvider.getIdSource(recordType);
		IdSourceProvider.getIdSource(recordType);
		IdSourceProvider.getIdSource(recordType);

		moduleInitializerSpy.MCR.assertNumberOfCallsToMethod("loadOneImplementationOfEachType", 1);
	}
}
