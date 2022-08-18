/**Copyright 2015,2019 Uppsala University Library**This file is part of Cora.**Cora is free software:you can redistribute it and/or modify*it under the terms of the GNU General Public License as published by*the Free Software Foundation,either version 3 of the License,or*(at your option)any later version.**Cora is distributed in the hope that it will be useful,*but WITHOUT ANY WARRANTY;without even the implied warranty of*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the*GNU General Public License for more details.**You should have received a copy of the GNU General Public License*along with Cora.If not,see<http://www.gnu.org/licenses/>.
*/

package se.uu.ub.cora.bookkeeper.linkcollector;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.testspies.data.DataFactorySpy;

public class DataGroupRecordLinkCollectorTest {
	private DataGroupRecordLinkCollector linkCollector;
	private DataGroupRecordLinkCollectorMetadataCreator dataGroupRecordLinkCollectorMetadataCreator;

	private DataFactorySpy dataFactorySpy;
	private DataGroupFactorySpy dataGroupFactory;
	private DataAtomicFactorySpy dataAtomicFactory;
	private DataRecordLinkFactorySpy dataRecordLinkFactory;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
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
		DataGroup dataGroup = new DataGroupOldSpy("testGroup");

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

		assertCorrectOneGroupWithOneLink(linkList, 1);
	}

	@Test
	public void testFromAndToPartsAreCorrectTypes() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupWithOneLink("test");
		DataGroup dataGroup = createDataGroupWithOneLink();
		linkCollector.collectLinks("testGroup", dataGroup);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.get(1), "recordToRecordLink");
	}

	private DataGroup createDataGroupWithOneLink() {
		DataGroup dataGroup = new DataGroupOldSpy("testGroup");
		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);
		return dataGroup;
	}

	private DataGroup createTestLinkWithRecordTypeAndRecordId() {
		DataGroup dataRecordLink = new DataGroupOldSpy("testLink");
		DataAtomic linkedRecordType = new DataAtomicSpy("linkedRecordType", "someRecordType");
		dataRecordLink.addChild(linkedRecordType);
		DataAtomic linkedRecordId = new DataAtomicSpy("linkedRecordId", "someRecordId");
		dataRecordLink.addChild(linkedRecordId);
		return dataRecordLink;
	}

	private void assertCorrectOneGroupWithOneLink(List<DataGroup> linkList,
			int numOfAtomicFactored) {
		assertEquals(linkList.size(), 1);
		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		assertCorrectGroupsFactoredForOneGroupWithOneLink();

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), numOfAtomicFactored);
		assertEquals(dataAtomicFactory.usedValues.size(), numOfAtomicFactored);

		assertCorrectChildrenForBasicLink();
	}

	private void assertCorrectChildrenForBasicLink() {
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "testLink");
	}

	private void assertCorrectGroupsFactoredForOneGroupWithOneLink() {
		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 2);
		assertEquals(namesOfGroupsFactored.get(1), "recordToRecordLink");
	}

	private void assertCorrectAtomicDataUsingIndexNameInDataAndValue(int index, String nameInData,
			String value) {
		List<String> namesOfAtomicDataFactored = dataAtomicFactory.usedNameInDatas;
		List<String> valuesOfAtomicDataFactored = dataAtomicFactory.usedValues;
		assertEquals(namesOfAtomicDataFactored.get(index), nameInData);
		assertEquals(valuesOfAtomicDataFactored.get(index), value);

	}

	@Test
	public void testOneGroupWithOneLinkAndOtherChildren() {
		dataGroupRecordLinkCollectorMetadataCreator
				.createMetadataForOneGroupWithOneLinkAndOtherChildren();
		DataGroup dataGroup = createDataGroupWithOneLink();
		addOtherChild(dataGroup);

		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 3);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(1), "recordToRecordLink");
		// since one child is a group, a path is created (though not used if no links in child
		// group)
		// assertEquals(namesOfGroupsFactored.get(2), "from");
		// assertEquals(namesOfGroupsFactored.get(3), "to");
		assertEquals(namesOfGroupsFactored.get(2), "linkedPath");

		assertCorrectOneGroupWithOneLinkAndOtherChildren(linkList);
	}

	private void addOtherChild(DataGroup dataGroup) {
		DataAtomic dataAtomic = new DataAtomicSpy("textVar", "some text");
		dataGroup.addChild(dataAtomic);
		DataGroup dataSubGroup = new DataGroupOldSpy("subGroup");
		dataGroup.addChild(dataSubGroup);
	}

	private void assertCorrectOneGroupWithOneLinkAndOtherChildren(List<DataGroup> linkList) {
		assertEquals(linkList.size(), 1);

		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");
		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 2);
		assertEquals(dataAtomicFactory.usedValues.size(), 2);

		assertCorrectChildrenForBasicLink();
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "nameInData", "subGroup");
	}

	@Test
	public void testOneGroupWithOneLinkWithPathAndRepeatId() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupWithOneLinkWithPath();
		DataGroup dataGroup = createDataGroupContainingLinkWithRepeatId();

		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);

		assertEquals(linkList.size(), 1);

		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		assertCorrectGroupsFactoredForOneGroupWithOneLink();

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 2);
		assertEquals(dataAtomicFactory.usedValues.size(), 2);

		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "testLink");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "repeatId", "e3");
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "linkedRecordType",
		// "fromRecordType");
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(3, "linkedRecordId", "fromRecordId");
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(4, "linkedRepeatId", "e3");
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(5, "linkedRecordType",
		// "someRecordType");
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(6, "linkedRecordId", "someRecordId");
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(7, "linkedRepeatId", "e3");

		// assertCorrectOneGroupWithOneLink(linkList);
	}

	private DataGroup createDataGroupContainingLinkWithRepeatId() {
		// DataGroup dataGroup = DataGroupProvider.getDataGroupUsingNameInData("testGroup");
		DataGroup dataGroup = new DataGroupOldSpy("testGroup");

		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);

		DataAtomic linkedRepeatId = new DataAtomicSpy("linkedRepeatId", "e3");
		dataRecordLink.addChild(linkedRepeatId);
		dataRecordLink.setRepeatId("e3");
		return dataGroup;
	}

	@Test
	public void testOneGroupWithOneLinkWithEmptyFromLinkedRepeatId() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupWithOneLink("test");
		DataGroup dataGroup = new DataGroupOldSpy("testGroup");

		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);

		DataAtomic linkedRepeatId = new DataAtomicSpy("linkedRepeatId", "someLinkedRepeatId");
		dataRecordLink.addChild(linkedRepeatId);
		dataRecordLink.setRepeatId("");

		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);
		assertEquals(linkList.size(), 1);

		assertCorrectOneGroupWithOneLink(linkList, 1);
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "linkedRepeatId",
		// "someLinkedRepeatId");

		DataGroup recordToRecordLink = linkList.get(0);
		// DatssertFalse(fromRecordLink.containsChildWithNameInData("linkedRepeatId"));

		// DataGroup toRecordLink = recordToRecordLink.getFirstGroupWithNameInData("to");
		// TODO: test...
		// assertTrue(toRecordLink.containsChildWithNameInData("linkedRepeatId"));
	}

	@Test
	public void testOneGroupInGroupWithOneLink() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupInGroupWithOneLink();
		DataGroup topDataGroup = createGroupInGroupWithOneLink();

		List<DataGroup> linkList = linkCollector.collectLinks("topGroup", topDataGroup);

		assertCorrectOneGroupInGroupWithOneLink(linkList);
	}

	private DataGroup createGroupInGroupWithOneLink() {
		DataGroup topDataGroup = new DataGroupOldSpy("topGroup");
		DataGroup dataGroup = new DataGroupOldSpy("testGroup");
		topDataGroup.addChild(dataGroup);

		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);
		return topDataGroup;
	}

	private void assertCorrectOneGroupInGroupWithOneLink(List<DataGroup> linkList) {
		assertEquals(linkList.size(), 1);

		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 4);
		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(1), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(2), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(3), "recordToRecordLink");
		// assertEquals(namesOfGroupsFactored.get(4), "from");
		// assertEquals(namesOfGroupsFactored.get(5), "to");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 3);
		assertEquals(dataAtomicFactory.usedValues.size(), 3);

		assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "nameInData", "testGroup");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "nameInData", "testGroup");
		assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "nameInData", "testLink");
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(3, "linkedRecordType",
		// "fromRecordType");
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(4, "linkedRecordId", "fromRecordId");
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(5, "linkedRecordType",
		// "someRecordType");
		// assertCorrectAtomicDataUsingIndexNameInDataAndValue(6, "linkedRecordId", "someRecordId");
	}

	@Test
	public void testOneGroupInGroupInGroupWithOneLink() {
		dataGroupRecordLinkCollectorMetadataCreator
				.addMetadataForOneGroupInGroupInGroupWithOneLink();

		DataGroup topTopDataGroup = createGroupInGroupInGroupWithOneLink();
		List<DataGroup> linkList = linkCollector.collectLinks("topTopGroup", topTopDataGroup);

		assertCorrectOneGroupInGroupInGroupWithOneLink(linkList);
	}

	private DataGroup createGroupInGroupInGroupWithOneLink() {
		DataGroup topTopDataGroup = new DataGroupOldSpy("topTopGroup");

		DataGroup topDataGroup = new DataGroupOldSpy("topGroup");
		topDataGroup.addAttributeByIdWithValue("attribute1", "attrValue");
		topDataGroup.setRepeatId("g6");
		topTopDataGroup.addChild(topDataGroup);

		DataGroup dataGroup = new DataGroupOldSpy("testGroup");
		topDataGroup.addChild(dataGroup);

		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);
		return topTopDataGroup;
	}

	private void assertCorrectOneGroupInGroupInGroupWithOneLink(List<DataGroup> linkList) {
		assertEquals(linkList.size(), 1);
		List<String> namesOfGroupsFactored = dataGroupFactory.usedNameInDatas;
		assertEquals(namesOfGroupsFactored.size(), 13);

		assertEquals(namesOfGroupsFactored.get(0), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(1), "attributes");
		assertEquals(namesOfGroupsFactored.get(2), "attribute");
		assertEquals(namesOfGroupsFactored.get(3), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(4), "attributes");
		assertEquals(namesOfGroupsFactored.get(5), "attribute");
		assertEquals(namesOfGroupsFactored.get(6), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(7), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(8), "attributes");
		assertEquals(namesOfGroupsFactored.get(9), "attribute");
		assertEquals(namesOfGroupsFactored.get(10), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(11), "linkedPath");
		assertEquals(namesOfGroupsFactored.get(12), "recordToRecordLink");
		// assertEquals(namesOfGroupsFactored.get(13), "from");
		// assertEquals(namesOfGroupsFactored.get(14), "to");

		assertEquals(dataAtomicFactory.usedNameInDatas.size(), 16);
		assertEquals(dataAtomicFactory.usedValues.size(), 16);

		// assertCorrectAtomicData();
	}

	// private void assertCorrectAtomicData() {
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(0, "attribute1", "attrValue");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(1, "nameInData", "topGroup");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(2, "attributeName", "attribute1");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(3, "attributeValue", "attrValue");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(4, "nameInData",
	// "someAtomicValueFromSpyFornameInData");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(5, "nameInData", "testGroup");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(6, "nameInData",
	// "someAtomicValueFromSpyFornameInData");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(7, "nameInData", "testLink");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(8, "linkedRecordType",
	// "fromRecordType");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(9, "linkedRecordId", "fromRecordId");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(10, "linkedRecordType",
	// "someAtomicValueFromSpyForlinkedRecordType");
	// assertCorrectAtomicDataUsingIndexNameInDataAndValue(11, "linkedRecordId",
	// "someAtomicValueFromSpyForlinkedRecordId");
	// }
}
