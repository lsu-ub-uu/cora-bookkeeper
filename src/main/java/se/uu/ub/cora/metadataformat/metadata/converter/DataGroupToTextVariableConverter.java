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
