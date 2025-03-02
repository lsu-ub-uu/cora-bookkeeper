/*
 * Copyright 2015, 2019 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.metadata.converter;

import java.util.List;

import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public final class DataToItemCollectionConverter implements DataToMetadataConverter {
	private DataRecordGroup dataRecordGroup;

	private DataToItemCollectionConverter(DataRecordGroup dataRecordGroup) {
		this.dataRecordGroup = dataRecordGroup;
	}

	public static DataToItemCollectionConverter fromDataRecordGroup(
			DataRecordGroup dataRecordGroup) {
		return new DataToItemCollectionConverter(dataRecordGroup);
	}

	@Override
	public ItemCollection toMetadata() {
		String id = dataRecordGroup.getId();
		String nameInData = dataRecordGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = extractLinkedRecordIdByNameInData("textId");
		String defTextId = extractLinkedRecordIdByNameInData("defTextId");

		ItemCollection itemCollection = new ItemCollection(id, nameInData, textId, defTextId);
		possiblyConvertCollectionItemReferences(itemCollection);
		return itemCollection;
	}

	private String extractLinkedRecordIdByNameInData(String nameInData) {
		DataRecordLink textLink = dataRecordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				nameInData);
		return textLink.getLinkedRecordId();
	}

	private void possiblyConvertCollectionItemReferences(ItemCollection itemCollection) {
		if (dataRecordGroup.containsChildWithNameInData("collectionItemReferences")) {
			convertAndAddCollectionItemReferences(itemCollection);
		}
	}

	private void convertAndAddCollectionItemReferences(ItemCollection itemCollection) {
		DataGroup attributeReferences = dataRecordGroup
				.getFirstGroupWithNameInData("collectionItemReferences");

		List<DataRecordLink> attributeRefs = attributeReferences
				.getChildrenOfTypeAndName(DataRecordLink.class, "ref");
		attributeRefs.forEach(ref -> {
			String refValue = ref.getLinkedRecordId();
			itemCollection.addItemReference(refValue);
		});
	}
}
