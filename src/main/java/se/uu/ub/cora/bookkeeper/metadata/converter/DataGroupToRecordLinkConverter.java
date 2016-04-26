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
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;

public final class DataGroupToRecordLinkConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	public static DataGroupToRecordLinkConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToRecordLinkConverter(dataGroup);
	}

	private DataGroupToRecordLinkConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public RecordLink toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");

		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");
		String linkedRecordType = dataGroup.getFirstAtomicValueWithNameInData("linkedRecordType");

		RecordLink recordLink = RecordLink
				.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(id, nameInData, textId,
						defTextId, linkedRecordType);
		convertLinkedPathIfExists(recordLink);
		convertFinalValueIfExists(recordLink);
		convertRefParentId(recordLink);
		return recordLink;
	}

	private void convertLinkedPathIfExists(RecordLink recordLink) {
		if (dataGroup.containsChildWithNameInData("linkedPath")) {
			recordLink.setLinkedPath(dataGroup.getFirstGroupWithNameInData("linkedPath"));
		}
	}

	private void convertFinalValueIfExists(RecordLink recordLink) {
		if (dataGroup.containsChildWithNameInData("finalValue")) {
			recordLink.setFinalValue(dataGroup.getFirstAtomicValueWithNameInData("finalValue"));
		}
	}

	private void convertRefParentId(RecordLink recordLink) {
		if (dataGroup.containsChildWithNameInData("refParentId")) {
			String refParentId = dataGroup.getFirstAtomicValueWithNameInData("refParentId");
			recordLink.setRefParentId(refParentId);
		}
	}
}
