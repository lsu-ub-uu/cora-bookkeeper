package epc.metadataformat.metadata.converter;

import epc.metadataformat.data.DataGroup;

public final class DataGroupToMetadataConverterFactoryImp implements
		DataGroupToMetadataConverterFactory {

	private DataGroup dataGroup;

	public static DataGroupToMetadataConverterFactoryImp fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToMetadataConverterFactoryImp(dataGroup);
	}

	private DataGroupToMetadataConverterFactoryImp(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public DataGroupToMetadataConverter factor() {
		try {
			return tryToFactor();
		} catch (Exception e) {
			throw DataConversionException.withMessageAndException(
					"Unable to factor DataGroupToMetadataConverter", e);
		}
	}

	private DataGroupToMetadataConverter tryToFactor() {
		ensureDataGroupHasDataIdMetadata();
		return createConverterBasedOnMetdataType();
	}

	private void ensureDataGroupHasDataIdMetadata() {
		if (dataGroupIsNotMetadata()) {
			throw DataConversionException.withMessage("DataGroup dataId:" + dataGroup.getDataId()
					+ " is not metadata");
		}
	}

	private boolean dataGroupIsNotMetadata() {
		return !"metadata".equals(dataGroup.getDataId());
	}

	private DataGroupToMetadataConverter createConverterBasedOnMetdataType() {
		String type = dataGroup.getAttribute("type");
		if ("group".equals(type)) {
			return DataGroupToMetadataGroupConverter.fromDataGroup(dataGroup);
		} else if ("textVariable".equals(type)) {
			return DataGroupToTextVariableConverter.fromDataGroup(dataGroup);
		}
		throw DataConversionException.withMessage("No converter found for DataGroup with dataId:"
				+ dataGroup.getDataId());
	}

}
