package se.uu.ub.cora.metadataformat.metadata.converter;

import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.metadata.MetadataChildReference;

public final class DataGroupToMetadataChildReferenceConverter {

	private DataGroup dataGroup;
	private MetadataChildReference childReference;

	public static DataGroupToMetadataChildReferenceConverter fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToMetadataChildReferenceConverter(dataGroup);
	}

	private DataGroupToMetadataChildReferenceConverter(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	public MetadataChildReference toMetadata() {
		createMetadataChildReferenceWithBasicInfo();
		if (dataGroup.containsChildWithNameInData("repeatMinKey")) {
			childReference.setRepeatMinKey(dataGroup.getFirstAtomicValueWithNameInData("repeatMinKey"));
		}
		if (dataGroup.containsChildWithNameInData("secret")) {
			childReference.setSecret(getFirstAtomicValueWithNameInDataAsBoolean("secret"));
		}
		if (dataGroup.containsChildWithNameInData("secretKey")) {
			childReference.setSecretKey(dataGroup.getFirstAtomicValueWithNameInData("secretKey"));
		}
		if (dataGroup.containsChildWithNameInData("readOnly")) {
			childReference.setReadOnly(getFirstAtomicValueWithNameInDataAsBoolean("readOnly"));
		}
		if (dataGroup.containsChildWithNameInData("readOnlyKey")) {
			childReference.setReadOnlyKey(dataGroup.getFirstAtomicValueWithNameInData("readOnlyKey"));
		}
		return childReference;
	}

	private void createMetadataChildReferenceWithBasicInfo() {
		String reference = dataGroup.getFirstAtomicValueWithNameInData("ref");
		int repeatMin = Integer.parseInt(dataGroup.getFirstAtomicValueWithNameInData("repeatMin"));
		int repeatMax = getRepeatMax();
		childReference = MetadataChildReference.withReferenceIdAndRepeatMinAndRepeatMax(reference,
				repeatMin, repeatMax);
	}

	private int getRepeatMax() {
		String repeatMaxString = dataGroup.getFirstAtomicValueWithNameInData("repeatMax");
		if ("X".equalsIgnoreCase(repeatMaxString)) {
			return Integer.MAX_VALUE;
		}
		return Integer.valueOf(repeatMaxString);
	}

	private boolean getFirstAtomicValueWithNameInDataAsBoolean(String nameInData) {
		String value = dataGroup.getFirstAtomicValueWithNameInData(nameInData);
		if ("true".equals(value)) {
			return true;
		} else if ("false".equals(value)) {
			return false;
		}
		throw DataConversionException.withMessage("Can not convert value:" + value
				+ " to a boolean value");
	}

}
