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
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroupChild;

public final class DataGroupToMetadataGroupChildConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	public static DataGroupToMetadataGroupChildConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToMetadataGroupChildConverter(dataGroup);
	}

	private DataGroupToMetadataGroupChildConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public MetadataGroupChild toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");
		String parentId = dataGroup.getFirstAtomicValueWithNameInData("parentId");

		MetadataGroupChild metadataGroupChild = new MetadataGroupChild(id, nameInData, textId,
				defTextId, parentId);

		DataGroup attributeReferences = dataGroup.getFirstGroupWithNameInData("attributeReferences");
		for (DataElement dataElement : attributeReferences.getChildren()) {
			metadataGroupChild.addAttributeReference(((DataAtomic) dataElement).getValue());
		}

		// TODO: add childReferences using childReference converter
		DataGroup childReferences = dataGroup.getFirstGroupWithNameInData("childReferences");
		for (DataElement dataElement : childReferences.getChildren()) {
			DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
					.fromDataGroup((DataGroup) dataElement);
			metadataGroupChild.addChildReference(converter.toMetadata());
		}

		return metadataGroupChild;
	}

}
