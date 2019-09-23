/*
 * Copyright 2015, 2019 Uppsala University Library
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

package se.uu.ub.cora.bookkeeper.linkcollector;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;

public class DataGroupRecordLinkCollectorTest {
	private DataGroupRecordLinkCollector linkCollector;
	private DataGroupRecordLinkCollectorMetadataCreator dataGroupRecordLinkCollectorMetadataCreator;

	private DataGroupFactorySpy dataGroupFactory;
	private DataAtomicFactorySpy dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupRecordLinkCollectorMetadataCreator = new DataGroupRecordLinkCollectorMetadataCreator();
		linkCollector = new DataGroupRecordLinkCollector(
				dataGroupRecordLinkCollectorMetadataCreator.getMetadataHolder(), "fromRecordType",
				"fromRecordId");
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
	}

	@Test
	public void testOneGroupWithNoLink() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupWithNoLink("test");
		DataGroup dataGroup = new DataGroupSpy("testGroup");

		assertEquals(dataGroupFactory.usedNameInDatas.size(), 0);
		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 0);
		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);
		assertEquals(linkList.size(), 0);
	}

	@Test
	public void testOneGroupWithOneLink() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupWithOneLink("test");
		DataGroup dataGroup = createDataGroupWithOneLink();
		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);

		assertCorrectOneGroupWithOneLink(linkList);
	}

	private DataGroup createDataGroupWithOneLink() {
		DataGroup dataGroup = new DataGroupSpy("testGroup");
		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);
		return dataGroup;
	}

	private DataGroup createTestLinkWithRecordTypeAndRecordId() {
		DataGroup dataRecordLink = new DataGroupSpy("testLink");
		DataAtomic linkedRecordType = new DataAtomicSpy("linkedRecordType", "someRecordType");
		dataRecordLink.addChild(linkedRecordType);
		DataAtomic linkedRecordId = new DataAtomicSpy("linkedRecordId", "someRecordId");
		dataRecordLink.addChild(linkedRecordId);
		return dataRecordLink;
	}

	private void assertCorrectOneGroupWithOneLink(List<DataGroup> linkList) {
		assertEquals(linkList.size(), 1);

		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 4);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(1), "recordToRecordLink");
		assertEquals(namesOfGroupsFactored.get(2), "from");
		assertEquals(namesOfGroupsFactored.get(3), "to");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 5);
		assertEquals(dataAtomicFactory.usedValues.size(), 5);

		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "testLink");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "linkedRecordType",
				"fromRecordType");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "linkedRecordId", "fromRecordId");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(3, "linkedRecordType",
				"someAtomicValueFromSpyForlinkedRecordType");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(4, "linkedRecordId",
				"someAtomicValueFromSpyForlinkedRecordId");
	}

	private void assertCorrectAtomicDataUsingIndexNameInDataAndValue(int index, String nameInData,
			String value) {
		List<String> namesOfAtomicDataFactored = dataAtomicFactory.usedNameInDatas;
		List<String> valuesOfAtomicDataFactored = dataAtomicFactory.usedValues;
		assertEquals(namesOfAtomicDataFactored.get(index), nameInData);
		assertEquals(valuesOfAtomicDataFactored.get(index), value);

	}
	//
	// private void assertCorrectFromPartOfRecordLink(DataGroup recordToRecordLink) {
	// DataGroup fromRecordLink = recordToRecordLink.getFirstGroupWithNameInData("from");
	// assertEquals(fromRecordLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "someAtomicValueFromSpyForlinkedRecordType");
	// assertEquals(fromRecordLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "someAtomicValueFromSpyForlinkedRecordId");
	// assertFalse(fromRecordLink.containsChildWithNameInData("linkedRepeatId"));
	// }

	private void assertCorrectToPartOfRecordLink(DataGroup recordToRecordLink) {
		DataGroup toRecordLink = recordToRecordLink.getFirstGroupWithNameInData("to");
		assertEquals(toRecordLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"someAtomicValueFromSpyForlinkedRecordType");
		assertEquals(toRecordLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someAtomicValueFromSpyForlinkedRecordId");
	}

	@Test
	public void testOneGroupWithOneLinkAndOtherChildren() {
		dataGroupRecordLinkCollectorMetadataCreator
				.createMetadataForOneGroupWithOneLinkAndOtherChildren();
		DataGroup dataGroup = createDataGroupWithOneLink();
		addOtherChild(dataGroup);

		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 5);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(1), "recordToRecordLink");
		assertEquals(namesOfGroupsFactored.get(2), "from");
		assertEquals(namesOfGroupsFactored.get(3), "to");
		// since one child is a group, a path is created (though not used if no links in child
		// group)
		assertEquals(namesOfGroupsFactored.get(4), "linkedPath");
		assertCorrectOneGroupWithOneLinkAndOtherChildren(linkList);
	}

	private void addOtherChild(DataGroup dataGroup) {
		DataAtomic dataAtomic = new DataAtomicSpy("textVar", "some text");
		dataGroup.addChild(dataAtomic);
		DataGroup dataSubGroup = new DataGroupSpy("subGroup");
		dataGroup.addChild(dataSubGroup);
	}

	private void assertCorrectOneGroupWithOneLinkAndOtherChildren(List<DataGroup> linkList) {
		assertEquals(linkList.size(), 1);

		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");
		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 6);
		assertEquals(dataAtomicFactory.usedValues.size(), 6);

		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "testLink");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "linkedRecordType",
				"fromRecordType");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "linkedRecordId", "fromRecordId");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(3, "linkedRecordType",
				"someAtomicValueFromSpyForlinkedRecordType");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(4, "linkedRecordId",
				"someAtomicValueFromSpyForlinkedRecordId");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(5, "nameInData", "subGroup");
		// assertCorrectToPartOfRecordLinkContainingPathAndRepeatId(recordToRecordLink);
	}

	// private void assertCorrectToPartOfRecordLinkContainingPathAndRepeatId(
	// DataGroup recordToRecordLink) {
	// DataGroup toRecordLink = recordToRecordLink.getFirstGroupWithNameInData("to");
	// assertEquals(toRecordLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "someAtomicValueFromSpyForlinkedRecordType");
	// assertEquals(toRecordLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "someAtomicValueFromSpyForlinkedRecordId");
	// assertFalse(toRecordLink.containsChildWithNameInData("linkedPath"));
	// assertFalse(toRecordLink.containsChildWithNameInData("repeatId"));
	// }

	@Test
	public void testOneGroupWithOneLinkWithPath() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupWithOneLinkWithPath();
		DataGroup dataGroup = createDataGroupContainingLink();

		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);
		assertEquals(linkList.size(), 1);

		assertCorrectOneGroupWithOneLink(linkList);
	}

	// private void assertCorrectOneGroupWithOneLinkWithPath(List<DataGroup> linkList) {
	// DataGroup recordToRecordLink = linkList.get(0);
	// assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");
	//
	// assertCorrectOneGroupWithOneLinkWithPathFromPart(recordToRecordLink);
	//
	// DataGroup toRecordLink = recordToRecordLink.getFirstGroupWithNameInData("to");
	// assertCorrectOneGroupWithOneLinkWithPathToPart(toRecordLink);
	// assertCorrectOneGroupWithOneLinkWithPathPathPart(toRecordLink);
	// }

	// private void assertCorrectOneGroupWithOneLinkWithPathFromPart(DataGroup recordToRecordLink) {
	// DataGroup fromRecordLink = recordToRecordLink.getFirstGroupWithNameInData("from");
	//
	// assertEquals(fromRecordLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "someAtomicValueFromSpyForlinkedRecordType");
	// assertEquals(fromRecordLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "someAtomicValueFromSpyForlinkedRecordId");
	//
	// String fromLinkedRepeatId = fromRecordLink
	// .getFirstAtomicValueWithNameInData("linkedRepeatId");
	// assertEquals(fromLinkedRepeatId, "someAtomicValueFromSpyForlinkedRepeatId");
	// }
	//
	// private void assertCorrectOneGroupWithOneLinkWithPathToPart(DataGroup toRecordLink) {
	// assertEquals(toRecordLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "someAtomicValueFromSpyForlinkedRecordType");
	// assertEquals(toRecordLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "someAtomicValueFromSpyForlinkedRecordId");
	//
	// assertEquals(toRecordLink.getFirstAtomicValueWithNameInData("linkedRepeatId"),
	// "someAtomicValueFromSpyForlinkedRepeatId");
	// }
	//
	// private void assertCorrectOneGroupWithOneLinkWithPathPathPart(DataGroup toRecordLink) {
	// DataGroup linkedPath = toRecordLink.getFirstGroupWithNameInData("linkedPath");
	// assertNotNull(linkedPath);
	// assertEquals(linkedPath.getNameInData(), "linkedPath");
	// assertEquals(linkedPath.getFirstAtomicValueWithNameInData("nameInData"),
	// "someAtomicValueFromSpyFornameInData");
	// }

	private DataGroup createDataGroupContainingLink() {
		// DataGroup dataGroup = DataGroupProvider.getDataGroupUsingNameInData("testGroup");
		DataGroup dataGroup = new DataGroupSpy("testGroup");

		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);

		DataAtomic linkedRepeatId = new DataAtomicSpy("linkedRepeatId", "e3");
		// DataAtomic linkedRepeatId = DataAtomicProvider
		// .getDataAtomicUsingNameInDataAndValue("linkedRepeatId", "e3");
		// DataAtomic linkedRepeatId = DataAtomic.withNameInDataAndValue("linkedRepeatId", "e3");
		dataRecordLink.addChild(linkedRepeatId);
		dataRecordLink.setRepeatId("e3");
		return dataGroup;
	}

	@Test
	public void testOneGroupWithOneLinkWithEmptyLinkedRepeatId() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupWithOneLinkWithPath();
		DataGroup dataGroup = new DataGroupSpy("testGroup");

		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);

		// DataAtomic linkedRepeatId = DataAtomicProvider
		// .getDataAtomicUsingNameInDataAndValue("linkedRepeatId", "someLinkedRepeatId");
		DataAtomic linkedRepeatId = new DataAtomicSpy("linkedRepeatId", "someLinkedRepeatId");
		dataRecordLink.addChild(linkedRepeatId);
		dataRecordLink.setRepeatId("");

		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);
		assertEquals(linkList.size(), 1);

		assertCorrectOneGroupWithOneLink(linkList);
	}

	@Test
	public void testOneGroupInGroupWithOneLink() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupInGroupWithOneLink();
		DataGroup topDataGroup = createGroupInGroupWithOneLink();

		List<DataGroup> linkList = linkCollector.collectLinks("topGroup", topDataGroup);

		assertCorrectOneGroupInGroupWithOneLink(linkList);
	}

	private DataGroup createGroupInGroupWithOneLink() {
		DataGroup topDataGroup = new DataGroupSpy("topGroup");
		// DataGroup topDataGroup = DataGroupProvider.getDataGroupUsingNameInData("topGroup");
		// DataGroup dataGroup = DataGroupProvider.getDataGroupUsingNameInData("testGroup");
		DataGroup dataGroup = new DataGroupSpy("testGroup");
		topDataGroup.addChild(dataGroup);

		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);
		return topDataGroup;
	}

	private void assertCorrectOneGroupInGroupWithOneLink(List<DataGroup> linkList) {
		assertEquals(linkList.size(), 1);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 6);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(1), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(2), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(3), "recordToRecordLink");
		assertEquals(namesOfGroupsFactored.get(4), "from");
		assertEquals(namesOfGroupsFactored.get(5), "to");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 7);
		assertEquals(dataAtomicFactory.usedValues.size(), 7);

		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "testGroup");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "nameInData",
				"someAtomicValueFromSpyFornameInData");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "nameInData", "testLink");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(3, "linkedRecordType",
				"fromRecordType");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(4, "linkedRecordId", "fromRecordId");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(5, "linkedRecordType",
				"someAtomicValueFromSpyForlinkedRecordType");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(6, "linkedRecordId",
				"someAtomicValueFromSpyForlinkedRecordId");
	}

	// private void assertCorrectOneGroupInGroupWithOneLinkFromPart(DataGroup fromRecordLink) {
	// assertEquals(fromRecordLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
	// "someAtomicValueFromSpyForlinkedRecordType");
	// assertEquals(fromRecordLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
	// "someAtomicValueFromSpyForlinkedRecordId");
	// }
	//
	// private void assertCorrectOneGroupInGroupWithOneLinkLinkedPathPart(DataGroup fromRecordLink)
	// {
	// DataGroup fromLinkedPath = fromRecordLink.getFirstGroupWithNameInData("linkedPath");
	//
	// assertNotNull(fromLinkedPath);
	// assertEquals(fromLinkedPath.getFirstAtomicValueWithNameInData("nameInData"),
	// "someAtomicValueFromSpyFornameInData");
	// assertTrue(fromLinkedPath.containsChildWithNameInData("linkedPath"));
	// assertFalse(fromLinkedPath.containsChildWithNameInData("repeatId"));
	// assertFalse(fromLinkedPath.containsChildWithNameInData("attributes"));
	//
	// DataGroup fromLinkedPathSub1 = fromLinkedPath.getFirstGroupWithNameInData("linkedPath");
	// assertFalse(fromLinkedPathSub1.containsChildWithNameInData("repeatId"));
	// assertEquals(fromLinkedPathSub1.getFirstAtomicValueWithNameInData("nameInData"),
	// "testLink");
	// assertFalse(fromLinkedPathSub1.containsChildWithNameInData("attributes"));
	// assertFalse(fromLinkedPathSub1.containsChildWithNameInData("linkedPath"));
	// }

	@Test
	public void testOneGroupInGroupInGroupWithOneLink() {
		dataGroupRecordLinkCollectorMetadataCreator
				.addMetadataForOneGroupInGroupInGroupWithOneLink();

		DataGroup topTopDataGroup = createGroupInGroupInGroupWithOneLink();
		List<DataGroup> linkList = linkCollector.collectLinks("topTopGroup", topTopDataGroup);
		assertCorrectOneGroupInGroupInGroupWithOneLink(linkList);
	}

	private DataGroup createGroupInGroupInGroupWithOneLink() {
		DataGroup topTopDataGroup = DataGroup.withNameInData("topTopGroup");

		DataGroup topDataGroup = DataGroup.withNameInData("topGroup");
		topDataGroup.addAttributeByIdWithValue("attribute1", "attrValue");
		topDataGroup.setRepeatId("g6");
		topTopDataGroup.addChild(topDataGroup);

		DataGroup dataGroup = DataGroup.withNameInData("testGroup");
		topDataGroup.addChild(dataGroup);

		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);
		return topTopDataGroup;
	}

	private void assertCorrectOneGroupInGroupInGroupWithOneLink(List<DataGroup> linkList) {
		assertEquals(linkList.size(), 1);

		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		assertCorrectFromPartOfOneGroupInGroupInGroupWithOneLink(recordToRecordLink);
		assertCorrectToPartOfOneGroupInGroupInGroupWithOneLink(recordToRecordLink);
	}

	private void assertCorrectFromPartOfOneGroupInGroupInGroupWithOneLink(
			DataGroup recordToRecordLink) {
		DataGroup fromRecordLink = recordToRecordLink.getFirstGroupWithNameInData("from");
		assertEquals(fromRecordLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"fromRecordType");
		assertEquals(fromRecordLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"fromRecordId");

		DataGroup fromLinkedPath = fromRecordLink.getFirstGroupWithNameInData("linkedPath");

		assertCorrectFromLinkedPathOfOneGroupInGroupInGroupWithOneLink(fromLinkedPath);
		assertCorrectAttributesOfOneGroupInGroupInGroupWithOneLink(fromLinkedPath);
		assertCorrectSubLinkedPathOfOneGroupInGroupInGroupWithOneLink(fromLinkedPath);
	}

	private void assertCorrectFromLinkedPathOfOneGroupInGroupInGroupWithOneLink(
			DataGroup fromLinkedPath) {
		assertNotNull(fromLinkedPath);
		assertTrue(fromLinkedPath.containsChildWithNameInData("repeatId"));
		assertEquals(fromLinkedPath.getFirstAtomicValueWithNameInData("nameInData"), "topGroup");
		assertTrue(fromLinkedPath.containsChildWithNameInData("attributes"));
	}

	private void assertCorrectAttributesOfOneGroupInGroupInGroupWithOneLink(
			DataGroup fromLinkedPath) {
		DataGroup attributes = fromLinkedPath.getFirstGroupWithNameInData("attributes");
		DataGroup attribute = attributes.getFirstGroupWithNameInData("attribute");
		assertEquals(attribute.getFirstAtomicValueWithNameInData("attributeName"), "attribute1");
		assertEquals(attribute.getFirstAtomicValueWithNameInData("attributeValue"), "attrValue");
		assertTrue(fromLinkedPath.containsChildWithNameInData("linkedPath"));
	}

	private void assertCorrectSubLinkedPathOfOneGroupInGroupInGroupWithOneLink(
			DataGroup fromLinkedPath) {
		DataGroup fromLinkedPathSub1 = fromLinkedPath.getFirstGroupWithNameInData("linkedPath");
		assertCorrectSubLinkedPath(fromLinkedPathSub1);

		assertCorrectSubLinkedPathOfSubLinkedPath(fromLinkedPathSub1);
	}

	private void assertCorrectSubLinkedPath(DataGroup fromLinkedPathSub1) {
		assertFalse(fromLinkedPathSub1.containsChildWithNameInData("repeatId"));
		assertEquals(fromLinkedPathSub1.getFirstAtomicValueWithNameInData("nameInData"),
				"testGroup");
		assertFalse(fromLinkedPathSub1.containsChildWithNameInData("attributes"));
		assertTrue(fromLinkedPathSub1.containsChildWithNameInData("linkedPath"));
	}

	private void assertCorrectSubLinkedPathOfSubLinkedPath(DataGroup fromLinkedPathSub1) {
		DataGroup fromLinkedPathSub2 = fromLinkedPathSub1.getFirstGroupWithNameInData("linkedPath");
		assertFalse(fromLinkedPathSub2.containsChildWithNameInData("repeatId"));
		assertEquals(fromLinkedPathSub2.getFirstAtomicValueWithNameInData("nameInData"),
				"testLink");
		assertFalse(fromLinkedPathSub2.containsChildWithNameInData("attributes"));
		assertFalse(fromLinkedPathSub2.containsChildWithNameInData("linkedPath"));
	}

	private void assertCorrectToPartOfOneGroupInGroupInGroupWithOneLink(
			DataGroup recordToRecordLink) {
		assertCorrectToPartOfRecordLink(recordToRecordLink);
	}
}
