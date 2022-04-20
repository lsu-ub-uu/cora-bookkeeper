/*
 * Copyright 2015, 2019 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupToMetadataGroupConverter implements DataGroupToMetadataConverter {

	private static final String LINKED_RECORD_ID = "linkedRecordId";
	protected DataGroup dataGroup;
	protected MetadataGroup metadataGroup;

	protected DataGroupToMetadataGroupConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public static DataGroupToMetadataGroupConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToMetadataGroupConverter(dataGroup);
	}

	@Override
	public MetadataGroup toMetadata() {
		createMetadataGroupWithBasicInfo();
		convertRefParentId();
		convertAttributeReferences();
		convertChildReferences();
		return metadataGroup;
	}

	private void createMetadataGroupWithBasicInfo() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = extractTextIdByNameInData("textId");
		String defTextId = extractTextIdByNameInData("defTextId");
		metadataGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(id, nameInData,
				textId, defTextId);
	}

	private String extractTextIdByNameInData(String nameInData) {
		DataGroup text = dataGroup.getFirstGroupWithNameInData(nameInData);
		return text.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	protected void convertRefParentId() {
		if (dataGroup.containsChildWithNameInData("refParentId")) {
			DataGroup refParentGroup = dataGroup.getFirstGroupWithNameInData("refParentId");
			metadataGroup.setRefParentId(
					refParentGroup.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID));
		}
	}

	protected void convertAttributeReferences() {
		if (dataGroup.containsChildWithNameInData("attributeReferences")) {
			DataGroup attributeReferences = dataGroup
					.getFirstGroupWithNameInData("attributeReferences");
			for (DataGroup attributeReference : attributeReferences
					.getAllGroupsWithNameInData("ref")) {
				String refValue = attributeReference
						.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
				metadataGroup.addAttributeReference(refValue);
			}
		}
	}

	protected void convertChildReferences() {
		DataGroup childReferences = dataGroup.getFirstGroupWithNameInData("childReferences");
		for (DataChild childReferenceElement : childReferences.getChildren()) {
			convertChildReference((DataGroup) childReferenceElement);
		}
	}

	private void convertChildReference(DataGroup childReference) {
		DataGroupToMetadataChildReferenceConverter childConverter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(childReference);
		MetadataChildReference metadataChildReference = childConverter.toMetadata();
		metadataGroup.addChildReference(metadataChildReference);
	}

}
