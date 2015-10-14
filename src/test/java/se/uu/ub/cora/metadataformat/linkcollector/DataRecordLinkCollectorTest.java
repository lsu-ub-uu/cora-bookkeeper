package se.uu.ub.cora.metadataformat.linkcollector;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.data.DataGroup;
import se.uu.ub.cora.metadataformat.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.metadataformat.storage.MetadataStorage;
import se.uu.ub.cora.metadataformat.validator.MetadataStorageStub;

public class DataRecordLinkCollectorTest {
	private MetadataStorage metadataStorage;
	private DataRecordLinkCollector linkCollector;

	@BeforeMethod
	public void setUp() {
		metadataStorage = new MetadataStorageStub();
		linkCollector = new DataRecordLinkCollector(metadataStorage);
	}

	@Test
	public void testCollectLinksGroupWithoutLink() {
		DataGroup dataGroup = DataGroup.withNameInData("someName");
		DataGroup collectedLinks = linkCollector.collectLinks("recordType", "recordId",
				"metadataId", dataGroup);
		assertEquals(collectedLinks.getNameInData(), "collectedDataLinks");
		assertTrue(collectedLinks.getChildren().isEmpty());
	}

	@Test
	public void testCollectLinksGroupWithOneLink() {
		DataGroup dataGroup = DataGroup.withNameInData("someName");
		DataGroup collectedLinks = linkCollector.collectLinks("recordType", "recordId",
				"metadataId", dataGroup);

	}
}
