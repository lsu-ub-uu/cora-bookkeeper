package epc.metadataformat.testdata;

import epc.metadataformat.storage.MetadataCache;
import epc.metadataformat.storage.MetadataStorageGateway;

public class TestDataMetadataCache {

	public static MetadataCache createMetadataCacheUsingInMemoryStorageContiningTestData() {
		MetadataStorageGateway metadataStorageGateway = TestDataMetadataInMemoryStorage
				.createMetadataInMemoryStorageContainingTestData();
		MetadataCache metadataCache = new MetadataCache(metadataStorageGateway);
		return metadataCache;
	}
}
