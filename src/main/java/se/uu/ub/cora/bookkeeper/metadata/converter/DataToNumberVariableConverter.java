/*
 * Copyright 2018, 2019 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.metadata.LimitsContainer;
import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;
import se.uu.ub.cora.bookkeeper.metadata.StandardMetadataParameters;
import se.uu.ub.cora.bookkeeper.metadata.TextContainer;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class DataToNumberVariableConverter implements DataToMetadataConverter {

	private DataRecordGroup dataRecordGroup;

	private DataToNumberVariableConverter(DataRecordGroup dataRecordGroup) {
		this.dataRecordGroup = dataRecordGroup;
	}

	public static DataToNumberVariableConverter fromDataRecordGroup(
			DataRecordGroup dataRecordGroup) {
		return new DataToNumberVariableConverter(dataRecordGroup);
	}

	@Override
	public NumberVariable toMetadata() {
		String id = dataRecordGroup.getId();
		String nameInData = dataRecordGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = extractLinkedRecordIdByNameInData("textId");
		String defTextId = extractLinkedRecordIdByNameInData("defTextId");
		StandardMetadataParameters standardParams = StandardMetadataParameters
				.usingIdNameInDataAndTextContainer(id, nameInData,
						TextContainer.usingTextIdAndDefTextId(textId, defTextId));
		LimitsContainer minMax = createMinAndMaxLimits();
		LimitsContainer warnMinMax = createWarningMinAndMaxLimits();
		int numOfDecimals = extractNumberOfDecimalsAsInt();
		NumberVariable numberVariable = NumberVariable
				.usingStandardParamsLimitsWarnLimitsAndNumOfDecimals(standardParams, minMax,
						warnMinMax, numOfDecimals);
		possiblyConvertAttributeReferences(numberVariable);
		return numberVariable;
	}

	private String extractLinkedRecordIdByNameInData(String nameInData) {
		DataRecordLink textLink = dataRecordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				nameInData);
		return textLink.getLinkedRecordId();
	}

	private LimitsContainer createMinAndMaxLimits() {
		double min = extractLimitAsDoubleUsingNameInData("min");
		double max = extractLimitAsDoubleUsingNameInData("max");
		return LimitsContainer.usingMinAndMax(min, max);
	}

	private double extractLimitAsDoubleUsingNameInData(String limitNameInData) {
		String minString = dataRecordGroup.getFirstAtomicValueWithNameInData(limitNameInData);
		return Double.parseDouble(minString);
	}

	private LimitsContainer createWarningMinAndMaxLimits() {
		double warnMin = extractLimitAsDoubleUsingNameInData("warningMin");
		double warnMax = extractLimitAsDoubleUsingNameInData("warningMax");
		return LimitsContainer.usingMinAndMax(warnMin, warnMax);
	}

	private int extractNumberOfDecimalsAsInt() {
		String numOfDecimalsString = dataRecordGroup
				.getFirstAtomicValueWithNameInData("numberOfDecimals");
		return Integer.parseInt(numOfDecimalsString);
	}

	private void possiblyConvertAttributeReferences(NumberVariable numberVariable) {
		if (dataRecordGroup.containsChildWithNameInData("attributeReferences")) {
			convertAndAddAttributeReferences(numberVariable);
		}
	}

	private void convertAndAddAttributeReferences(NumberVariable numberVariable) {
		DataGroup attributeReferences = dataRecordGroup
				.getFirstGroupWithNameInData("attributeReferences");

		List<DataRecordLink> attributeRefs = attributeReferences
				.getChildrenOfTypeAndName(DataRecordLink.class, "ref");
		attributeRefs.forEach(ref -> {
			String refValue = ref.getLinkedRecordId();
			numberVariable.addAttributeReference(refValue);
		});
	}
}
