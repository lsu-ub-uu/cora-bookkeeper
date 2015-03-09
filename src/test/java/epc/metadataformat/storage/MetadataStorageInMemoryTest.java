package epc.metadataformat.storage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import epc.metadataformat.CoherentMetadata;
import epc.metadataformat.testdata.TestDataMetadataInMemoryStorage;

public class MetadataStorageInMemoryTest {

	@Test
	public void testInit() {
		MetadataStorageInMemory metadataMemoryGateway = new MetadataStorageInMemory();
		assertNotNull(metadataMemoryGateway.getAllMetadata());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithNull() {
		MetadataStorageInMemory.usingCoherentMetadata(null);
	}

	@Test
	public void testInitWithTestData() {
		MetadataStorage metadataStorage = TestDataMetadataInMemoryStorage
				.createMetadataStorageInMemoryContainingTestData();
		CoherentMetadata coherentMetadata = metadataStorage.getAllMetadata();
		assertNotNull(coherentMetadata, "Metadata should be present");
		assertEquals(coherentMetadata.getTextTranslation("sv", "textId"), "Testar en text");
	}

}
