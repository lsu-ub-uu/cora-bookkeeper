/**Copyright 2015,2019 Uppsala University Library**This file is part of Cora.**Cora is free software:you can redistribute it and/or modify*it under the terms of the GNU General Public License as published by*the Free Software Foundation,either version 3 of the License,or*(at your option)any later version.**Cora is distributed in the hope that it will be useful,*but WITHOUT ANY WARRANTY;without even the implied warranty of*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the*GNU General Public License for more details.**You should have received a copy of the GNU General Public License*along with Cora.If not,see<http://www.gnu.org/licenses/>.
*/

package se.uu.ub.cora.bookkeeper.linkcollector;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.DataAtomicOldSpy;
import se.uu.ub.cora.bookkeeper.DataGroupOldSpy;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.collected.Link;
import se.uu.ub.cora.data.spies.DataFactorySpy;

public class DataGroupRecordLinkCollectorTest {
	private DataGroupRecordLinkCollector linkCollector;
	private DataGroupRecordLinkCollectorMetadataCreator dataGroupRecordLinkCollectorMetadataCreator;

	private DataFactorySpy dataFactorySpy;
	// private DataGroupFactorySpy dataGroupFactory;
	// private DataAtomicFactorySpy dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		dataGroupRecordLinkCollectorMetadataCreator = new DataGroupRecordLinkCollectorMetadataCreator();
		linkCollector = new DataGroupRecordLinkCollector(
				dataGroupRecordLinkCollectorMetadataCreator.getMetadataHolder());
		// dataAtomicFactory = new DataAtomicFactorySpy();
		// DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
	}

	@Test
	public void testOneGroupWithNoLink() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupWithNoLink("test");
		DataGroup dataGroup = new DataGroupOldSpy("testGroup");

		Set<Link> linkSet = linkCollector.collectLinks("testGroup", dataGroup);
		assertEquals(linkSet.size(), 0);
	}

	@Test
	public void testOneGroupWithOneLink() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupWithOneLink("test");
		DataGroup dataGroup = createDataGroupWithOneLink();

		Set<Link> linkSet = linkCollector.collectLinks("testGroup", dataGroup);

		assertEquals(linkSet.size(), 1);
		Link link = (Link) linkSet.toArray()[0];
		assertEquals(link.type(), "someRecordType");
		assertEquals(link.id(), "someRecordId");
	}

	private DataGroup createDataGroupWithOneLink() {
		DataGroup dataGroup = new DataGroupOldSpy("testGroup");
		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);
		return dataGroup;
	}

	private DataGroup createTestLinkWithRecordTypeAndRecordId() {
		DataGroup dataRecordLink = new DataGroupOldSpy("testLink");
		DataAtomic linkedRecordType = new DataAtomicOldSpy("linkedRecordType", "someRecordType");
		dataRecordLink.addChild(linkedRecordType);
		DataAtomic linkedRecordId = new DataAtomicOldSpy("linkedRecordId", "someRecordId");
		dataRecordLink.addChild(linkedRecordId);
		return dataRecordLink;
	}

	@Test
	public void testOneGroupWithOneLinkAndOtherChildren() {
		dataGroupRecordLinkCollectorMetadataCreator
				.createMetadataForOneGroupWithOneLinkAndOtherChildren();
		DataGroup dataGroup = createDataGroupWithOneLink();
		addOtherChild(dataGroup);

		Set<Link> linkSet = linkCollector.collectLinks("testGroup", dataGroup);

		assertEquals(linkSet.size(), 1);
		Link link = (Link) linkSet.toArray()[0];
		assertEquals(link.type(), "someRecordType");
		assertEquals(link.id(), "someRecordId");
	}

	private void addOtherChild(DataGroup dataGroup) {
		DataAtomic dataAtomic = new DataAtomicOldSpy("textVar", "some text");
		dataGroup.addChild(dataAtomic);
		DataGroup dataSubGroup = new DataGroupOldSpy("subGroup");
		dataGroup.addChild(dataSubGroup);
	}

	@Test
	public void testOneGroupWithOneLinkWithPathAndRepeatId() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupWithOneLinkWithPath();
		DataGroup dataGroup = createDataGroupContainingLinkWithRepeatId();

		Set<Link> linkSet = linkCollector.collectLinks("testGroup", dataGroup);

		assertEquals(linkSet.size(), 1);
		Link link = (Link) linkSet.toArray()[0];
		assertEquals(link.type(), "someRecordType");
		assertEquals(link.id(), "someRecordId");
	}

	private DataGroup createDataGroupContainingLinkWithRepeatId() {
		DataGroup dataGroup = new DataGroupOldSpy("testGroup");

		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);

		DataAtomic linkedRepeatId = new DataAtomicOldSpy("linkedRepeatId", "e3");
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

		DataAtomic linkedRepeatId = new DataAtomicOldSpy("linkedRepeatId", "someLinkedRepeatId");
		dataRecordLink.addChild(linkedRepeatId);
		dataRecordLink.setRepeatId("");

		Set<Link> linkSet = linkCollector.collectLinks("testGroup", dataGroup);
		assertEquals(linkSet.size(), 1);
		Link link = (Link) linkSet.toArray()[0];
		assertEquals(link.type(), "someRecordType");
		assertEquals(link.id(), "someRecordId");

	}

	@Test
	public void testOneGroupInGroupWithOneLink() {
		dataGroupRecordLinkCollectorMetadataCreator.addMetadataForOneGroupInGroupWithOneLink();
		DataGroup topDataGroup = createGroupInGroupWithOneLink();

		Set<Link> linkSet = linkCollector.collectLinks("topGroup", topDataGroup);

		assertEquals(linkSet.size(), 1);
		Link link = (Link) linkSet.toArray()[0];
		assertEquals(link.type(), "someRecordType");
		assertEquals(link.id(), "someRecordId");
	}

	private DataGroup createGroupInGroupWithOneLink() {
		DataGroup topDataGroup = new DataGroupOldSpy("topGroup");
		DataGroup dataGroup = new DataGroupOldSpy("testGroup");
		topDataGroup.addChild(dataGroup);

		DataGroup dataRecordLink = createTestLinkWithRecordTypeAndRecordId();
		dataGroup.addChild(dataRecordLink);
		return topDataGroup;
	}

	@Test
	public void testOneGroupInGroupInGroupWithOneLink() {
		se.uu.ub.cora.data.spies.DataAtomicSpy dataAtomicSpy = new se.uu.ub.cora.data.spies.DataAtomicSpy();
		dataAtomicSpy.MRV.setDefaultReturnValuesSupplier("getValue", () -> "attrValue");
		dataFactorySpy.MRV.setDefaultReturnValuesSupplier("factorAtomicUsingNameInDataAndValue",
				() -> dataAtomicSpy);
		dataGroupRecordLinkCollectorMetadataCreator
				.addMetadataForOneGroupInGroupInGroupWithOneLink();

		DataGroup topTopDataGroup = createGroupInGroupInGroupWithOneLink();
		Set<Link> linkSet = linkCollector.collectLinks("topTopGroup", topTopDataGroup);

		dataFactorySpy.MCR.assertNumberOfCallsToMethod("factorAtomicUsingNameInDataAndValue", 1);

		assertEquals(linkSet.size(), 1);
		Link link = (Link) linkSet.toArray()[0];
		assertEquals(link.type(), "someRecordType");
		assertEquals(link.id(), "someRecordId");
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

}
