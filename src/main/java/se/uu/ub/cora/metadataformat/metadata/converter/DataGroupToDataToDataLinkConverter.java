package se.uu.ub.cora.metadataformat.metadata.converter;

import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;

public final class DataGroupToDataToDataLinkConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	public static DataGroupToDataToDataLinkConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToDataToDataLinkConverter(dataGroup);
	}

	private DataGroupToDataToDataLinkConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public DataToDataLink toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");

		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");
		String targetRecordType = dataGroup.getFirstAtomicValueWithNameInData("targetRecordType");

		return DataToDataLink.withIdAndNameInDataAndTextIdAndDefTextIdAndTargetRecordType(id,
				nameInData, textId, defTextId, targetRecordType);
	}

}
