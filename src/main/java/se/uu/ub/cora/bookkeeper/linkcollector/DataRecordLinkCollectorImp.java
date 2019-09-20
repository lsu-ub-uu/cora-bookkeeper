/*
 * Copyright 2015, 2017, 2019 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.linkcollector;

import java.util.List;

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderFromStoragePopulator;
import se.uu.ub.cora.data.DataFactoryProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.storage.MetadataStorage;

public class DataRecordLinkCollectorImp implements DataRecordLinkCollector {

	private MetadataStorage metadataStorage;
	private MetadataHolder metadataHolder;
	private DataFactoryProvider factoryProvider;

	public DataRecordLinkCollectorImp(MetadataStorage metadataStorage,
			DataFactoryProvider factoryProvider) {
		this.metadataStorage = metadataStorage;
		this.factoryProvider = factoryProvider;
	}

	@Override
	public DataGroup collectLinks(String metadataId, DataGroup dataGroup, String fromRecordType,
			String fromRecordId) {
		getMetadataFromStorage();
		DataGroupFactory dataGroupFactory = factoryProvider.getDataGroupFactory();
		// dataGroupFactory.factorUsingNameInData("");
		// DataGroup collectedDataLinks = DataGroup.withNameInData("collectedDataLinks");
		DataGroup collectedDataLinks = DataGroup.withNameInData("collectedDataLinks");
		DataGroupRecordLinkCollector collector = new DataGroupRecordLinkCollector(metadataHolder,
				fromRecordType, fromRecordId);
		List<DataGroup> collectedLinks = collector.collectLinks(metadataId, dataGroup);
		for (DataGroup collectedLink : collectedLinks) {
			collectedDataLinks.addChild(collectedLink);
		}
		return collectedDataLinks;
	}

	private void getMetadataFromStorage() {
		metadataHolder = new MetadataHolderFromStoragePopulator()
				.createAndPopulateMetadataHolderFromMetadataStorage(metadataStorage);
	}

	public MetadataStorage getMetadataStorage() {
		// needed for test
		return metadataStorage;
	}
}
