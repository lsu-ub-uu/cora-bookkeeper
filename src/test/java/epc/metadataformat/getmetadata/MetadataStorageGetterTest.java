package epc.metadataformat.getmetadata;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.storage.MetadataStorageGateway;
import epc.metadataformat.testdata.TestDataMetadataInMemoryStorage;

public class MetadataStorageGetterTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithNull() throws Exception {
		new MetadataStorageGetter(null);
	}

	@Test
	public void testInitWithTestData() {

		MetadataStorageGateway metadataStorageGateway = TestDataMetadataInMemoryStorage
				.createMetadataInMemoryStorageContainingTestData();

		MetadataStorageGetterInputBoundary metadataGetter = new MetadataStorageGetter(
				metadataStorageGateway);

		Assert.assertEquals(metadataGetter.getAllMetadata(),
				metadataStorageGateway.getAllMetadata(),
				"Returned testData for should be the complete metadata");
	}

}
