package epc.metadataformat.metadata.converter;

import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.MetadataChildReference;

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
		if (dataGroup.containsChildWithDataId("repeatMinKey")) {
			childReference.setRepeatMinKey(dataGroup.getFirstAtomicValueWithDataId("repeatMinKey"));
		}
		if (dataGroup.containsChildWithDataId("secret")) {
			childReference.setSecret(getFirstAtomicValueWithDataIdAsBoolean("secret"));
		}
		if (dataGroup.containsChildWithDataId("secretKey")) {
			childReference.setSecretKey(dataGroup.getFirstAtomicValueWithDataId("secretKey"));
		}
		if (dataGroup.containsChildWithDataId("readOnly")) {
			childReference.setReadOnly(getFirstAtomicValueWithDataIdAsBoolean("readOnly"));
		}
		if (dataGroup.containsChildWithDataId("readOnlyKey")) {
			childReference.setReadOnlyKey(dataGroup.getFirstAtomicValueWithDataId("readOnlyKey"));
		}
		return childReference;
	}

	private void createMetadataChildReferenceWithBasicInfo() {
		String reference = dataGroup.getFirstAtomicValueWithDataId("ref");
		int repeatMin = Integer.parseInt(dataGroup.getFirstAtomicValueWithDataId("repeatMin"));
		int repeatMax = getRepeatMax();
		childReference = MetadataChildReference.withReferenceIdAndRepeatMinAndRepeatMax(reference,
				repeatMin, repeatMax);
	}

	private int getRepeatMax() {
		String repeatMaxString = dataGroup.getFirstAtomicValueWithDataId("repeatMax");
		if ("X".equalsIgnoreCase(repeatMaxString)) {
			return Integer.MAX_VALUE;
		}
		return Integer.valueOf(repeatMaxString);
	}

	private boolean getFirstAtomicValueWithDataIdAsBoolean(String dataId) {
		String value = dataGroup.getFirstAtomicValueWithDataId(dataId);
		if ("true".equals(value)) {
			return true;
		} else if ("false".equals(value)) {
			return false;
		}
		throw DataConversionException.withMessage("Can not convert value:" + value
				+ " to a boolean value");
	}

}
