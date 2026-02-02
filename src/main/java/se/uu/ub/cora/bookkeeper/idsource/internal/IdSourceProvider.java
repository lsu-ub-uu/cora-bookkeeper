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

import se.uu.ub.cora.bookkeeper.idsource.IdSource;
import se.uu.ub.cora.bookkeeper.idsource.IdSourceInstanceProvider;
import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.initialize.InitializedTypes;
import se.uu.ub.cora.initialize.ModuleInitializer;
import se.uu.ub.cora.initialize.ModuleInitializerImp;

/**
 * IdSourceProvider provides access to different types of idSources
 */
public class IdSourceProvider {
	private static InitializedTypes<IdSourceInstanceProvider> initializedTypes;
	private static ModuleInitializer moduleInitializer = new ModuleInitializerImp();

	private IdSourceProvider() {
		// prevent call to constructor
		throw new UnsupportedOperationException();
	}

	/**
	 * getStorageView returns a new IdSource that can be used by anything that needs an id
	 * generator.
	 * 
	 * @param recordType
	 *            An String indication what type of idSource is requested
	 * 
	 * @return A IdSource of the requested type.
	 */
	public static synchronized IdSource getIdSource(RecordType recordType) {
		locateAndChooseRecordStorageInstanceProvider();
		IdSourceInstanceProvider idSourceInstanceProvider = initializedTypes
				.getImplementationByType(recordType.idSource());
		return idSourceInstanceProvider.getIdSource(recordType);
	}

	private static void locateAndChooseRecordStorageInstanceProvider() {
		if (initializedTypes == null) {
			initializedTypes = moduleInitializer
					.loadOneImplementationOfEachType(IdSourceInstanceProvider.class);
		}
	}

	static void onlyForTestSetModuleInitializer(ModuleInitializer moduleInitializer) {
		IdSourceProvider.moduleInitializer = moduleInitializer;

	}

	static ModuleInitializer onlyForTestGetModuleInitializer() {
		return moduleInitializer;
	}

	/**
	 * onlyForTestSetInitializedTypes sets a InitializedTypes<IdSourceInstanceProvider> that
	 * contains one or several IdSourceInstanceProvider which are used to return instances of
	 * {@link IdSource} using {@link #getIdSource(RecordType)} method. This possibility to set a
	 * InitializedTypes is provided to enable testing of getting an idSource and is not intented to
	 * be used in production.
	 * <p>
	 * The InitializedTypes to use in production should be provided through an implementation of
	 * {@link InitializedTypes} in a seperate java module.
	 * 
	 * @param initializedTypes
	 *            A InitializedTypes to use to return IdSource instances for testing
	 */
	public static void onlyForTestSetInitializedTypes(
			InitializedTypes<IdSourceInstanceProvider> initializedTypes) {
		IdSourceProvider.initializedTypes = initializedTypes;
	}

}
