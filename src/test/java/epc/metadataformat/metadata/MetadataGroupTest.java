package epc.metadataformat.metadata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

public class MetadataGroupTest {
	@Test
	public void testInit() {
		MetadataGroup metadataGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("id",
				"nameInData", "textId", "defTextId");

		assertEquals(metadataGroup.getId(), "id", "Id should have the value set in the constructor");

		assertEquals(metadataGroup.getNameInData(), "nameInData",
				"NameInData should have the value set in the constructor");

		assertEquals(metadataGroup.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		assertEquals(metadataGroup.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

		assertNotNull(metadataGroup.getAttributeReferences(),
				"attributeReferences should not be null for a new metadataGroup");

		assertNotNull(metadataGroup.getChildReferences(),
				"childReferences should not be null for a new metadataGroup");
	}

	@Test
	public void testAddAttributeReference() {
		MetadataGroup metadataGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("id",
				"nameInData", "textId", "defTextId");
		metadataGroup.addAttributeReference("attributeReference");
		assertEquals(metadataGroup.getAttributeReferences().iterator().next(),
				"attributeReference", "AttributeReference should be the same as the one added");
	}

	@Test
	public void testAddChildReference() {
		MetadataGroup metadataGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("id",
				"nameInData", "textId", "defTextId");
		MetadataChildReference metadataChildReference = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("aChildReference", 1,
						MetadataChildReference.UNLIMITED);
		metadataGroup.addChildReference(metadataChildReference);
		assertEquals(metadataGroup.getChildReferences().iterator().next(), metadataChildReference,
				"MetadataChildReference should be the same as the one added.");
	}
}
