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

import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public final class DataToRecordLinkConverter implements DataToMetadataConverter {
	private DataRecordGroup dataRecordGroup;
	private RecordLink recordLink;

	private DataToRecordLinkConverter(DataRecordGroup dataRecordGroup) {
		this.dataRecordGroup = dataRecordGroup;
	}

	public static DataToRecordLinkConverter fromDataRecordGroup(DataRecordGroup dataRecordGroup) {
		return new DataToRecordLinkConverter(dataRecordGroup);
	}

	@Override
	public RecordLink toMetadata() {
		createRecordLinkWithBasicInfo();
		convertLinkedPathIfExists();
		convertFinalValueIfExists();
		possiblyConvertRefParentId();
		possiblyConvertAttributeReferences(recordLink);
		return recordLink;
	}

	private void createRecordLinkWithBasicInfo() {
		String id = dataRecordGroup.getId();
		String nameInData = dataRecordGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = extractLinkedRecordIdByNameInData("textId");
		String defTextId = extractLinkedRecordIdByNameInData("defTextId");

		String linkedRecordType = extractLinkedRecordIdByNameInData("linkedRecordType");

		recordLink = RecordLink.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(id,
				nameInData, textId, defTextId, linkedRecordType);
	}

	private String extractLinkedRecordIdByNameInData(String nameInData) {
		DataRecordLink textLink = dataRecordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				nameInData);
		return textLink.getLinkedRecordId();
	}

	private void convertLinkedPathIfExists() {
		if (dataRecordGroup.containsChildWithNameInData("linkedPath")) {
			recordLink.setLinkedPath(dataRecordGroup.getFirstGroupWithNameInData("linkedPath"));
		}
	}

	private void convertFinalValueIfExists() {
		if (dataRecordGroup.containsChildWithNameInData("finalValue")) {
			recordLink
					.setFinalValue(dataRecordGroup.getFirstAtomicValueWithNameInData("finalValue"));
		}
	}

	private void possiblyConvertRefParentId() {
		if (dataRecordGroup.containsChildWithNameInData("refParentId")) {
			convertRefParentId();
		}
	}

	private void convertRefParentId() {
		String refParentId = extractLinkedRecordIdByNameInData("refParentId");
		recordLink.setRefParentId(refParentId);

	}

	private void possiblyConvertAttributeReferences(RecordLink recordLink) {
		if (dataRecordGroup.containsChildWithNameInData("attributeReferences")) {
			convertAndAddAttributeReferences(recordLink);
		}
	}

	private void convertAndAddAttributeReferences(RecordLink recordLink) {
		DataGroup attributeReferences = dataRecordGroup
				.getFirstGroupWithNameInData("attributeReferences");

		List<DataRecordLink> attributeRefs = attributeReferences
				.getChildrenOfTypeAndName(DataRecordLink.class, "ref");
		attributeRefs.forEach(ref -> {
			String refValue = ref.getLinkedRecordId();
			recordLink.addAttributeReference(refValue);
		});
	}
}
