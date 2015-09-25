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
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");

		ItemCollection itemCollection = new ItemCollection(id, nameInData, textId, defTextId);

		DataGroup collectionItemReferences = dataGroup
				.getFirstGroupWithNameInData("collectionItemReferences");
		for (DataElement dataElement : collectionItemReferences.getChildren()) {
			itemCollection.addItemReference(((DataAtomic) dataElement).getValue());
		}

		return itemCollection;
	}

}
