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

import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;

public class MetadataHolderProvider {

	private MetadataHolderProvider() {
		// not called
		throw new UnsupportedOperationException();
	}

	private static MetadataHolder metadataHolder;
	private static MetadataHolderPopulator metadataHolderPopulator;

	public static MetadataHolder getHolder() {
		synchronized (MetadataHolderProvider.class) {
			startMetadtaHolderIfNotStarted();
			return metadataHolder;
		}
	}

	private static void startMetadtaHolderIfNotStarted() {
		if (metadataHolder == null) {
			if (metadataHolderPopulator == null) {
				metadataHolderPopulator = new MetadataHolderPopulatorImp();
			}
			metadataHolder = metadataHolderPopulator
					.createAndPopulateMetadataHolderFromMetadataStorage();
		}
	}

	/**
	 * dataChanged method is intended to inform the instance provider about data that is changed in
	 * storage. This is to make it possible to implement a cached storage and update relevant
	 * records when data is changed. This change can be done by processes running in the same system
	 * or by processes running on other servers.
	 * 
	 * @param id
	 *            A String with the records id
	 * @param action
	 *            A String with the action of how the data was changed ("create", "update" or
	 *            "delete").
	 */
	public static void dataChanged(String id, String action) {
		dataChangedSynchonizedWithGetHolder(id, action);
	}

	private static void dataChangedSynchonizedWithGetHolder(String id, String action) {
		synchronized (MetadataHolderProvider.class) {
			possiblyUpdateMetadataHolderWithLatestDataChanges(id, action);
		}
	}

	private static void possiblyUpdateMetadataHolderWithLatestDataChanges(String id,
			String action) {
		if (null != metadataHolder) {
			updateMetadataHolderWithLatestDataChanges(id, action);
		}
	}

	private static void updateMetadataHolderWithLatestDataChanges(String id, String action) {
		if ("delete".equals(action)) {
			metadataHolder.deleteMetadataElement(id);
		} else {
			updateMetadataHolderWithLatestDataFromStorage(id);
		}
	}

	private static void updateMetadataHolderWithLatestDataFromStorage(String id) {
		MetadataStorageView storageView = MetadataStorageProvider.getStorageView();
		MetadataElement metadataElement = storageView.getMetadataElement(id);
		metadataHolder.addMetadataElement(metadataElement);
	}

	/**
	 * Sets a MetadataHolder that will be returned. This possibility to set a MetadataHolder is
	 * provided to enable testing of using the MetadataHolder in other classes and is not intented
	 * to be used in production.
	 * 
	 * @param metadataHolder
	 *            A MetadataHolder to use return to the caller of getHolder
	 */
	public static void onlyForTestSetHolder(MetadataHolder metadataHolderIn) {
		metadataHolder = metadataHolderIn;
	}

	static void onlyForTestSetMetadataHolderPopulator(
			MetadataHolderPopulator metadataHolderPopulatorIn) {
		metadataHolderPopulator = metadataHolderPopulatorIn;

	}

	static MetadataHolderPopulator onlyForTestGetMetadataHolderPopulator() {
		return metadataHolderPopulator;
	}
}
