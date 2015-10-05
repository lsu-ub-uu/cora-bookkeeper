package se.uu.ub.cora.metadataformat.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataElement;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.data.DataMissingException;

public class DataGroupTest {
	@Test
	public void testInit() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");

		assertEquals(dataGroup.getNameInData(), "nameInData",
				"NameInData should be the same as the one set in the constructor.");
	}

	@Test
	public void testAddAttribute() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		dataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(dataGroup.getAttribute("attributeId"), "attributeValue",
				"Attribute should be the same as the one added to the group");
	}

	@Test
	public void testGetAttributes() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		dataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(dataGroup.getAttributes().get("attributeId"), "attributeValue",
				"Attribute should be the same as the one added to the group");

	}

	@Test
	public void testAddChild() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		assertEquals(dataGroup.getChildren().iterator().next(), child,
				"Child should be the same as the one added to the group");
	}

	@Test
	public void testContainsChildWithId() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		assertTrue(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testContainsChildWithIdNotFound() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		assertFalse(dataGroup.containsChildWithNameInData("childId_NOT_FOUND"));
	}

	@Test
	public void testGetFirstAtomicValueWithNameInData() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		String value = dataGroup.getFirstAtomicValueWithNameInData("childId");
		assertEquals(value, "child value");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstAtomicValueWithIdNotFound() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		dataGroup.getFirstAtomicValueWithNameInData("childId_NOTFOUND");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstAtomicValueWithIdNotFoundGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataGroup.withNameInData("groupId2");
		dataGroup.addChild(child);
		dataGroup.getFirstAtomicValueWithNameInData("groupId2");
	}

	@Test
	public void testGetFirstGroupWithNameInData() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataGroup.withNameInData("groupId2");
		dataGroup.addChild(child);
		DataGroup group = dataGroup.getFirstGroupWithNameInData("groupId2");
		assertEquals(group, child);
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstGroupWithIdNotFound() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataGroup.withNameInData("groupId2");
		dataGroup.addChild(child);
		dataGroup.getFirstGroupWithNameInData("groupId2_NOTFOUND");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstGroupWithIdNotFoundGroup() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		dataGroup.getFirstGroupWithNameInData("childId");
	}

}
