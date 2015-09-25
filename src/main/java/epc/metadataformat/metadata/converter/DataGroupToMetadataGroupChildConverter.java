package epc.metadataformat.metadata.converter;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataElement;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.MetadataGroupChild;

public final class DataGroupToMetadataGroupChildConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;

	public static DataGroupToMetadataGroupChildConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToMetadataGroupChildConverter(dataGroup);
	}

	private DataGroupToMetadataGroupChildConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public MetadataGroupChild toMetadata() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");
		String parentId = dataGroup.getFirstAtomicValueWithNameInData("parentId");

		MetadataGroupChild metadataGroupChild = new MetadataGroupChild(id, nameInData, textId,
				defTextId, parentId);

		DataGroup attributeReferences = dataGroup.getFirstGroupWithNameInData("attributeReferences");
		for (DataElement dataElement : attributeReferences.getChildren()) {
			metadataGroupChild.addAttributeReference(((DataAtomic) dataElement).getValue());
		}

		// TODO: add childReferences using childReference converter
		DataGroup childReferences = dataGroup.getFirstGroupWithNameInData("childReferences");
		for (DataElement dataElement : childReferences.getChildren()) {
			DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
					.fromDataGroup((DataGroup) dataElement);
			metadataGroupChild.addChildReference(converter.toMetadata());
		}

		return metadataGroupChild;
	}

}
