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
		switch (type) {
			case "group":
				return DataGroupToMetadataGroupConverter.fromDataGroup(dataGroup);
			case "groupChild":
				return DataGroupToMetadataGroupChildConverter.fromDataGroup(dataGroup);
			case "collectionItem":
				return DataGroupToCollectionItemConverter.fromDataGroup(dataGroup);
			case "collectionVariable":
				return DataGroupToCollectionVariableConverter.fromDataGroup(dataGroup);
			case "collectionVariableChild":
				return DataGroupToCollectionVariableChildConverter.fromDataGroup(dataGroup);
			case "itemCollection":
				return DataGroupToItemCollectionConverter.fromDataGroup(dataGroup);
			case "textVariable":
				return DataGroupToTextVariableConverter.fromDataGroup(dataGroup);
		}

		throw DataConversionException.withMessage("No converter found for DataGroup with type:"
				+ type);
	}
}
