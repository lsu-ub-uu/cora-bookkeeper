package epc.metadataformat.metadata.converter;

import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.CollectionVariableChild;

public final class DataGroupToCollectionVariableChildConverter implements
		DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	public static DataGroupToCollectionVariableChildConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToCollectionVariableChildConverter(dataGroup);
	}

	private DataGroupToCollectionVariableChildConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public CollectionVariableChild toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithDataId("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithDataId("id");
		String dataId = dataGroup.getFirstAtomicValueWithDataId("dataId");
		String textId = dataGroup.getFirstAtomicValueWithDataId("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithDataId("defTextId");
		String refCollectionId = dataGroup.getFirstAtomicValueWithDataId("refCollectionId");
		String refParentId = dataGroup.getFirstAtomicValueWithDataId("refParentId");
		String finalValue = dataGroup.getFirstAtomicValueWithDataId("finalValue");

		CollectionVariableChild collectionVariableChild = new CollectionVariableChild(id, dataId,
				textId, defTextId, refCollectionId, refParentId);
		collectionVariableChild.setFinalValue(finalValue);
		return collectionVariableChild;
	}
}
