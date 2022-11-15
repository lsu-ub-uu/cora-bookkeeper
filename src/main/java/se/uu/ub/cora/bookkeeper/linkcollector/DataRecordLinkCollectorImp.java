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

import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolderFromStoragePopulator;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.collected.Link;

public class DataRecordLinkCollectorImp implements DataRecordLinkCollector {

	private MetadataStorageView metadataStorage;
	private MetadataHolder metadataHolder;

	public DataRecordLinkCollectorImp(MetadataStorageView metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	@Override
	public Set<Link> collectLinks(String metadataId, DataGroup dataGroup) {
		getMetadataFromStorage();
		DataGroupRecordLinkCollector collector = new DataGroupRecordLinkCollector(metadataHolder);
		return collectLinksAndAddToDataGroup(metadataId, dataGroup, collector);
	}

	private Set<Link> collectLinksAndAddToDataGroup(String metadataId, DataGroup dataGroup,
			DataGroupRecordLinkCollector collector) {
		return collector.collectLinks(metadataId, dataGroup);
	}

	private void getMetadataFromStorage() {
		metadataHolder = new MetadataHolderFromStoragePopulator()
				.createAndPopulateMetadataHolderFromMetadataStorage(metadataStorage);
	}

	public MetadataStorageView getMetadataStorage() {
		// needed for test
		return metadataStorage;
	}
}
