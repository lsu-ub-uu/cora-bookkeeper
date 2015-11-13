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

package se.uu.ub.cora.bookkeeper.linkcollector;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.data.DataRecordLink;
import se.uu.ub.cora.bookkeeper.metadata.CollectionVariableChild;
import se.uu.ub.cora.bookkeeper.metadata.MetadataChildReference;
import se.uu.ub.cora.bookkeeper.metadata.MetadataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataHolder;
import se.uu.ub.cora.bookkeeper.metadata.RecordLink;
import se.uu.ub.cora.bookkeeper.metadata.TextVariable;

public class DataGroupRecordLinkCollectorTest {
	private MetadataHolder metadataHolder;
	private DataGroupRecordLinkCollector linkCollector;

	@BeforeMethod
	public void setUp() {
		metadataHolder = new MetadataHolder();
		linkCollector = new DataGroupRecordLinkCollector(metadataHolder, "fromRecordType",
				"fromRecordId");
	}

	@Test
	public void testOneGroupWithNoLink() {
		addMetadataForOneGroupWithNoLink("test");
		DataGroup dataGroup = DataGroup.withNameInData("testGroup");

		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);

		assertEquals(linkList.size(), 0);
	}

	private void addMetadataForOneGroupWithNoLink(String id) {
		metadataHolder
				.addMetadataElement(addMetadataForOneGroupWithNoLinkUsingIdAndNameInData(id, id));
	}

	private MetadataGroup addMetadataForOneGroupWithNoLinkUsingIdAndNameInData(String id,
			String nameInData) {
		MetadataGroup group = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId(id + "Group",
				nameInData + "Group", id + "GroupTextId", id + "GroupDefTextId");
		return group;
	}

	@Test
	public void testOneGroupWithOneLink() {
		addMetadataForOneGroupWithOneLink("test");
		DataGroup dataGroup = DataGroup.withNameInData("testGroup");
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("testLink", "linkedRecordType",
						"linkedRecordId");
		dataGroup.addChild(dataRecordLink);

		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);

		assertEquals(linkList.size(), 1);
		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		DataRecordLink fromRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("from");
		assertEquals(fromRecordLink.getLinkedRecordType(), "fromRecordType");
		assertEquals(fromRecordLink.getLinkedRecordId(), "fromRecordId");

		DataRecordLink toRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("to");
		assertEquals(toRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(toRecordLink.getLinkedRecordId(), "linkedRecordId");
	}

	private void addMetadataForOneGroupWithOneLink(String id) {
		addMetadataForOneGroupWithNoLink(id);

		RecordLink recordLink = RecordLink
				.withIdAndNameInDataAndTextIdAndDefTextIdAndLinkedRecordType(id + "Link",
						id + "Link", id + "LinkTextId", id + "LinkDefTextId", "linkedRecordType");
		metadataHolder.addMetadataElement(recordLink);

		addChildReferenceParentIdChildIdMinMax(id + "Group", id + "Link", 1, 15);
	}

	@Test
	public void testOneGroupWithOneLinkAndOtherChildren() {
		createMetadataForOneGroupWithOneLinkAndOtherChildren();
		DataGroup dataGroup = DataGroup.withNameInData("testGroup");
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("testLink", "linkedRecordType",
						"linkedRecordId");
		dataGroup.addChild(dataRecordLink);
		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("textVar", "some text");
		dataGroup.addChild(dataAtomic);
		DataGroup dataSubGroup = DataGroup.withNameInData("subGroup");
		dataGroup.addChild(dataSubGroup);

		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);

		assertEquals(linkList.size(), 1);
		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		DataRecordLink fromRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("from");
		assertEquals(fromRecordLink.getLinkedRecordType(), "fromRecordType");
		assertEquals(fromRecordLink.getLinkedRecordId(), "fromRecordId");

		DataRecordLink toRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("to");
		assertEquals(toRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(toRecordLink.getLinkedRecordId(), "linkedRecordId");
		Assert.assertNull(toRecordLink.getLinkedPath());
		Assert.assertNull(toRecordLink.getLinkedRepeatId());
	}

	private void createMetadataForOneGroupWithOneLinkAndOtherChildren() {
		addMetadataForOneGroupWithOneLink("test");
		MetadataGroup group = (MetadataGroup) metadataHolder.getMetadataElement("testGroup");

		TextVariable textVar = TextVariable
				.withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("textVar",
						"textVarNameInData", "textVarTextId", "textVarDefTextId", ".*");
		metadataHolder.addMetadataElement(textVar);

		MetadataChildReference textVarReference = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("textVar", 1, 15);
		group.addChildReference(textVarReference);

		MetadataGroup subGroup = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("subGroup",
				"subGroup", "subGroupTextId", "subGroupDefTextId");
		metadataHolder.addMetadataElement(subGroup);
		MetadataChildReference subGroupReference = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("subGroup", 1, 15);
		group.addChildReference(subGroupReference);
	}

	@Test
	public void testOneGroupWithOneLinkWithPath() {
		addMetadataForOneGroupWithOneLinkWithPath();
		DataGroup dataGroup = DataGroup.withNameInData("testGroup");
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("testLink", "linkedRecordType",
						"linkedRecordId");
		dataGroup.addChild(dataRecordLink);
		dataRecordLink.setLinkedRepeatId("e3");
		dataRecordLink.setRepeatId("i2");

		List<DataGroup> linkList = linkCollector.collectLinks("testGroup", dataGroup);

		assertEquals(linkList.size(), 1);
		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		DataRecordLink fromRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("from");
		assertEquals(fromRecordLink.getLinkedRecordType(), "fromRecordType");
		assertEquals(fromRecordLink.getLinkedRecordId(), "fromRecordId");
		String fromLinkedRepeatId = fromRecordLink.getLinkedRepeatId();
		assertEquals(fromLinkedRepeatId, "i2");

		DataRecordLink toRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("to");
		assertEquals(toRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(toRecordLink.getLinkedRecordId(), "linkedRecordId");
		DataGroup linkedPath = toRecordLink.getLinkedPath();
		assertNotNull(linkedPath);
		assertEquals(linkedPath.getNameInData(), "linkedPath");
		assertEquals(linkedPath.getFirstAtomicValueWithNameInData("nameInData"), "someNameInData");
		String linkedRepeatId = toRecordLink.getLinkedRepeatId();
		assertNotNull(linkedRepeatId);
		assertEquals(linkedRepeatId, "e3");

	}

	private void addMetadataForOneGroupWithOneLinkWithPath() {
		addMetadataForOneGroupWithOneLink("test");

		RecordLink recordLink = (RecordLink) metadataHolder.getMetadataElement("testLink");

		DataGroup linkedPath = DataGroup.withNameInData("linkedPath");
		recordLink.setLinkedPath(linkedPath);
		linkedPath.addChild(DataAtomic.withNameInDataAndValue("nameInData", "someNameInData"));
	}

	@Test
	public void testOneGroupInGroupWithOneLink() {
		addMetadataForOneGroupInGroupWithOneLink();
		DataGroup topDataGroup = DataGroup.withNameInData("topGroup");
		DataGroup dataGroup = DataGroup.withNameInData("testGroup");
		topDataGroup.addChild(dataGroup);
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("testLink", "linkedRecordType",
						"linkedRecordId");
		dataGroup.addChild(dataRecordLink);

		List<DataGroup> linkList = linkCollector.collectLinks("topGroup", topDataGroup);

		assertEquals(linkList.size(), 1);
		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		DataRecordLink fromRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("from");
		assertEquals(fromRecordLink.getLinkedRecordType(), "fromRecordType");
		assertEquals(fromRecordLink.getLinkedRecordId(), "fromRecordId");
		DataGroup fromLinkedPath = fromRecordLink.getLinkedPath();
		assertNotNull(fromLinkedPath);
		assertFalse(fromLinkedPath.containsChildWithNameInData("repeatId"));
		assertEquals(fromLinkedPath.getFirstAtomicValueWithNameInData("nameInData"), "testGroup");
		assertFalse(fromLinkedPath.containsChildWithNameInData("attributes"));
		assertTrue(fromLinkedPath.containsChildWithNameInData("linkedPath"));

		DataGroup fromLinkedPathSub1 = fromLinkedPath.getFirstGroupWithNameInData("linkedPath");
		assertFalse(fromLinkedPathSub1.containsChildWithNameInData("repeatId"));
		assertEquals(fromLinkedPathSub1.getFirstAtomicValueWithNameInData("nameInData"),
				"testLink");
		assertFalse(fromLinkedPathSub1.containsChildWithNameInData("attributes"));
		assertFalse(fromLinkedPathSub1.containsChildWithNameInData("linkedPath"));

		DataRecordLink toRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("to");
		assertEquals(toRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(toRecordLink.getLinkedRecordId(), "linkedRecordId");
	}

	private void addMetadataForOneGroupInGroupWithOneLink() {
		addMetadataForOneGroupWithNoLink("top");
		addMetadataForOneGroupWithOneLink("test");

		addChildReferenceParentIdChildIdMinMax("topGroup", "testGroup", 1, 1);
	}

	private void addChildReferenceParentIdChildIdMinMax(String from, String to, int min, int max) {
		MetadataGroup topGroup = (MetadataGroup) metadataHolder.getMetadataElement(from);

		MetadataChildReference reference = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax(to, min, max);
		topGroup.addChildReference(reference);
	}

	@Test
	public void testOneGroupInGroupInGroupWithOneLink() {
		addMetadataForOneGroupInGroupInGroupWithOneLink();

		DataGroup toptopDataGroup = DataGroup.withNameInData("toptopGroup");
		DataGroup topDataGroup = DataGroup.withNameInData("topGroup");
		topDataGroup.addAttributeByIdWithValue("attribute1", "attrValue");
		topDataGroup.setRepeatId("g6");
		toptopDataGroup.addChild(topDataGroup);
		DataGroup dataGroup = DataGroup.withNameInData("testGroup");
		topDataGroup.addChild(dataGroup);
		DataRecordLink dataRecordLink = DataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("testLink", "linkedRecordType",
						"linkedRecordId");
		dataGroup.addChild(dataRecordLink);

		List<DataGroup> linkList = linkCollector.collectLinks("toptopGroup", toptopDataGroup);

		assertEquals(linkList.size(), 1);
		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		DataRecordLink fromRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("from");
		assertEquals(fromRecordLink.getLinkedRecordType(), "fromRecordType");
		assertEquals(fromRecordLink.getLinkedRecordId(), "fromRecordId");
		DataGroup fromLinkedPath = fromRecordLink.getLinkedPath();
		assertNotNull(fromLinkedPath);
		assertTrue(fromLinkedPath.containsChildWithNameInData("repeatId"));
		assertEquals(fromLinkedPath.getFirstAtomicValueWithNameInData("nameInData"), "topGroup");
		assertTrue(fromLinkedPath.containsChildWithNameInData("attributes"));
		DataGroup attributes = fromLinkedPath.getFirstGroupWithNameInData("attributes");
		DataGroup attribute = attributes.getFirstGroupWithNameInData("attribute");
		assertEquals(attribute.getFirstAtomicValueWithNameInData("attributeName"), "attribute1");
		assertEquals(attribute.getFirstAtomicValueWithNameInData("attributeValue"), "attrValue");
		assertTrue(fromLinkedPath.containsChildWithNameInData("linkedPath"));

		DataGroup fromLinkedPathSub1 = fromLinkedPath.getFirstGroupWithNameInData("linkedPath");
		assertFalse(fromLinkedPathSub1.containsChildWithNameInData("repeatId"));
		assertEquals(fromLinkedPathSub1.getFirstAtomicValueWithNameInData("nameInData"),
				"testGroup");
		assertFalse(fromLinkedPathSub1.containsChildWithNameInData("attributes"));
		assertTrue(fromLinkedPathSub1.containsChildWithNameInData("linkedPath"));

		DataGroup fromLinkedPathSub2 = fromLinkedPathSub1.getFirstGroupWithNameInData("linkedPath");
		assertFalse(fromLinkedPathSub2.containsChildWithNameInData("repeatId"));
		assertEquals(fromLinkedPathSub2.getFirstAtomicValueWithNameInData("nameInData"),
				"testLink");
		assertFalse(fromLinkedPathSub2.containsChildWithNameInData("attributes"));
		assertFalse(fromLinkedPathSub2.containsChildWithNameInData("linkedPath"));

		DataRecordLink toRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("to");
		assertEquals(toRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(toRecordLink.getLinkedRecordId(), "linkedRecordId");
	}

	private void addMetadataForOneGroupInGroupInGroupWithOneLink() {
		addMetadataForOneGroupWithNoLink("top");
		addMetadataForOneGroupWithOneLink("test");

		addChildReferenceParentIdChildIdMinMax("topGroup", "testGroup", 1, 1);

		addMetadataForOneGroupWithNoLink("toptop");
		addChildReferenceParentIdChildIdMinMax("toptopGroup", "topGroup", 1, 2);

		// TextVariable attribute1 = TextVariable
		// .withIdAndNameInDataAndTextIdAndDefTextIdAndRegularExpression("attribute1",
		// "attribute1", "textId", "defTextId", "attrValue");

		// metadataHolder.addMetadataElement(attribute1);

		CollectionVariableChild collectionVariableChild = new CollectionVariableChild("attribute1",
				"attribute1", "textId", "defTextId", "itemCollectionId", "collectionVariableId");
		metadataHolder.addMetadataElement(collectionVariableChild);

		collectionVariableChild.setFinalValue("attrValue");

		MetadataGroup toptopGroup = (MetadataGroup) metadataHolder.getMetadataElement("topGroup");
		toptopGroup.addAttributeReference("attribute1");

	}

}
