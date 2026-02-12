/*
 * Copyright 2026 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.AnyTypeRecordLink;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public final class DataToAnyTypeRecordLinkConverter implements DataToMetadataConverter {
	private DataRecordGroup dataRecordGroup;
	private AnyTypeRecordLink recordLink;

	private DataToAnyTypeRecordLinkConverter(DataRecordGroup dataRecordGroup) {
		this.dataRecordGroup = dataRecordGroup;
	}

	public static DataToAnyTypeRecordLinkConverter fromDataRecordGroup(
			DataRecordGroup dataRecordGroup) {
		return new DataToAnyTypeRecordLinkConverter(dataRecordGroup);
	}

	@Override
	public AnyTypeRecordLink toMetadata() {
		createRecordLinkWithBasicInfo();
		possiblyConvertAttributeReferences(recordLink);
		return recordLink;
	}

	private void createRecordLinkWithBasicInfo() {
		String id = dataRecordGroup.getId();
		String nameInData = dataRecordGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = extractLinkedRecordIdByNameInData("textId");
		String defTextId = extractLinkedRecordIdByNameInData("defTextId");

		recordLink = AnyTypeRecordLink.withIdAndNameInDataAndTextIdAndDefTextId(id, nameInData,
				textId, defTextId);
	}

	private String extractLinkedRecordIdByNameInData(String nameInData) {
		DataRecordLink textLink = dataRecordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				nameInData);
		return textLink.getLinkedRecordId();
	}

	private void possiblyConvertAttributeReferences(AnyTypeRecordLink recordLink) {
		if (dataRecordGroup.containsChildWithNameInData("attributeReferences")) {
			convertAndAddAttributeReferences(recordLink);
		}
	}

	private void convertAndAddAttributeReferences(AnyTypeRecordLink recordLink) {
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
