package epc.metadataformat.getmetadata;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.storage.MetadataStorage;
import epc.metadataformat.testdata.TestDataMetadataInMemoryStorage;

public class MetadataGetterTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInitWithNull() throws Exception {
		MetadataGetterImp.usingMetadataStorage(null);
	}

	@Test
	public void testInitWithTestData() {

		MetadataStorage metadataStorage = TestDataMetadataInMemoryStorage
				.createMetadataStorageInMemoryContainingTestData();

		MetadataGetter metadataGetter = MetadataGetterImp.usingMetadataStorage(metadataStorage);

		Assert.assertEquals(metadataGetter.getAllMetadata(),
				metadataStorage.getAllMetadata(),
				"Returned testData for should be the complete metadata");
	}

}
