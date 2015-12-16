/*
 * Copyright 2015 Uppsala University Library
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

import java.util.Collection;
import java.util.List;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverter;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverterFactory;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverterFactoryImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;

public class DataRecordLinkCollectorImp implements DataRecordLinkCollector {

	private MetadataStorage metadataStorage;
	private MetadataHolder metadataHolder;

	public DataRecordLinkCollectorImp(MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	public DataGroup collectLinks(String metadataId, DataGroup dataGroup, String fromRecordType,
			String fromRecordId) {
		getMetadataFromStorage();

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
		metadataHolder = new MetadataHolder();
		Collection<DataGroup> metadataElementDataGroups = metadataStorage.getMetadataElements();
		convertDataGroupsToMetadataElementsAndAddThemToMetadataHolder(metadataElementDataGroups);
	}

	private void convertDataGroupsToMetadataElementsAndAddThemToMetadataHolder(
			Collection<DataGroup> metadataElements) {
		for (DataGroup metadataElement : metadataElements) {
			convertDataGroupToMetadataElementAndAddItToMetadataHolder(metadataElement);
		}
	}

	private void convertDataGroupToMetadataElementAndAddItToMetadataHolder(
			DataGroup metadataElement) {
		DataGroupToMetadataConverterFactory factory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(metadataElement);
		DataGroupToMetadataConverter converter = factory.factor();
		metadataHolder.addMetadataElement(converter.toMetadata());
	}
}
