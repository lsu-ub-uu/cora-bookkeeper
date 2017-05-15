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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;

public final class DataGroupToRecordLinkConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;
	private RecordLink recordLink;

	private DataGroupToRecordLinkConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public static DataGroupToRecordLinkConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToRecordLinkConverter(dataGroup);
	}

	@Override
	public RecordLink toMetadata() {
		createRecordLinkWithBasicInfo();
		convertLinkedPathIfExists();
		convertFinalValueIfExists();
		convertRefParentId();
		convertAttributeReferences();
		return recordLink;
	}

	private void createRecordLinkWithBasicInfo() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");

		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = extractTextIdByNameInData("textId");
		String defTextId = extractTextIdByNameInData("defTextId");

		DataGroup linkedRecordTypeGroup = dataGroup.getFirstGroupWithNameInData("linkedRecordType");
		String linkedRecordType = linkedRecordTypeGroup
				.getFirstAtomicValueWithNameInData("linkedRecordId");

		recordLink = RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(id,
				nameInData, textId, defTextId, linkedRecordType);
	}

	private String extractTextIdByNameInData(String nameInData) {
		DataGroup text = dataGroup.getFirstGroupWithNameInData(nameInData);
		return text.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private void convertLinkedPathIfExists() {
		if (dataGroup.containsChildWithNameInData("linkedPath")) {
			recordLink.setLinkedPath(dataGroup.getFirstGroupWithNameInData("linkedPath"));
		}
	}

	private void convertFinalValueIfExists() {
		if (dataGroup.containsChildWithNameInData("finalValue")) {
			recordLink.setFinalValue(dataGroup.getFirstAtomicValueWithNameInData("finalValue"));
		}
	}

	private void convertRefParentId() {
		if (dataGroup.containsChildWithNameInData("refParentId")) {
			String refParentId = dataGroup.getFirstAtomicValueWithNameInData("refParentId");
			recordLink.setRefParentId(refParentId);
		}
	}

	private void convertAttributeReferences() {
		if (dataGroup.containsChildWithNameInData("attributeReferences")) {
			DataGroup attributeReferences = dataGroup
					.getFirstGroupWithNameInData("attributeReferences");
			for (DataElement attributeReference : attributeReferences.getChildren()) {
				recordLink.addAttributeReference(((DataAtomic) attributeReference).getValue());
			}
		}
	}
}
