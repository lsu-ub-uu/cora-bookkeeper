package epc.metadataformat.testdata;

import epc.metadataformat.CoherentMetadata;
import epc.metadataformat.MetadataHolder;
import epc.metadataformat.TextHolder;
import epc.metadataformat.storage.MetadataStorageInMemory;

public class TestDataMetadataInMemoryStorage {
	public static MetadataStorageInMemory createMetadataStorageInMemoryNoTestData() {

		TextHolder textHolder = new TextHolder();
		MetadataHolder metadataHolder = new MetadataHolder();
		CoherentMetadata coherentMetadata = CoherentMetadata.usingTextHolderAndMetadataHolder(textHolder, metadataHolder);

		return MetadataStorageInMemory.usingCoherentMetadata(coherentMetadata);

	}

	public static MetadataStorageInMemory createMetadataStorageInMemoryContainingTestData() {

		CoherentMetadata coherentMetadata = CoherentMetadata.usingTextHolderAndMetadataHolder(TestDataTextElement.createTestTextElements(), TestDataMetadataElement.createTestMetadataElements());

		return MetadataStorageInMemory.usingCoherentMetadata(coherentMetadata);

	}
}
