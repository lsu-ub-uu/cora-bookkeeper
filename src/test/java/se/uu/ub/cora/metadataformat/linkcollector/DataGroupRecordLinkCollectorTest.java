package se.uu.ub.cora.metadataformat.linkcollector;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataAtomic;
import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.data.DataRecordLink;
import se.uu.ub.cora.metadataformat.metadata.DataToDataLink;
import se.uu.ub.cora.metadataformat.metadata.MetadataChildReference;
import se.uu.ub.cora.metadataformat.metadata.MetadataGroup;
import se.uu.ub.cora.metadataformat.metadata.MetadataHolder;
import se.uu.ub.cora.metadataformat.metadata.TextVariable;

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
		addMetadataForOneGroupWithNoLink();
		DataGroup dataGroup = DataGroup.withNameInData("testGroup");

		List<DataGroup> linkList = linkCollector.collectLinks("group", dataGroup);

		assertEquals(linkList.size(), 0);
	}

	private void addMetadataForOneGroupWithNoLink() {
		MetadataGroup group = MetadataGroup.withIdAndNameInDataAndTextIdAndDefTextId("group",
				"testGroup", "groupTextId", "groupDefTextId");
		metadataHolder.addMetadataElement(group);
	}

	@Test
	public void testOneGroupWithOneLink() {
		addMetadataForOneGroupWithOneLink();
		DataGroup dataGroup = DataGroup.withNameInData("testGroup");
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndRecordTypeAndRecordId(
				"testLink", "targetRecordType", "targetRecordId");
		dataGroup.addChild(dataRecordLink);

		List<DataGroup> linkList = linkCollector.collectLinks("group", dataGroup);

		assertEquals(linkList.size(), 1);
		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		DataRecordLink fromRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("from");
		assertEquals(fromRecordLink.getRecordType(), "fromRecordType");
		assertEquals(fromRecordLink.getRecordId(), "fromRecordId");

		DataRecordLink toRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("to");
		assertEquals(toRecordLink.getRecordType(), "targetRecordType");
		assertEquals(toRecordLink.getRecordId(), "targetRecordId");
	}

	private void addMetadataForOneGroupWithOneLink() {
		addMetadataForOneGroupWithNoLink();
		MetadataGroup group = (MetadataGroup) metadataHolder.getMetadataElement("group");

		DataToDataLink recordLink = DataToDataLink
				.withIdAndNameInDataAndTextIdAndDefTextIdAndTargetRecordType("link", "testLink",
						"linkTextId", "linkDefTextId", "targetRecordType");
		metadataHolder.addMetadataElement(recordLink);

		MetadataChildReference linkReference = MetadataChildReference
				.withReferenceIdAndRepeatMinAndRepeatMax("link", 1, 15);
		group.addChildReference(linkReference);

	}

	@Test
	public void testOneGroupWithOneLinkAndOtherChildren() {
		createMetadataForOneGroupWithOneLinkAndOtherChildren();
		DataGroup dataGroup = DataGroup.withNameInData("testGroup");
		DataRecordLink dataRecordLink = DataRecordLink.withNameInDataAndRecordTypeAndRecordId(
				"testLink", "targetRecordType", "targetRecordId");
		dataGroup.addChild(dataRecordLink);
		DataAtomic dataAtomic = DataAtomic.withNameInDataAndValue("textVar", "some text");
		dataGroup.addChild(dataAtomic);
		DataGroup dataSubGroup = DataGroup.withNameInData("subGroup");
		dataGroup.addChild(dataSubGroup);

		List<DataGroup> linkList = linkCollector.collectLinks("group", dataGroup);

		assertEquals(linkList.size(), 1);
		DataGroup recordToRecordLink = linkList.get(0);
		assertEquals(recordToRecordLink.getNameInData(), "recordToRecordLink");

		DataRecordLink fromRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("from");
		assertEquals(fromRecordLink.getRecordType(), "fromRecordType");
		assertEquals(fromRecordLink.getRecordId(), "fromRecordId");

		DataRecordLink toRecordLink = (DataRecordLink) recordToRecordLink
				.getFirstChildWithNameInData("to");
		assertEquals(toRecordLink.getRecordType(), "targetRecordType");
		assertEquals(toRecordLink.getRecordId(), "targetRecordId");
	}

	private void createMetadataForOneGroupWithOneLinkAndOtherChildren() {
		addMetadataForOneGroupWithOneLink();
		MetadataGroup group = (MetadataGroup) metadataHolder.getMetadataElement("group");

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
}
