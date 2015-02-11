package epc.metadataformat;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class MetadataGroupTest {
	@Test
	public void testInit() {
		List<String> attributeReference = new ArrayList<>();
		List<MetadataChildReference> childReferences = new ArrayList<>();

		MetadataGroup metadataGroup = new MetadataGroup("id", "dataId",
				"textId", "deffTextId", attributeReference, childReferences);

		assertEquals(metadataGroup.getId(), "id",
				"Id should have the value set in the constructor");

		assertEquals(metadataGroup.getDataId(), "dataId",
				"DataId should have the value set in the constructor");

		assertEquals(metadataGroup.getTextId(), "textId",
				"TextId should have the value set in the constructor");

		assertEquals(metadataGroup.getDeffTextId(), "deffTextId",
				"DeffTextId should have the value set in the constructor");

		assertEquals(metadataGroup.getAttributeReference(), attributeReference,
				"AttributeReference should be the same list as the one set in the constructor");

		assertEquals(metadataGroup.getChildReferences(), childReferences,
				"ChildReferences should be the same as the once set in the constructor");
	}
}
