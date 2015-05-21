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
		DataGroup recordInfo = dataGroup.getFirstGroupWithDataId("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithDataId("id");
		String dataId = dataGroup.getFirstAtomicValueWithDataId("dataId");
		String textId = dataGroup.getFirstAtomicValueWithDataId("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithDataId("defTextId");
		String parentId = dataGroup.getFirstAtomicValueWithDataId("parentId");

		MetadataGroupChild metadataGroupChild = new MetadataGroupChild(id, dataId, textId,
				defTextId, parentId);

		DataGroup attributeReferences = dataGroup.getFirstGroupWithDataId("attributeReferences");
		for (DataElement dataElement : attributeReferences.getChildren()) {
			metadataGroupChild.addAttributeReference(((DataAtomic) dataElement).getValue());
		}

		// TODO: add childReferences using childReference converter
		DataGroup childReferences = dataGroup.getFirstGroupWithDataId("childReferences");
		for (DataElement dataElement : childReferences.getChildren()) {
			DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
					.fromDataGroup((DataGroup) dataElement);
			metadataGroupChild.addChildReference(converter.toMetadata());
		}

		return metadataGroupChild;
	}

}
