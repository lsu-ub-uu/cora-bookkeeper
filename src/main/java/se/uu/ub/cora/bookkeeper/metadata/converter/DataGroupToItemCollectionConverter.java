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

package se.uu.ub.cora.bookkeeper.metadata.converter;

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.ItemCollection;

public final class DataGroupToItemCollectionConverter implements DataGroupToMetadataConverter {

	public static DataGroupToItemCollectionConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToItemCollectionConverter(dataGroup);
	}

	private DataGroup dataGroup;

	private DataGroupToItemCollectionConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public ItemCollection toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");

		ItemCollection itemCollection = new ItemCollection(id, nameInData, textId, defTextId);

		DataGroup collectionItemReferences = dataGroup
				.getFirstGroupWithNameInData("collectionItemReferences");
		for (DataElement dataElement : collectionItemReferences.getChildren()) {
			DataGroup itemRefElement = (DataGroup)dataElement;
			String itemRefId = itemRefElement.getFirstAtomicValueWithNameInData("linkedRecordId");
			itemCollection.addItemReference(itemRefId);
		}

		return itemCollection;
	}

}
