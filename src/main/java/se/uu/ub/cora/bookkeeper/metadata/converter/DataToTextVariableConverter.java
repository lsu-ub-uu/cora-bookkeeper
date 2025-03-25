/*
 * Copyright 2015, 2019, 2022 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.TextVariable;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public final class DataToTextVariableConverter implements DataToMetadataConverter {
	private DataRecordGroup dataRecordGroup;

	private DataToTextVariableConverter(DataRecordGroup dataRecordGroup) {
		this.dataRecordGroup = dataRecordGroup;
	}

	public static DataToTextVariableConverter fromDataRecordGroup(DataRecordGroup dataRecordGroup) {
		return new DataToTextVariableConverter(dataRecordGroup);
	}

	@Override
	public TextVariable toMetadata() {
		String id = dataRecordGroup.getId();
		String nameInData = dataRecordGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = extractLinkedRecordIdByNameInData("textId");
		String defTextId = extractLinkedRecordIdByNameInData("defTextId");
		String regularExpression = dataRecordGroup.getFirstAtomicValueWithNameInData("regEx");

		TextVariable textVariable = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(id, nameInData,
						textId, defTextId, regularExpression);
		possiblyConvertRefParentId(textVariable);
		possiblyConvertAttributeReferences(textVariable);
		convertFinalValue(textVariable);

		return textVariable;
	}

	private String extractLinkedRecordIdByNameInData(String nameInData) {
		DataRecordLink textLink = dataRecordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				nameInData);
		return textLink.getLinkedRecordId();
	}

	private void possiblyConvertRefParentId(TextVariable textVariable) {
		if (dataRecordGroup.containsChildWithNameInData("refParentId")) {
			convertRefParentId(textVariable);
		}
	}

	private void convertRefParentId(TextVariable textVariable) {
		String refParentId = extractLinkedRecordIdByNameInData("refParentId");
		textVariable.setRefParentId(refParentId);
	}

	private void convertFinalValue(TextVariable textVariable) {
		if (dataRecordGroup.containsChildWithNameInData("finalValue")) {
			String finalValue = dataRecordGroup.getFirstAtomicValueWithNameInData("finalValue");
			textVariable.setFinalValue(finalValue);
		}
	}

	private void possiblyConvertAttributeReferences(TextVariable textVariable) {
		if (dataRecordGroup.containsChildWithNameInData("attributeReferences")) {
			convertAndAddAttributeReferences(textVariable);
		}
	}

	private void convertAndAddAttributeReferences(TextVariable textVariable) {
		DataGroup attributeReferences = dataRecordGroup
				.getFirstGroupWithNameInData("attributeReferences");

		List<DataRecordLink> attributeRefs = attributeReferences
				.getChildrenOfTypeAndName(DataRecordLink.class, "ref");
		attributeRefs.forEach(ref -> {
			String refValue = ref.getLinkedRecordId();
			textVariable.addAttributeReference(refValue);
		});
	}
}
