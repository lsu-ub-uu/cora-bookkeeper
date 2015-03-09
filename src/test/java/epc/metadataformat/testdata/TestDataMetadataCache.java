package epc.metadataformat.testdata;

import epc.metadataformat.storage.MetadataCache;
import epc.metadataformat.storage.MetadataStorage;

public class TestDataMetadataCache {

	public static MetadataCache createMetadataCacheUsingInMemoryStorageContiningTestData() {
		MetadataStorage metadataStorage = TestDataMetadataInMemoryStorage
				.createMetadataStorageInMemoryContainingTestData();
		MetadataCache metadataCache = MetadataCache.usingMetadataStorage(metadataStorage);
		return metadataCache;
	}
}
