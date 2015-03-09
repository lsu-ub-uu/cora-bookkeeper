package epc.metadataformat.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class DataGroupTest {
	@Test
	public void testInit() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");

		assertEquals(dataGroup.getDataId(), "dataId",
				"DataId should be the same as the one set in the constructor.");
	}

	@Test
	public void testAddAttribute() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		dataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(dataGroup.getAttribute("attributeId"), "attributeValue",
				"Attribute should be the same as the one added to the group");
	}

	@Test
	public void testGetAttributes() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		dataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(dataGroup.getAttributes().get("attributeId"), "attributeValue",
				"Attribute should be the same as the one added to the group");

	}

	@Test
	public void testAddChild() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		DataElement child = DataAtomic.withDataIdAndValue("childId", "child value");
		dataGroup.addChild(child);
		assertEquals(dataGroup.getChildren().iterator().next(), child,
				"Child should be the same as the one added to the group");
	}
}
