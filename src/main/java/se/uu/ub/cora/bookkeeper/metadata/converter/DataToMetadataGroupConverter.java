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

import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class DataToMetadataGroupConverter implements DataToMetadataConverter {
	protected DataRecordGroup dataRecordGroup;
	protected MetadataGroup metadataGroup;

	protected DataToMetadataGroupConverter(DataRecordGroup dataRecordGroup) {
		this.dataRecordGroup = dataRecordGroup;
	}

	public static DataToMetadataGroupConverter fromDataRecordGroup(
			DataRecordGroup dataRecordGroup) {
		return new DataToMetadataGroupConverter(dataRecordGroup);
	}

	@Override
	public MetadataGroup toMetadata() {
		createMetadataGroupWithBasicInfo();
		possiblyConvertRefParentId(metadataGroup);
		possiblyConvertAttributeReferences(metadataGroup);
		convertChildReferences();
		return metadataGroup;
	}

	private void createMetadataGroupWithBasicInfo() {
		String id = dataRecordGroup.getId();
		String nameInData = dataRecordGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = extractLinkedRecordIdByNameInData("textId");
		String defTextId = extractLinkedRecordIdByNameInData("defTextId");
		metadataGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(id, nameInData,
				textId, defTextId);
	}

	private String extractLinkedRecordIdByNameInData(String nameInData) {
		DataRecordLink textLink = dataRecordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				nameInData);
		return textLink.getLinkedRecordId();
	}

	private void possiblyConvertRefParentId(MetadataGroup metadataGroup) {
		if (dataRecordGroup.containsChildWithNameInData("refParentId")) {
			convertRefParentId(metadataGroup);
		}
	}

	private void convertRefParentId(MetadataGroup metadataGroup) {
		String refParentId = extractLinkedRecordIdByNameInData("refParentId");
		metadataGroup.setRefParentId(refParentId);
	}

	private void possiblyConvertAttributeReferences(MetadataGroup metadataGroup) {
		if (dataRecordGroup.containsChildWithNameInData("attributeReferences")) {
			convertAndAddAttributeReferences(metadataGroup);
		}
	}

	private void convertAndAddAttributeReferences(MetadataGroup metadataGroup) {
		DataGroup attributeReferences = dataRecordGroup
				.getFirstGroupWithNameInData("attributeReferences");

		List<DataRecordLink> attributeRefs = attributeReferences
				.getChildrenOfTypeAndName(DataRecordLink.class, "ref");
		attributeRefs.forEach(ref -> {
			String refValue = ref.getLinkedRecordId();
			metadataGroup.addAttributeReference(refValue);
		});
	}

	protected void convertChildReferences() {
		DataGroup childReferences = dataRecordGroup.getFirstGroupWithNameInData("childReferences");
		for (DataChild childReferenceElement : childReferences.getChildren()) {
			convertChildReference((DataGroup) childReferenceElement);
		}
	}

	private void convertChildReference(DataGroup childReference) {
		DataToMetadataChildReferenceConverter childConverter = DataToMetadataChildReferenceConverter
				.fromDataGroup(childReference);
		MetadataChildReference metadataChildReference = childConverter.toMetadata();
		metadataGroup.addChildReference(metadataChildReference);
	}

}
