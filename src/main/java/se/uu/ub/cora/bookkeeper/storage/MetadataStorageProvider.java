/*
 * Copyright 2022 Uppsala University Library
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
package se.uu.ub.cora.bookkeeper.storage;

import se.uu.ub.cora.initialize.ModuleInitializer;
import se.uu.ub.cora.initialize.ModuleInitializerImp;

/**
 * MetadataStorageProvider provides view access to the systems metadata stored in storage.
 */
public class MetadataStorageProvider {

	private static MetadataStorageViewInstanceProvider instanceProvider;
	private static ModuleInitializer moduleInitializer = new ModuleInitializerImp();

	private MetadataStorageProvider() {
		// prevent call to constructor
		throw new UnsupportedOperationException();
	}

	/**
	 * getStorageView returns a new MetadataStorageView that can be used by anything that needs
	 * access metadata data.
	 * <p>
	 * <i>Code using the returned MetadataStorageView instance MUST consider the returned instance
	 * as NOT thread safe.</i>
	 * 
	 * @return A MetadataStorageView that gives access to storage for metadata
	 */
	public static synchronized MetadataStorageView getStorageView() {
		locateAndChooseRecordStorageInstanceProvider();
		return instanceProvider.getStorageView();
	}

	private static void locateAndChooseRecordStorageInstanceProvider() {
		if (instanceProvider == null) {
			instanceProvider = moduleInitializer
					.loadOneImplementationBySelectOrder(MetadataStorageViewInstanceProvider.class);
		}
	}

	static void onlyForTestSetModuleInitializer(ModuleInitializer moduleInitializer) {
		MetadataStorageProvider.moduleInitializer = moduleInitializer;

	}

	static ModuleInitializer onlyForTestGetModuleInitializer() {
		return moduleInitializer;
	}

	/**
	 * onlyForTestSetMetadataStorageViewInstanceProvider sets a MetadataStorageViewInstanceProvider
	 * that will be used to return instances for the {@link #getStorageView()} method. This
	 * possibility to set a MetadataStorageViewInstanceProvider is provided to enable testing of
	 * getting a record storage in other classes and is not intented to be used in production.
	 * <p>
	 * The MetadataStorageViewInstanceProvider to use in production should be provided through an
	 * implementation of {@link MetadataStorageViewInstanceProvider} in a seperate java module.
	 * 
	 * @param instanceProvider
	 *            A MetadataStorageViewInstanceProvider to use to return MetadataStorageView
	 *            instances for testing
	 */
	public static void onlyForTestSetMetadataStorageViewInstanceProvider(
			MetadataStorageViewInstanceProvider instanceProvider) {
		MetadataStorageProvider.instanceProvider = instanceProvider;

	}

}
