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
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;

public final class DataGroupToMetadataGroupConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;
	private MetadataGroup metadataGroup;

	public static DataGroupToMetadataGroupConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToMetadataGroupConverter(dataGroup);
	}

	private DataGroupToMetadataGroupConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public MetadataGroup toMetadata() {
		createMetadataGroupWithBasicInfo();
		convertAttributeReferences();
		convertChildReferences();
		return metadataGroup;
	}

	private void createMetadataGroupWithBasicInfo() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");
		metadataGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(id, nameInData, textId,
				defTextId);
	}

	private void convertAttributeReferences() {
		if (dataGroup.containsChildWithNameInData("attributeReferences")) {
			DataGroup attributeReferences = dataGroup
					.getFirstGroupWithNameInData("attributeReferences");
			for (DataElement attributeReference : attributeReferences.getChildren()) {
				metadataGroup.addAttributeReference(((DataAtomic) attributeReference).getValue());
			}
		}
	}

	private void convertChildReferences() {
		DataGroup childReferences = dataGroup.getFirstGroupWithNameInData("childReferences");
		for (DataElement childReferenceElement : childReferences.getChildren()) {
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
