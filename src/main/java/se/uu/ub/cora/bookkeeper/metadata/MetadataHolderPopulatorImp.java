/*
 * Copyright 2017, 2019, 2023 Uppsala University Library
 * Copyright 2025 Olov McKie
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

import java.util.Collection;

import se.uu.ub.cora.bookkeeper.metadata.converter.DataToMetadataConverter;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataToMetadataConverterProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataRecordGroup;

final class MetadataHolderPopulatorImp implements MetadataHolderPopulator {

	@Override
	public MetadataHolder createAndPopulateMetadataHolderFromMetadataStorage() {
		MetadataStorageView metadataStorageView = MetadataStorageProvider.getStorageView();
		MetadataHolder mh = new MetadataHolderImp();

		Collection<DataRecordGroup> metadataElementDataGroups = metadataStorageView
				.getMetadataElements();
		convertDataToMetadataElementsAndAddThemToMetadataHolder(metadataElementDataGroups, mh);
		return mh;
	}

	private void convertDataToMetadataElementsAndAddThemToMetadataHolder(
			Collection<DataRecordGroup> metadataElementDataRecordGroups, MetadataHolder mh) {
		for (DataRecordGroup metadataElement : metadataElementDataRecordGroups) {
			convertDataGroupToMetadataElementAndAddItToMetadataHolder(metadataElement, mh);
		}
	}

	private void convertDataGroupToMetadataElementAndAddItToMetadataHolder(
			DataRecordGroup metadataElement, MetadataHolder mh) {
		DataToMetadataConverter converter = DataToMetadataConverterProvider
				.getConverter(metadataElement);
		MetadataElement metadata = converter.toMetadata();
		mh.addMetadataElement(metadata);
	}
}
