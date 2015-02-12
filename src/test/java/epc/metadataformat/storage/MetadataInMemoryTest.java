package epc.metadataformat.storage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import epc.metadataformat.CoherentMetadata;
import epc.metadataformat.testdata.TestDataMetadataInMemoryStorage;

public class MetadataInMemoryTest {
	@Test
	public void testInit() {

		MetadataStorageGateway metadataStorageGateway = TestDataMetadataInMemoryStorage
				.createMetadataInMemoryStorageContainingTestData();
		CoherentMetadata coherentMetadata = metadataStorageGateway
				.loadAllMetadata();
		assertNotNull(coherentMetadata, "Metadata should be present");
		assertEquals(coherentMetadata.getTextTranslation("sv", "textId"),
				"Testar en text");

	}

}
