package epc.metadataformat.testdata;

import epc.metadataformat.CoherentMetadata;
import epc.metadataformat.MetadataHolder;
import epc.metadataformat.TextHolder;
import epc.metadataformat.storage.MetadataInMemoryStorage;

public class TestDataMetadataInMemoryStorage {
	public static MetadataInMemoryStorage createMetadataInMemoryStorageNoTestData() {

		TextHolder textHolder = new TextHolder();
		MetadataHolder metadataHolder = new MetadataHolder();
		CoherentMetadata coherentMetadata = new CoherentMetadata(textHolder,
				metadataHolder);

		return new MetadataInMemoryStorage(coherentMetadata);

	}

	public static MetadataInMemoryStorage createMetadataInMemoryStorageContainingTestData() {

		CoherentMetadata coherentMetadata = new CoherentMetadata(
				TestDataTextElement.createTestTextElements(),
				TestDataMetadataElement.createTestMetadataElements());

		return new MetadataInMemoryStorage(coherentMetadata);

	}
}
