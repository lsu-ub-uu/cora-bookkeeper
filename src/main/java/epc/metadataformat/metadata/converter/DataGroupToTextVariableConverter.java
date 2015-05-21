package epc.metadataformat.metadata.converter;

import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.TextVariable;

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
		DataGroup recordInfo = dataGroup.getFirstGroupWithDataId("recordInfo");

		String id = recordInfo.getFirstAtomicValueWithDataId("id");
		String dataId = dataGroup.getFirstAtomicValueWithDataId("dataId");
		String textId = dataGroup.getFirstAtomicValueWithDataId("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithDataId("defTextId");
		String regularExpression = dataGroup.getFirstAtomicValueWithDataId("regEx");

		return TextVariable.withIdAndDataIdAndTextIdAndDefTextIdAndRegularExpression(id, dataId,
				textId, defTextId, regularExpression);
	}

}
