package se.uu.ub.cora.metadataformat.metadata.converter;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.MetadataChildReference;
import se.uu.ub.cora.metadataformat.metadata.MetadataGroup;

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
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		String nameInData = dataGroup.getFirstAtomicValueWithNameInData("nameInData");
		String textId = dataGroup.getFirstAtomicValueWithNameInData("textId");
		String defTextId = dataGroup.getFirstAtomicValueWithNameInData("defTextId");
		metadataGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(id, nameInData, textId,
				defTextId);
	}

	private void convertAttributeReferences() {
		if (dataGroup.containsChildWithNameInData("attributeReferences")) {
			DataGroup attributeReferences = dataGroup
					.getFirstGroupWithNameInData("attributeReferences");
			for (DataElement attributeReference : attributeReferences.getChildren()) {
				metadataGroup.addAttributeReference(((DataAtomic) attributeReference).getValue());
			}
		}
	}

	private void convertChildReferences() {
		DataGroup childReferences = dataGroup.getFirstGroupWithNameInData("childReferences");
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
