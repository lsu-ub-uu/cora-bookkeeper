package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;

public class DataGroupToMetadataChildReferenceConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withNameInData("childReference");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMinKey", "SOME_KEY"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "16"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("secret", "true"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("secretKey", "SECRET_KEY"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("readOnly", "true"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("readOnlyKey", "READONLY_KEY"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getReferenceId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMinKey(), "SOME_KEY");
		assertEquals(metadataChildReference.getRepeatMax(), 16);
		assertEquals(metadataChildReference.isSecret(), true);
		assertEquals(metadataChildReference.getSecretKey(), "SECRET_KEY");
		assertEquals(metadataChildReference.isReadOnly(), true);
		assertEquals(metadataChildReference.getReadOnlyKey(), "READONLY_KEY");
	}

	@Test
	public void testToMetadataFalse() {
		DataGroup dataGroup = DataGroup.withNameInData("childReference");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "16"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("secret", "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("readOnly", "false"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getReferenceId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMinKey(), "");
		assertEquals(metadataChildReference.getRepeatMax(), 16);
		assertEquals(metadataChildReference.isSecret(), false);
		assertEquals(metadataChildReference.getSecretKey(), "");
		assertEquals(metadataChildReference.isReadOnly(), false);
		assertEquals(metadataChildReference.getReadOnlyKey(), "");
	}

	@Test
	public void testToMetadataNoNonMandatoryInfo() {
		DataGroup dataGroup = DataGroup.withNameInData("childReference");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "16"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getReferenceId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMinKey(), "");
		assertEquals(metadataChildReference.getRepeatMax(), 16);
		assertEquals(metadataChildReference.isSecret(), false);
		assertEquals(metadataChildReference.getSecretKey(), "");
		assertEquals(metadataChildReference.isReadOnly(), false);
		assertEquals(metadataChildReference.getReadOnlyKey(), "");
	}

	@Test(expectedExceptions = DataConversionException.class)
	public void testToMetadataNotBooleanValue() {
		DataGroup dataGroup = DataGroup.withNameInData("childReference");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "16"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("secret", "NOT_BOOLEAN_VALUE"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("readOnly", "NOT_BOOLEAN_VALUE"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		converter.toMetadata();
	}

	@Test(expectedExceptions = DataConversionException.class)
	public void testToMetadataNotBooleanValueReadOnly() {
		DataGroup dataGroup = DataGroup.withNameInData("childReference");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "16"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("readOnly", "NOT_BOOLEAN_VALUE"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		converter.toMetadata();

	}

	@Test
	public void testToMetadataRepeatMaxValueX() {
		DataGroup dataGroup = DataGroup.withNameInData("childReference");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("repeatMax", "X"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getReferenceId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMax(), Integer.MAX_VALUE);
	}
}
