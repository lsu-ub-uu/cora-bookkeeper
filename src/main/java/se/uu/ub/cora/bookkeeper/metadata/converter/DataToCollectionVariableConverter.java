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

import se.uu.ub.cora.bookkeeper.metadata.CollectionVariable;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public final class DataToCollectionVariableConverter implements DataToMetadataConverter {
	private DataRecordGroup dataRecordGroup;

	private DataToCollectionVariableConverter(DataRecordGroup dataRecordGroup) {
		this.dataRecordGroup = dataRecordGroup;
	}

	public static DataToCollectionVariableConverter fromDataRecordGroup(
			DataRecordGroup dataRecordGroup) {
		return new DataToCollectionVariableConverter(dataRecordGroup);
	}

	@Override
	public CollectionVariable toMetadata() {
		String id = dataRecordGroup.getId();
		String nameInData = dataRecordGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = extractLinkedRecordIdByNameInData("textId");
		String defTextId = extractLinkedRecordIdByNameInData("defTextId");

		String refCollectionId = extractLinkedRecordIdByNameInData("refCollection");

		CollectionVariable collectionVariable = new CollectionVariable(id, nameInData, textId,
				defTextId, refCollectionId);
		possiblyConvertRefParentId(collectionVariable);
		possiblyConvertFinalValue(collectionVariable);
		possiblyConvertAttributeReferences(collectionVariable);
		return collectionVariable;
	}

	private String extractLinkedRecordIdByNameInData(String nameInData) {
		DataRecordLink textLink = dataRecordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				nameInData);
		return textLink.getLinkedRecordId();
	}

	private void possiblyConvertRefParentId(CollectionVariable collectionVariable) {
		if (dataRecordGroup.containsChildWithNameInData("refParentId")) {
			convertRefParentId(collectionVariable);
		}
	}

	private void convertRefParentId(CollectionVariable collectionVariable) {
		String refParentId = extractLinkedRecordIdByNameInData("refParentId");
		collectionVariable.setRefParentId(refParentId);
	}

	private void possiblyConvertFinalValue(CollectionVariable collectionVariable) {
		if (dataRecordGroup.containsChildWithNameInData("finalValue")) {
			String finalValue = dataRecordGroup.getFirstAtomicValueWithNameInData("finalValue");
			collectionVariable.setFinalValue(finalValue);
		}
	}

	private void possiblyConvertAttributeReferences(CollectionVariable collectionVariable) {
		if (dataRecordGroup.containsChildWithNameInData("attributeReferences")) {
			convertAndAddAttributeReferences(collectionVariable);
		}
	}

	private void convertAndAddAttributeReferences(CollectionVariable collectionVariable) {
		DataGroup attributeReferences = dataRecordGroup
				.getFirstGroupWithNameInData("attributeReferences");

		List<DataRecordLink> attributeRefs = attributeReferences
				.getChildrenOfTypeAndName(DataRecordLink.class, "ref");
		attributeRefs.forEach(ref -> {
			String refValue = ref.getLinkedRecordId();
			collectionVariable.addAttributeReference(refValue);
		});
	}

}
