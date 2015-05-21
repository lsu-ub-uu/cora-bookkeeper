package epc.metadataformat.metadata.converter;

import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.CollectionItem;

public final class DataGroupToCollectionItemConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	public static DataGroupToCollectionItemConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToCollectionItemConverter(dataGroup);
	}

	private DataGroupToCollectionItemConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public CollectionItem toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithDataId("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithDataId("id");
		String dataId = dataGroup.getFirstAtomicValueWithDataId("dataId");
		String textId = dataGroup.getFirstAtomicValueWithDataId("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithDataId("defTextId");

		return new CollectionItem(id, dataId, textId, defTextId);
	}

}
