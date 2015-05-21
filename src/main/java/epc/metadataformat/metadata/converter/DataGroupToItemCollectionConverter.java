package epc.metadataformat.metadata.converter;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataElement;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.ItemCollection;

public final class DataGroupToItemCollectionConverter implements DataGroupToMetadataConverter {

	public static DataGroupToItemCollectionConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToItemCollectionConverter(dataGroup);
	}

	private DataGroup dataGroup;

	private DataGroupToItemCollectionConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public ItemCollection toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithDataId("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithDataId("id");
		String dataId = dataGroup.getFirstAtomicValueWithDataId("dataId");
		String textId = dataGroup.getFirstAtomicValueWithDataId("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithDataId("defTextId");

		ItemCollection itemCollection = new ItemCollection(id, dataId, textId, defTextId);

		DataGroup collectionItemReferences = dataGroup
				.getFirstGroupWithDataId("collectionItemReferences");
		for (DataElement dataElement : collectionItemReferences.getChildren()) {
			itemCollection.addItemReference(((DataAtomic) dataElement).getValue());
		}

		return itemCollection;
	}

}
