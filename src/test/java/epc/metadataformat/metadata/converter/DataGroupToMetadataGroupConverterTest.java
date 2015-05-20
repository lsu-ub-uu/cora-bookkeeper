package epc.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;

import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.MetadataChildReference;
import epc.metadataformat.metadata.MetadataGroup;

public class DataGroupToMetadataGroupConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withDataId("metadata");
		dataGroup.addAttributeByIdWithValue("type", "group");

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue("dataId", "other"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("defTextId", "otherDefTextId"));

		DataGroup attributeReference = DataGroup.withDataId("attributeReferences");
		attributeReference.addChild(DataAtomic.withDataIdAndValue("ref", "attribute1"));
		attributeReference.addChild(DataAtomic.withDataIdAndValue("ref", "attribute2"));
		attributeReference.addChild(DataAtomic.withDataIdAndValue("ref", "attribute3"));
		dataGroup.addChild(attributeReference);

		DataGroup childReferences = DataGroup.withDataId("childReferences");
		dataGroup.addChild(childReferences);

		DataGroup childReference = DataGroup.withDataId("childReference");
		childReference.addChild(DataAtomic.withDataIdAndValue("ref", "otherMetadata"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMin", "0"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMinKey", "SOME_KEY"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMax", "16"));
		childReference.addChild(DataAtomic.withDataIdAndValue("secret", "true"));
		childReference.addChild(DataAtomic.withDataIdAndValue("secretKey", "SECRET_KEY"));
		childReference.addChild(DataAtomic.withDataIdAndValue("readOnly", "true"));
		childReference.addChild(DataAtomic.withDataIdAndValue("readOnlyKey", "READONLY_KEY"));
		childReferences.addChild(childReference);

		DataGroupToMetadataGroupConverter converter = DataGroupToMetadataGroupConverter
				.fromDataGroup(dataGroup);

		MetadataGroup metadataGroup = converter.toMetadata();
		assertEquals(metadataGroup.getId(), "otherId");
		assertEquals(metadataGroup.getDataId(), "other");
		assertEquals(metadataGroup.getTextId(), "otherTextId");
		assertEquals(metadataGroup.getDefTextId(), "otherDefTextId");

		Iterator<String> attributeReferenceIterator = metadataGroup.getAttributeReferences()
				.iterator();
		assertEquals(attributeReferenceIterator.next(), "attribute1");
		assertEquals(attributeReferenceIterator.next(), "attribute2");
		assertEquals(attributeReferenceIterator.next(), "attribute3");

		Iterator<MetadataChildReference> childReferencesIterator = metadataGroup
				.getChildReferences().iterator();
		MetadataChildReference metadataChildReference = childReferencesIterator.next();
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
	public void testToMetadataNoAttributeRefrences() {
		DataGroup dataGroup = DataGroup.withDataId("metadata");
		dataGroup.addAttributeByIdWithValue("type", "group");

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue("dataId", "other"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("defTextId", "otherDefTextId"));

		DataGroup childReferences = DataGroup.withDataId("childReferences");
		dataGroup.addChild(childReferences);

		DataGroup childReference = DataGroup.withDataId("childReference");
		childReference.addChild(DataAtomic.withDataIdAndValue("ref", "otherMetadata"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMin", "0"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMinKey", "SOME_KEY"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMax", "16"));
		childReference.addChild(DataAtomic.withDataIdAndValue("secret", "true"));
		childReference.addChild(DataAtomic.withDataIdAndValue("secretKey", "SECRET_KEY"));
		childReference.addChild(DataAtomic.withDataIdAndValue("readOnly", "true"));
		childReference.addChild(DataAtomic.withDataIdAndValue("readOnlyKey", "READONLY_KEY"));
		childReferences.addChild(childReference);

		DataGroupToMetadataGroupConverter converter = DataGroupToMetadataGroupConverter
				.fromDataGroup(dataGroup);

		MetadataGroup metadataGroup = converter.toMetadata();
		assertEquals(metadataGroup.getId(), "otherId");
		assertEquals(metadataGroup.getDataId(), "other");
		assertEquals(metadataGroup.getTextId(), "otherTextId");
		assertEquals(metadataGroup.getDefTextId(), "otherDefTextId");

		Iterator<MetadataChildReference> childReferencesIterator = metadataGroup
				.getChildReferences().iterator();
		MetadataChildReference metadataChildReference = childReferencesIterator.next();
		assertEquals(metadataChildReference.getReferenceId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMinKey(), "SOME_KEY");
		assertEquals(metadataChildReference.getRepeatMax(), 16);
		assertEquals(metadataChildReference.isSecret(), true);
		assertEquals(metadataChildReference.getSecretKey(), "SECRET_KEY");
		assertEquals(metadataChildReference.isReadOnly(), true);
		assertEquals(metadataChildReference.getReadOnlyKey(), "READONLY_KEY");

	}
}
