package se.uu.ub.cora.metadataformat.metadata.converter;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataGroup;

public class DataGroupToMetadataConverterFactoryTest {
	@Test(expectedExceptions = DataConversionException.class)
	public void testFactorNotMetadata() {
		DataGroup dataGroup = DataGroup.withNameInData("metadataNOT");
		dataGroup.addAttributeByIdWithValue("type", "group");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		converterFactory.factor();
	}

	@Test(expectedExceptions = DataConversionException.class)
	public void testFactorWrongType() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "groupNOT");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		converterFactory.factor();
	}

	@Test
	public void testFactorGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "group");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		DataGroupToMetadataConverter converter = converterFactory.factor();
		assertTrue(converter instanceof DataGroupToMetadataGroupConverter);
	}

	@Test
	public void testFactorGroupChild() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "groupChild");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		DataGroupToMetadataConverter converter = converterFactory.factor();
		assertTrue(converter instanceof DataGroupToMetadataGroupChildConverter);
	}

	@Test
	public void testFactorCollectionItem() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionItem");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		DataGroupToMetadataConverter converter = converterFactory.factor();
		assertTrue(converter instanceof DataGroupToCollectionItemConverter);
	}

	@Test
	public void testFactorCollectionVariable() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionVariable");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		DataGroupToMetadataConverter converter = converterFactory.factor();
		assertTrue(converter instanceof DataGroupToCollectionVariableConverter);
	}

	@Test
	public void testFactorCollectionVariableChild() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "collectionVariableChild");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		DataGroupToMetadataConverter converter = converterFactory.factor();
		assertTrue(converter instanceof DataGroupToCollectionVariableChildConverter);
	}

	@Test
	public void testFactorItemCollection() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "itemCollection");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		DataGroupToMetadataConverter converter = converterFactory.factor();
		assertTrue(converter instanceof DataGroupToItemCollectionConverter);
	}

	@Test
	public void testFactorTextVariable() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "textVariable");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		DataGroupToMetadataConverter converter = converterFactory.factor();
		assertTrue(converter instanceof DataGroupToTextVariableConverter);
	}

	@Test
	public void testFactorRecordLink() {
		DataGroup dataGroup = DataGroup.withNameInData("metadata");
		dataGroup.addAttributeByIdWithValue("type", "recordLink");
		DataGroupToMetadataConverterFactory converterFactory = DataGroupToMetadataConverterFactoryImp
				.fromDataGroup(dataGroup);
		DataGroupToMetadataConverter converter = converterFactory.factor();
		assertTrue(converter instanceof DataGroupToRecordLinkConverter);
	}

}
