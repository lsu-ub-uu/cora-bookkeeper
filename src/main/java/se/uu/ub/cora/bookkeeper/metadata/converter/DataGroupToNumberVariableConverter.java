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
