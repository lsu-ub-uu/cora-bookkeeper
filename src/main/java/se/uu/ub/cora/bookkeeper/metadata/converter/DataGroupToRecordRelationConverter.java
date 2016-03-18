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
import se.uu.ub.cora.bookkeeper.metadata.RecordRelation;

public class DataGroupToRecordRelationConverter extends DataGroupToMetadataGroupConverter {

	public static DataGroupToRecordRelationConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToRecordRelationConverter(dataGroup);
	}

	private DataGroupToRecordRelationConverter(DataGroup dataGroup) {
		super(dataGroup);
	}

	@Override
	public RecordRelation toMetadata() {
		createMetadataGroupWithBasicInfo();
		convertRefParentId();
		convertAttributeReferences();
		convertChildReferences();
		return (RecordRelation) metadataGroup;
	}

	private void createMetadataGroupWithBasicInfo() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");
		String refrecordLinkId = dataGroup
				.getFirstAtomicValueWithNameInData("refrecordLinkId");
		metadataGroup = RecordRelation
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRefrecordLinkId(id, nameInData,
						textId, defTextId, refrecordLinkId);
	}
}
