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

import java.util.Collection;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataGroupTest {
	private DataGroup dataGroup;
	private Collection<DataGroup> groupsFound;

	@BeforeMethod
	public void setUp() {
		dataGroup = DataGroup.withNameInData("nameInData");
	}

	@Test
	public void testInit() {
		assertEquals(dataGroup.getNameInData(), "nameInData",
				"NameInData should be the same as the one set in the constructor.");
	}

	@Test
	public void testGroupIsData() {
		assertTrue(dataGroup instanceof Data);
	}

	@Test
	public void testInitWithRepeatId() {
		dataGroup.setRepeatId("gh");
		assertEquals(dataGroup.getNameInData(), "nameInData",
				"NameInData should be the same as the one set in the constructor.");
		assertEquals(dataGroup.getRepeatId(), "gh");
	}

	@Test
	public void testAddAttribute() {
		dataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(dataGroup.getAttribute("attributeId"), "attributeValue",
				"Attribute should be the same as the one added to the group");
	}

	@Test
	public void testGetAttributes() {
		dataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(dataGroup.getAttributes().get("attributeId"), "attributeValue",
				"Attribute should be the same as the one added to the group");

	}

	@Test
	public void testAddChild() {
		DataElement child = createAndAddAnAtomicChildToDataGroup();
		assertEquals(dataGroup.getChildren().iterator().next(), child,
				"Child should be the same as the one added to the group");
	}

	private DataElement createAndAddAnAtomicChildToDataGroup() {
		DataElement child = DataAtomic.withNameInDataAndValue("childId", "child value");
		dataGroup.addChild(child);
		return child;
	}

	@Test
	public void testContainsChildWithId() {
		createAndAddAnAtomicChildToDataGroup();
		assertTrue(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testContainsChildWithIdNotFound() {
		createAndAddAnAtomicChildToDataGroup();
		assertFalse(dataGroup.containsChildWithNameInData("childId_NOT_FOUND"));
	}

	@Test
	public void testGetFirstAtomicValueWithNameInData() {
		createAndAddAnAtomicChildToDataGroup();
		String value = dataGroup.getFirstAtomicValueWithNameInData("childId");
		assertEquals(value, "child value");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstAtomicValueWithIdNotFound() {
		createAndAddAnAtomicChildToDataGroup();
		dataGroup.getFirstAtomicValueWithNameInData("childId_NOTFOUND");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstAtomicValueWithIdNotFoundGroup() {
		addAndReturnDataGroupChildWithNameInData("groupId2");
		dataGroup.getFirstAtomicValueWithNameInData("groupId2");
	}

	@Test
	public void testGetFirstGroupWithNameInData() {
		DataElement child = addAndReturnDataGroupChildWithNameInData("groupId2");
		DataGroup group = dataGroup.getFirstGroupWithNameInData("groupId2");
		assertEquals(group, child);
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstGroupWithIdNotFound() {
		addAndReturnDataGroupChildWithNameInData("groupId2");
		dataGroup.getFirstGroupWithNameInData("groupId2_NOTFOUND");
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstGroupWithIdNotFoundGroup() {
		createAndAddAnAtomicChildToDataGroup();
		dataGroup.getFirstGroupWithNameInData("childId");
	}

	@Test
	public void testGetFirstChildWithNameInData() {
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("some", "value"));
		DataElement child = addAndReturnDataGroupChildWithNameInData("groupId2");
		DataElement childOut = dataGroup.getFirstChildWithNameInData("groupId2");
		assertEquals(childOut, child);
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testGetFirstChildWithIdNotFound() {
		addAndReturnDataGroupChildWithNameInData("groupId2");
		dataGroup.getFirstChildWithNameInData("groupId2_NOTFOUND");
	}

	@Test
	public void testRemoveChild() {
		createAndAddAnAtomicChildToDataGroup();
		dataGroup.removeFirstChildWithNameInData("childId");
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test(expectedExceptions = DataMissingException.class)
	public void testRemoveChildNotFound() {
		createAndAddAnAtomicChildToDataGroup();
		dataGroup.removeFirstChildWithNameInData("childId_NOTFOUND");
	}

	@Test
	public void testGetAllGroupsWithNameInData() {
		DataGroup child = addAndReturnDataGroupChildWithNameInData("groupId2");
		groupsFound = dataGroup.getAllGroupsWithNameInData("groupId2");
		assertNumberOfGroupsFoundIs(1);
		assertGroupsFoundAre(child);
	}

	private void assertNumberOfGroupsFoundIs(int numberOfGroups) {
		assertEquals(groupsFound.size(), numberOfGroups);
	}

	@Test
	public void testGetAllGroupsWithNameInDataTwoGroups() {
		DataGroup child = addAndReturnDataGroupChildWithNameInData("groupId2");
		DataGroup child2 = addAndReturnDataGroupChildWithNameInData("groupId2");
		groupsFound = dataGroup.getAllGroupsWithNameInData("groupId2");
		assertNumberOfGroupsFoundIs(2);
		assertGroupsFoundAre(child, child2);
	}

	private void assertGroupsFoundAre(DataGroup... groups) {
		int i = 0;
		for (DataGroup groupFound : groupsFound) {
			assertEquals(groupFound, groups[i]);
			i++;
		}
	}

	@Test
	public void testGetAllGroupsWithNameInDataMoreChildren() {
		DataGroup child = addAndReturnDataGroupChildWithNameInData("groupId2");
		addAndReturnDataGroupChildWithNameInData("groupId3");
		DataGroup child2 = addAndReturnDataGroupChildWithNameInData("groupId2");
		createDataAtomicWithNameInData("groupId2");
		groupsFound = dataGroup.getAllGroupsWithNameInData("groupId2");
		assertNumberOfGroupsFoundIs(2);
		assertGroupsFoundAre(child, child2);
	}

	private DataAtomic createDataAtomicWithNameInData(String nameInData) {
		DataAtomic child4 = DataAtomic.withNameInDataAndValue(nameInData, "someValue");
		return child4;
	}

	private DataGroup addAndReturnDataGroupChildWithNameInData(String nameInData) {
		DataGroup child = DataGroup.withNameInData(nameInData);
		dataGroup.addChild(child);
		return child;
	}

	@Test
	public void testGetAllDataAtomicsWithNameInData() {
		DataGroup book = createDataGroupWithTwoAtomicChildrenAndOneGroupChild();

		assertEquals(book.getAllDataAtomicsWithNameInData("someChild").size(), 2);

	}

	private DataGroup createDataGroupWithTwoAtomicChildrenAndOneGroupChild() {
		DataGroup book = DataGroup.withNameInData("book");
		DataAtomic child1 = DataAtomic.withNameInDataAndValue("someChild", "child1");
		child1.setRepeatId("0");
		book.addChild(child1);

		DataAtomic child2 = DataAtomic.withNameInDataAndValue("someChild", "child2");
		child2.setRepeatId("1");
		book.addChild(child2);

		DataGroup child3 = DataGroup.withNameInData("someChild");
		book.addChild(child3);
		return book;
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneMatch() {
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute();

		groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertNumberOfGroupsFoundIs(1);
		assertGroupsFoundAre(child3);
	}

	private DataGroup createTestGroupForAttributesReturnChildGroupWithAttribute() {
		addAndReturnDataGroupChildWithNameInData("groupId2");
		addAndReturnDataGroupChildWithNameInData("groupId3");
		addAndReturnDataGroupChildWithNameInData("groupId2");
		createDataAtomicWithNameInData("groupId2");
		DataGroup child3 = addAndReturnDataGroupChildWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"));
		return child3;
	}

	private DataGroup addAndReturnDataGroupChildWithNameInDataAndAttributes(String nameInData,
			DataAttribute... attributes) {
		DataGroup child = DataGroup.withNameInData(nameInData);
		dataGroup.addChild(child);
		for (DataAttribute attribute : attributes) {
			child.addAttributeByIdWithValue(attribute.getNameInData(), attribute.getValue());
		}
		return child;
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesTwoMatches() {
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute();
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"));

		groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertNumberOfGroupsFoundIs(2);
		assertGroupsFoundAre(child3, child4);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneWrongAttributeValueTwoMatches() {
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute();
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"));
		addAndReturnDataGroupChildWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value2"));

		groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertNumberOfGroupsFoundIs(2);
		assertGroupsFoundAre(child3, child4);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneWrongAttributeNameTwoMatches() {
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute();
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"));
		addAndReturnDataGroupChildWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData2", "value1"));

		groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertNumberOfGroupsFoundIs(2);
		assertGroupsFoundAre(child3, child4);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndTwoAttributesNoMatches() {
		createTestGroupForAttributesReturnChildGroupWithAttribute();
		addAndReturnDataGroupChildWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"),
				DataAttribute.withNameInDataAndValue("nameInData2", "value2"));

		groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"),
				DataAttribute.withNameInDataAndValue("nameInData2", "value1"));

		assertNumberOfGroupsFoundIs(0);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndTwoAttributesOneMatches() {
		createTestGroupForAttributesReturnChildGroupWithAttribute();
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"),
				DataAttribute.withNameInDataAndValue("nameInData2", "value2"));
		addAndReturnDataGroupChildWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"),
				DataAttribute.withNameInDataAndValue("nameInData3", "value2"));

		groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes("groupId2",
				DataAttribute.withNameInDataAndValue("nameInData", "value1"),
				DataAttribute.withNameInDataAndValue("nameInData2", "value2"));

		assertNumberOfGroupsFoundIs(1);
		assertGroupsFoundAre(child4);
	}

	@Test
	public void testDefaultOnGetFirstAtomicValueWithNameInDataOrDefault() {
		String expected = "some default value";
		String actual = dataGroup.getFirstAtomicValueWithNameInDataOrDefault("missing child",
				expected);
		assertEquals(actual, expected);
	}

	@Test
	public void testActualOnGetFirstAtomicValueWithNameInDataOrDefault() {
		String expected = "some child value";
		DataAtomic child = DataAtomic.withNameInDataAndValue("someChild", expected);
		dataGroup.addChild(child);
		String actual = dataGroup.getFirstAtomicValueWithNameInDataOrDefault("someChild",
				"some default value");
		assertEquals(actual, expected);
	}

	@Test
	public void testGroupAsLink() {
		dataGroup = DataGroup.asLinkWithNameInDataTypeAndId("nameInData", "someType", "someId");
		assertEquals(dataGroup.getNameInData(), "nameInData");
		assertEquals(dataGroup.getFirstAtomicValueWithNameInData("linkedRecordType"), "someType");
		assertEquals(dataGroup.getFirstAtomicValueWithNameInData("linkedRecordId"), "someId");
	}
}
