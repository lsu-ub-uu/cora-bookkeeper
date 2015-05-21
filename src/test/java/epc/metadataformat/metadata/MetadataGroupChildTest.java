package epc.metadataformat.metadata;

import static org.testng.Assert.assertEquals;

import java.util.Collection;

import org.testng.annotations.Test;

public class MetadataGroupChildTest {
	@Test
	public void testInit() {
		MetadataGroupChild metadataGroupChild = new MetadataGroupChild("id", "dataId", "textId",
				"defTextId", "refParentId");
		metadataGroupChild.addAttributeReference("namePartType");

		assertEquals(metadataGroupChild.getId(), "id",
				"Id should have the value set in the constructor");

		assertEquals(metadataGroupChild.getDataId(), "dataId",
				"DataId should have the value set in the constructor");

		assertEquals(metadataGroupChild.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		assertEquals(metadataGroupChild.getDefTextId(), "defTextId",
				"DefTextId should have the value set in the constructor");

		assertEquals(metadataGroupChild.getRefParentId(), "refParentId",
				"ParentId should be the same as the one set in the constructor");

		Collection<String> attributeReferences = metadataGroupChild.getAttributeReferences();
		assertEquals(attributeReferences.iterator().next(), "namePartType");
	}
}