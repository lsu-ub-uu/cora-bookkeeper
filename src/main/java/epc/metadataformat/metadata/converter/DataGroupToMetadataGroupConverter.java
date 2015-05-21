package epc.metadataformat.metadata.converter;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataElement;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.MetadataChildReference;
import epc.metadataformat.metadata.MetadataGroup;

public final class DataGroupToMetadataGroupConverter implements DataGroupToMetadataConverter {

	private DataGroup dataGroup;
	private MetadataGroup metadataGroup;

	public static DataGroupToMetadataGroupConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToMetadataGroupConverter(dataGroup);
	}

	private DataGroupToMetadataGroupConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public MetadataGroup toMetadata() {
		createMetadataGroupWithBasicInfo();
		convertAttributeReferences();
		convertChildReferences();
		return metadataGroup;
	}

	private void createMetadataGroupWithBasicInfo() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithDataId("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithDataId("id");
		String dataId = dataGroup.getFirstAtomicValueWithDataId("dataId");
		String textId = dataGroup.getFirstAtomicValueWithDataId("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithDataId("defTextId");
		metadataGroup = MetadataGroup.withIdAndDataIdAndTextIdAndDefTextId(id, dataId, textId,
				defTextId);
	}

	private void convertAttributeReferences() {
		if (dataGroup.containsChildWithDataId("attributeReferences")) {
			DataGroup attributeReferences = dataGroup
					.getFirstGroupWithDataId("attributeReferences");
			for (DataElement attributeReference : attributeReferences.getChildren()) {
				metadataGroup.addAttributeReference(((DataAtomic) attributeReference).getValue());
			}
		}
	}

	private void convertChildReferences() {
		DataGroup childReferences = dataGroup.getFirstGroupWithDataId("childReferences");
		for (DataElement childReferenceElement : childReferences.getChildren()) {
			convertChildReference((DataGroup) childReferenceElement);
		}
	}

	private void convertChildReference(DataGroup childReference) {
		DataGroupToMetadataChildReferenceConverter childConverter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(childReference);
		MetadataChildReference metadataChildReference = childConverter.toMetadata();
		metadataGroup.addChildReference(metadataChildReference);
	}

}
