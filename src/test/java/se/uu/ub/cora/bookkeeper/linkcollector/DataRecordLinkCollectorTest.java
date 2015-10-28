package se.uu.ub.cora.bookkeeper.linkcollector;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.data.DataRecordLink;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.validator.MetadataStorageStub;

public class DataRecordLinkCollectorTest {
	private MetadataStorage metadataStorage;
	private DataRecordLinkCollector linkCollector;

	@BeforeMethod
	public void setUp() {
		metadataStorage = new MetadataStorageStub();
		linkCollector = new DataRecordLinkCollectorImp(metadataStorage);
	}

	@Test
	public void testCollectLinksGroupWithoutLink() {
		DataGroup dataGroup = DataGroup.withNameInData("bush");
		DataGroup collectedLinks = linkCollector.collectLinks("bush", dataGroup, "recordType",
				"recordId");
		assertEquals(collectedLinks.getNameInData(), "collectedDataLinks");
		assertTrue(collectedLinks.getChildren().isEmpty());
	}

	@Test
	public void testCollectLinksGroupWithOneLink() {
		// data
		DataGroup dataGroup = DataGroup.withNameInData("bush");
		DataRecordLink dataTestLink = DataRecordLink
				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("testLink", "bush", "bush1");
		dataGroup.addChild(dataTestLink);

		DataGroup collectedLinks = linkCollector.collectLinks("bush", dataGroup, "recordType",
				"recordId");

		assertEquals(collectedLinks.getNameInData(), "collectedDataLinks");
		List<DataElement> linkList = collectedLinks.getChildren();
		assertEquals(linkList.size(), 1);

	}
}
