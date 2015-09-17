package epc.metadataformat.metadata.converter;

import epc.metadataformat.data.DataGroup;

public final class DataGroupToMetadataConverterFactoryImp implements
		DataGroupToMetadataConverterFactory {

	public static DataGroupToMetadataConverterFactoryImp fromDataGroup(DataGroup dataGroup) {
		return new DataGroupToMetadataConverterFactoryImp(dataGroup);
	}

	private DataGroup dataGroup;

	private DataGroupToMetadataConverterFactoryImp(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public DataGroupToMetadataConverter factor() {
		if ("metadata".equals(dataGroup.getDataId())) {
			return createConverterBasedOnMetadataType();
		}
		throw DataConversionException.withMessage("No converter found for DataGroup with dataId:"
				+ dataGroup.getDataId());
	}

	private DataGroupToMetadataConverter createConverterBasedOnMetadataType() {
		String type = dataGroup.getAttributes().get("type");
		if ("group".equals(type)) {
			return DataGroupToMetadataGroupConverter.fromDataGroup(dataGroup);
		}
		if ("groupChild".equals(type)) {
			return DataGroupToMetadataGroupChildConverter.fromDataGroup(dataGroup);
		}
		if ("collectionItem".equals(type)) {
			return DataGroupToCollectionItemConverter.fromDataGroup(dataGroup);
		}
		if ("collectionVariable".equals(type)) {
			return DataGroupToCollectionVariableConverter.fromDataGroup(dataGroup);
		}
		if ("collectionVariableChild".equals(type)) {
			return DataGroupToCollectionVariableChildConverter.fromDataGroup(dataGroup);
		}
		if ("itemCollection".equals(type)) {
			return DataGroupToItemCollectionConverter.fromDataGroup(dataGroup);
		}
		if ("textVariable".equals(type)) {
			return DataGroupToTextVariableConverter.fromDataGroup(dataGroup);
		}
		throw DataConversionException.withMessage("No converter found for DataGroup with type:"
				+ type);
	}
}
