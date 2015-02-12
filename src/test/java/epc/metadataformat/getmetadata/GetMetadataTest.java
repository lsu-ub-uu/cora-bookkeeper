package epc.metadataformat.getmetadata;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.storage.MetadataCache;
import epc.metadataformat.storage.MetadataStorageGateway;
import epc.metadataformat.testdata.TestDataMetadataInMemoryStorage;

public class GetMetadataTest {
	@Test
	public void testGetMetadata() {

		MetadataStorageGateway metadataStorageGateway = TestDataMetadataInMemoryStorage
				.createMetadataInMemoryStorageContainingTestData();
		MetadataCache metadataCache = new MetadataCache(metadataStorageGateway);
		
		
		GetMetadataInputBoundry metadataGetter = new GetMetadata(metadataCache);

		Assert.assertEquals(metadataGetter.getAllMetadata(), metadataStorageGateway.loadAllMetadata(),
				"Returned testData for should be the complete metadata");
	}

}
