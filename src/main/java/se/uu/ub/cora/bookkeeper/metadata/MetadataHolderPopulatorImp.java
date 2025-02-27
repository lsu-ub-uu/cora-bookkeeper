/*
 * Copyright 2017, 2019, 2023 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverter;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverterFactory;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataGroupToMetadataConverterFactoryImp;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataGroup;

public final class MetadataHolderPopulatorImp implements MetadataHolderPopulator {

	DataGroupToMetadataConverterFactory factory = DataGroupToMetadataConverterFactoryImp
			.forDataGroups();

	@Override
	public MetadataHolder createAndPopulateMetadataHolderFromMetadataStorage() {
		long start = System.currentTimeMillis();
		MetadataStorageView metadataStorageView = MetadataStorageProvider.getStorageView();
		MetadataHolder mh = new MetadataHolderImp();
		Collection<DataGroup> metadataElementDataGroups = metadataStorageView.getMetadataElements();
		convertDataGroupsToMetadataElementsAndAddThemToMetadataHolder(metadataElementDataGroups,
				mh);

		long end = System.currentTimeMillis();
		long difference = end - start;
		System.out.println("READ METADATA!!!, Total time (reading and converting): " + difference);
		return mh;
	}

	private void convertDataGroupsToMetadataElementsAndAddThemToMetadataHolder(
			Collection<DataGroup> metadataElements, MetadataHolder mh) {
		for (DataGroup metadataElement : metadataElements) {
			convertDataGroupToMetadataElementAndAddItToMetadataHolder(metadataElement, mh);
		}
	}

	private void convertDataGroupToMetadataElementAndAddItToMetadataHolder(
			DataGroup metadataElement, MetadataHolder mh) {
		DataGroupToMetadataConverter converter = factory
				.factorForDataGroupContainingMetadata(metadataElement);
		mh.addMetadataElement(converter.toMetadata());
	}

	DataGroupToMetadataConverterFactory onlyForTestGetDataGroupToMetadataConverterFactory() {
		return factory;
	}

	void onlyForTestSetDataGroupToMetadataConverter(
			DataGroupToMetadataConverterFactory factorySpy) {
		this.factory = factorySpy;
	}
}
