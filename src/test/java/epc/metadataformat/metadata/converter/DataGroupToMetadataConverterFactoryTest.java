package epc.metadataformat.metadata.converter;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import epc.metadataformat.data.DataGroup;

public class DataGroupToMetadataConverterFactoryTest {
	@Test(expectedExceptions = DataConversionException.class)
	public void testFactorNotMetadata() {
		DataGroup dataGroup = DataGroup.withDataId("metadataNOT");
		dataGroup.addAttributeByIdWithValue("type", "group");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		converterFactory.factor();
	}

	@Test(expectedExceptions = DataConversionException.class)
	public void testFactorUnknownType() {
		DataGroup dataGroup = DataGroup.withDataId("metadata");
		dataGroup.addAttributeByIdWithValue("type", "groupUNKNOWN");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		converterFactory.factor();
	}

	@Test
	public void testFactorGroup() {
		DataGroup dataGroup = DataGroup.withDataId("metadata");
		dataGroup.addAttributeByIdWithValue("type", "group");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		DataGroupToMetadataConverter converter = converterFactory.factor();
		assertTrue(converter instanceof DataGroupToMetadataGroupConverter);
	}

	@Test
	public void testFactorTextVariable() {
		DataGroup dataGroup = DataGroup.withDataId("metadata");
		dataGroup.addAttributeByIdWithValue("type", "textVariable");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		DataGroupToMetadataConverter converter = converterFactory.factor();
		assertTrue(converter instanceof DataGroupToTextVariableConverter);
	}
}
