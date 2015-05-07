package epc.metadataformat.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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

	@Test
	public void testContainsChildWithId() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		DataElement child = DataAtomic.withDataIdAndValue("childId", "child value");
		dataGroup.addChild(child);
		assertTrue(dataGroup.containsChildWithDataId("childId"));
	}

	@Test
	public void testContainsChildWithIdNotFound() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		DataElement child = DataAtomic.withDataIdAndValue("childId", "child value");
		dataGroup.addChild(child);
		assertFalse(dataGroup.containsChildWithDataId("childId_NOT_FOUND"));
	}

	@Test
	public void testGetFirstAtomicValueWithDataId() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		DataElement child = DataAtomic.withDataIdAndValue("childId", "child value");
		dataGroup.addChild(child);
		String value = dataGroup.getFirstAtomicValueWithDataId("childId");
		assertEquals(value, "child value");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstAtomicValueWithIdNotFound() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		DataElement child = DataAtomic.withDataIdAndValue("childId", "child value");
		dataGroup.addChild(child);
		dataGroup.getFirstAtomicValueWithDataId("childId_NOTFOUND");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstAtomicValueWithIdNotFoundGroup() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		DataElement child = DataGroup.withDataId("groupId2");
		dataGroup.addChild(child);
		dataGroup.getFirstAtomicValueWithDataId("groupId2");
	}

	@Test
	public void testGetFirstGroupWithDataId() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		DataElement child = DataGroup.withDataId("groupId2");
		dataGroup.addChild(child);
		DataGroup group = dataGroup.getFirstGroupWithDataId("groupId2");
		assertEquals(group, child);
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstGroupWithIdNotFound() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		DataElement child = DataGroup.withDataId("groupId2");
		dataGroup.addChild(child);
		dataGroup.getFirstGroupWithDataId("groupId2_NOTFOUND");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstGroupWithIdNotFoundGroup() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		DataElement child = DataAtomic.withDataIdAndValue("childId", "child value");
		dataGroup.addChild(child);
		dataGroup.getFirstGroupWithDataId("childId");
	}

}
