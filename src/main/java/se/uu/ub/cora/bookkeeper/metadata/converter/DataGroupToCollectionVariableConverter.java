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

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;

public final class DataGroupToCollectionVariableConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	private DataGroupToCollectionVariableConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public static DataGroupToCollectionVariableConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToCollectionVariableConverter(dataGroup);
	}

	@Override
	public CollectionVariable toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = extractTextIdByNameInData("textId");
		String defTextId = extractTextIdByNameInData("defTextId");
		
		DataGroup refCollection = (DataGroup) dataGroup.getFirstChildWithNameInData("refCollection");
		String refCollectionId = refCollection.getFirstAtomicValueWithNameInData("linkedRecordId");

		CollectionVariable collectionVariable = new CollectionVariable(id, nameInData, textId,
				defTextId, refCollectionId);
		convertRefParentId(collectionVariable);
		convertFinalValue(collectionVariable);
		return collectionVariable;
	}


	private String extractTextIdByNameInData(String nameInData) {
		DataGroup text = dataGroup.getFirstGroupWithNameInData(nameInData);
		return text.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private void convertRefParentId(CollectionVariable collectionVariable) {
		if (dataGroup.containsChildWithNameInData("refParentId")) {
			String refParentId = dataGroup.getFirstAtomicValueWithNameInData("refParentId");
			collectionVariable.setRefParentId(refParentId);
		}
	}

	private void convertFinalValue(CollectionVariable collectionVariable) {
		if (dataGroup.containsChildWithNameInData("finalValue")) {
			String finalValue = dataGroup.getFirstAtomicValueWithNameInData("finalValue");
			collectionVariable.setFinalValue(finalValue);
		}
	}

}
