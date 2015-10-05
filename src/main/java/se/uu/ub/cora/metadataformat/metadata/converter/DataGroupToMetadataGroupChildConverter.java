package se.uu.ub.cora.metadataformat.metadata.converter;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.MetadataGroupChild;

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
