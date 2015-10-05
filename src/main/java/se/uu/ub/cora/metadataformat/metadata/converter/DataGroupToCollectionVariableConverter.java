package se.uu.ub.cora.metadataformat.metadata.converter;

import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.CollectionVariable;

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
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");
		String refCollectionId = dataGroup.getFirstAtomicValueWithNameInData("refCollectionId");

		return new CollectionVariable(id, nameInData, textId, defTextId, refCollectionId);
	}

}
