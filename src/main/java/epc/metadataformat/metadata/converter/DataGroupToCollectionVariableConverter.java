package epc.metadataformat.metadata.converter;

import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.CollectionVariable;

public final class DataGroupToCollectionVariableConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	public static DataGroupToCollectionVariableConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToCollectionVariableConverter(dataGroup);
	}

	private DataGroupToCollectionVariableConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public CollectionVariable toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithDataId("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithDataId("id");
		String dataId = dataGroup.getFirstAtomicValueWithDataId("dataId");
		String textId = dataGroup.getFirstAtomicValueWithDataId("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithDataId("defTextId");
		String refCollectionId = dataGroup.getFirstAtomicValueWithDataId("refCollectionId");

		return new CollectionVariable(id, dataId, textId, defTextId, refCollectionId);
	}

}
