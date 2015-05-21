package epc.metadataformat.metadata.converter;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;

import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.metadataformat.metadata.MetadataChildReference;
import epc.metadataformat.metadata.MetadataGroupChild;

public class DataGroupToMetadataGroupChildConverterTest {
	@Test
	public void testToMetadata() {
		DataGroup dataGroup = DataGroup.withDataId("metadata");
		dataGroup.addAttributeByIdWithValue("type", "groupChild");

		DataGroup recordInfo = DataGroup.withDataId("recordInfo");
		recordInfo.addChild(DataAtomic.withDataIdAndValue("id", "otherId"));
		dataGroup.addChild(recordInfo);

		dataGroup.addChild(DataAtomic.withDataIdAndValue("dataId", "other"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("textId", "otherTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("defTextId", "otherDefTextId"));
		dataGroup.addChild(DataAtomic.withDataIdAndValue("parentId", "parentMetadataId"));

		DataGroup attributeReferences = DataGroup.withDataId("attributeReferences");
		attributeReferences.addChild(DataAtomic.withDataIdAndValue("ref", "attribute1"));
		attributeReferences.addChild(DataAtomic.withDataIdAndValue("ref", "attribute2"));
		attributeReferences.addChild(DataAtomic.withDataIdAndValue("ref", "attribute3"));
		dataGroup.addChild(attributeReferences);

		// TODO: add childReferences
		DataGroup childReferences = DataGroup.withDataId("childReferences");
		dataGroup.addChild(childReferences);

		DataGroup childReference = DataGroup.withDataId("childReference");
		childReference.addChild(DataAtomic.withDataIdAndValue("ref", "otherMetadata"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMin", "0"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMinKey", "SOME_KEY"));
		childReference.addChild(DataAtomic.withDataIdAndValue("repeatMax", "15"));
		childReference.addChild(DataAtomic.withDataIdAndValue("secret", "true"));
		childReference.addChild(DataAtomic.withDataIdAndValue("secretKey", "SECRET_KEY"));
		childReference.addChild(DataAtomic.withDataIdAndValue("readOnly", "true"));
		childReference.addChild(DataAtomic.withDataIdAndValue("readOnlyKey", "READONLY_KEY"));
		childReferences.addChild(childReference);

		DataGroupToMetadataGroupChildConverter converter = DataGroupToMetadataGroupChildConverter
				.fromDataGroup(dataGroup);
		MetadataGroupChild metadataGroupChild = converter.toMetadata();

		assertEquals(metadataGroupChild.getId(), "otherId");
		assertEquals(metadataGroupChild.getDataId(), "other");
		assertEquals(metadataGroupChild.getTextId(), "otherTextId");
		assertEquals(metadataGroupChild.getDefTextId(), "otherDefTextId");

		Iterator<String> iterator = metadataGroupChild.getAttributeReferences().iterator();
		assertEquals(iterator.next(), "attribute1");
		assertEquals(iterator.next(), "attribute2");
		assertEquals(iterator.next(), "attribute3");

		Iterator<MetadataChildReference> iterator2 = metadataGroupChild.getChildReferences()
				.iterator();
		MetadataChildReference metadataChildReference = iterator2.next();
		assertEquals(metadataChildReference.getReferenceId(), "otherMetadata");
		assertEquals(metadataChildReference.getRepeatMin(), 0);
		assertEquals(metadataChildReference.getRepeatMinKey(), "SOME_KEY");
		assertEquals(metadataChildReference.getRepeatMax(), 15);
		assertEquals(metadataChildReference.isSecret(), true);
		assertEquals(metadataChildReference.getSecretKey(), "SECRET_KEY");
		assertEquals(metadataChildReference.isReadOnly(), true);
		assertEquals(metadataChildReference.getReadOnlyKey(), "READONLY_KEY");
	}
}
