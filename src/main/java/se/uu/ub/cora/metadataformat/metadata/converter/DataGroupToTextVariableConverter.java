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

package se.uu.ub.cora.metadataformat.metadata.converter;

import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.TextVariable;

public final class DataGroupToTextVariableConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	public static DataGroupToTextVariableConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToTextVariableConverter(dataGroup);
	}

	private DataGroupToTextVariableConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public TextVariable toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");

		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");
		String regularExpression = dataGroup.getFirstAtomicValueWithNameInData("regEx");

		return TextVariable.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression(id, nameInData,
				textId, defTextId, regularExpression);
	}

}
