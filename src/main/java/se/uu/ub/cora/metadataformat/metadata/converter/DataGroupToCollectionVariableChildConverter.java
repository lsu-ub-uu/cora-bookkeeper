package se.uu.ub.cora.metadataformat.metadata.converter;

import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.CollectionVariableChild;

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
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");
		String refCollectionId = dataGroup.getFirstAtomicValueWithNameInData("refCollectionId");
		String refParentId = dataGroup.getFirstAtomicValueWithNameInData("refParentId");
		String finalValue = dataGroup.getFirstAtomicValueWithNameInData("finalValue");

		CollectionVariableChild collectionVariableChild = new CollectionVariableChild(id, nameInData,
				textId, defTextId, refCollectionId, refParentId);
		collectionVariableChild.setFinalValue(finalValue);
		return collectionVariableChild;
	}
}
