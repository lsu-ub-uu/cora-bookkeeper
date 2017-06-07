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
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;

public final class DataGroupToTextVariableConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	private DataGroupToTextVariableConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public static DataGroupToTextVariableConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToTextVariableConverter(dataGroup);
	}

	@Override
	public TextVariable toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");

		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");

		String textId = extractTextIdByNameInData("textId");
		String defTextId = extractTextIdByNameInData("defTextId");
		String regularExpression = dataGroup.getFirstAtomicValueWithNameInData("regEx");

		TextVariable textVariable = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(id, nameInData,
						textId, defTextId, regularExpression);
		possiblyConvertRefParentId(textVariable);
		convertFinalValue(textVariable);

		return textVariable;
	}

	private String extractTextIdByNameInData(String nameInData) {
		DataGroup text = dataGroup.getFirstGroupWithNameInData(nameInData);
		return text.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private void possiblyConvertRefParentId(TextVariable textVariable) {
		if (dataGroup.containsChildWithNameInData("refParentId")) {
			convertRefParentId(textVariable);
		}
	}

	private void convertRefParentId(TextVariable textVariable) {
		DataGroup refParentGroup = dataGroup.getFirstGroupWithNameInData("refParentId");
		String refParentId = refParentGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
		textVariable.setRefParentId(refParentId);
	}

	private void convertFinalValue(TextVariable textVariable) {
		if (dataGroup.containsChildWithNameInData("finalValue")) {
			String finalValue = dataGroup.getFirstAtomicValueWithNameInData("finalValue");
			textVariable.setFinalValue(finalValue);
		}
	}
}
