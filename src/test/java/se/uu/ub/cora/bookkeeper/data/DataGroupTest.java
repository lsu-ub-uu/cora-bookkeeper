/*
 * Copyright 2015 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.bookkeeper.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class DataGroupTest {
	@Test
	public void testInit() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");

		assertEquals(dataGroup.getNameInData(), "nameInData",
				"NameInData should be the same as the one set in the constructor.");
	}

	@Test
	public void testGroupIsData() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		assertTrue(dataGroup instanceof Data);
	}

	@Test
	public void testInitWithRepeatId() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		dataGroup.setRepeatId("gh");
		assertEquals(dataGroup.getNameInData(), "nameInData",
				"NameInData should be the same as the one set in the constructor.");
		assertEquals(dataGroup.getRepeatId(), "gh");
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

	@Test
	public void testGetFirstChildWithNameInData() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("some", "value"));
		DataElement child = DataGroup.withNameInData("groupId2");
		dataGroup.addChild(child);
		DataElement childOut = dataGroup.getFirstChildWithNameInData("groupId2");
		assertEquals(childOut, child);
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstChildWithIdNotFound() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataGroup.withNameInData("groupId2");
		dataGroup.addChild(child);
		dataGroup.getFirstChildWithNameInData("groupId2_NOTFOUND");
	}

	@Test
	public void testRemoveChild() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		dataGroup.removeFirstChildWithNameInData("childId");
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testRemoveChildNotFound() {
		DataGroup dataGroup = DataGroup.withNameInData("nameInData");
		DataElement child = DataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		dataGroup.removeFirstChildWithNameInData("childId_NOTFOUND");
	}

}
