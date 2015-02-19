package epc.metadataformat.getmetadata;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.storage.MetadataStorageGateway;
import epc.metadataformat.testdata.TestDataMetadataInMemoryStorage;

public class GetMetadataTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithNull() throws Exception {
		new GetMetadata(null);
	}

	@Test
	public void testInitWithTestData() {

		MetadataStorageGateway metadataStorageGateway = TestDataMetadataInMemoryStorage
				.createMetadataInMemoryStorageContainingTestData();

		GetMetadataInputBoundry metadataGetter = new GetMetadata(
				metadataStorageGateway);

		Assert.assertEquals(metadataGetter.getAllMetadata(),
				metadataStorageGateway.getAllMetadata(),
				"Returned testData for should be the complete metadata");
	}

}
