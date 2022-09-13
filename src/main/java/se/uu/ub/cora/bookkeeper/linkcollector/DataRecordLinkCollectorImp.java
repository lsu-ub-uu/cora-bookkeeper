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
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.collected.RecordToRecordLink;
import se.uu.ub.cora.storage.MetadataStorage;

public class DataRecordLinkCollectorImp implements DataRecordLinkCollector {

	private MetadataStorage metadataStorage;
	private MetadataHolder metadataHolder;

	public DataRecordLinkCollectorImp(MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	@Override
	public List<RecordToRecordLink> collectLinks(String metadataId, DataGroup dataGroup, String fromRecordType,
			String fromRecordId) {
		getMetadataFromStorage();
		DataGroupRecordLinkCollector collector = new DataGroupRecordLinkCollector(metadataHolder,
				fromRecordType, fromRecordId);
		return collectLinksAndAddToDataGroup(metadataId, dataGroup,
				collector);
	}

	private DataGroup collectLinksAndAddToDataGroup(String metadataId, DataGroup dataGroup,
			DataGroupRecordLinkCollector collector) {
		List<DataGroup> collectedLinks = collector.collectLinks(metadataId, dataGroup);
		DataGroup collectedDataLinks = DataGroupProvider
				.getDataGroupUsingNameInData("collectedDataLinks");
		addLinksToDataGroup(collectedLinks, collectedDataLinks);
		return collectedDataLinks;
	}

	private void addLinksToDataGroup(List<DataGroup> collectedLinks, DataGroup collectedDataLinks) {
		for (DataGroup collectedLink : collectedLinks) {
			collectedDataLinks.addChild(collectedLink);
		}
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
