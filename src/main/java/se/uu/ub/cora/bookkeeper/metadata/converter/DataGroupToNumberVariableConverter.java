/*
 * Copyright 2018 Uppsala University Library
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
import se.uu.ub.cora.bookkeeper.metadata.LimitsContainer;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.NumberVariable;
import se.uu.ub.cora.bookkeeper.metadata.StandardMetadataParameters;
import se.uu.ub.cora.bookkeeper.metadata.TextContainer;

public class DataGroupToNumberVariableConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	private DataGroupToNumberVariableConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public static DataGroupToNumberVariableConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToNumberVariableConverter(dataGroup);
	}

	@Override
	public MetadataElement toMetadata() {
		StandardMetadataParameters standardParams = extractStandardParameters();
		LimitsContainer minMax = createMinAndMaxLimits();
		LimitsContainer warnMinMax = createWarningMinAndMaxLimits();
		int numOfDecimals = extractNumberOfDecimalsAsInt();
		return NumberVariable.usingStandardParamsLimitsWarnLimitsAndNumOfDecimals(standardParams,
				minMax, warnMinMax, numOfDecimals);
	}

	private StandardMetadataParameters extractStandardParameters() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");

		TextContainer textContainer = createTextContainer();
		return StandardMetadataParameters.usingIdNameInDataAndTextContainer(id, nameInData,
				textContainer);
	}

	private TextContainer createTextContainer() {
		String textId = extractTextUsingNameInData("textId");
		String defTextId = extractTextUsingNameInData("defTextId");
		return TextContainer.usingTextIdAndDefTextId(textId, defTextId);
	}

	private String extractTextUsingNameInData(String textNameInData) {
		DataGroup textIdGroup = dataGroup.getFirstGroupWithNameInData(textNameInData);
		return textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private LimitsContainer createMinAndMaxLimits() {
		double min = extractLimitAsDoubleUsingNameInData("min");
		double max = extractLimitAsDoubleUsingNameInData("max");
		return LimitsContainer.usingMinAndMax(min, max);
	}

	private double extractLimitAsDoubleUsingNameInData(String limitNameInData) {
		String minString = dataGroup.getFirstAtomicValueWithNameInData(limitNameInData);
		return Double.parseDouble(minString);
	}

	private LimitsContainer createWarningMinAndMaxLimits() {
		double warnMin = extractLimitAsDoubleUsingNameInData("warningMin");
		double warnMax = extractLimitAsDoubleUsingNameInData("warningMax");
		return LimitsContainer.usingMinAndMax(warnMin, warnMax);
	}

	private int extractNumberOfDecimalsAsInt() {
		String numOfDecimalsString = dataGroup
				.getFirstAtomicValueWithNameInData("numberOfDecimals");
		return Integer.parseInt(numOfDecimalsString);
	}

}
