package epc.metadataformat.testdata;

import java.util.HashMap;
import java.util.Map;

import epc.metadataformat.CoherentMetadata;
import epc.metadataformat.MetadataElement;
import epc.metadataformat.TextElement;
import epc.metadataformat.storage.MetadataInMemoryStorage;

public class TestDataMetadataInMemoryStorage {
	public static MetadataInMemoryStorage createMetadataInMemoryStorageNoTestData() {

		Map<String, TextElement> textElements = new HashMap<>();
		Map<String, MetadataElement> metadataElements = new HashMap<>();
		CoherentMetadata coherentMetadata = new CoherentMetadata(textElements ,
				metadataElements );

		return new MetadataInMemoryStorage(coherentMetadata);

	}

	public static MetadataInMemoryStorage createMetadataInMemoryStorageContainingTestData() {

		CoherentMetadata coherentMetadata = new CoherentMetadata(
				TestDataTextElement.createTestTextElements(),
				TestDataMetadataElement.createTestMetadataElements());

		return new MetadataInMemoryStorage(coherentMetadata);

	}
}
