package epc.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.MetadataChildReference;

public class DataGroupToMetadataChildReferenceConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withDataId("childReference");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMinKey", "SOME_KEY"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMax", "16"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("secret", "true"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("secretKey", "SECRET_KEY"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("readOnly", "true"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("readOnlyKey", "READONLY_KEY"));

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
		DataGroup dataGroup = DataGroup.withDataId("childReference");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMax", "16"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("secret", "false"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("readOnly", "false"));

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
		DataGroup dataGroup = DataGroup.withDataId("childReference");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMax", "16"));

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
		DataGroup dataGroup = DataGroup.withDataId("childReference");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMax", "16"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("secret", "NOT_BOOLEAN_VALUE"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("readOnly", "NOT_BOOLEAN_VALUE"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		converter.toMetadata();
	}

	@Test(expectedExceptions = DataConversionException.class)
	public void testToMetadataNotBooleanValueReadOnly() {
		DataGroup dataGroup = DataGroup.withDataId("childReference");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMax", "16"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("readOnly", "NOT_BOOLEAN_VALUE"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		converter.toMetadata();

	}

	@Test
	public void testToMetadataRepeatMaxValueX() {
		DataGroup dataGroup = DataGroup.withDataId("childReference");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("ref", "otherMetadata"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMin", "0"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("repeatMax", "X"));

		DataGroupToMetadataChildReferenceConverter converter = DataGroupToMetadataChildReferenceConverter
				.fromDataGroup(dataGroup);
		MetadataChildReference metadataChildReference = converter.toMetadata();

		assertEquals(metadataChildReference.getReferenceId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMax(), Integer.MAX_VALUE);
	}
}
