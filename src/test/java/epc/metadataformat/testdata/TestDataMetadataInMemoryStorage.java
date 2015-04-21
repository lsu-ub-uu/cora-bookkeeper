package epc.metadataformat.testdata;

import epc.metadataformat.CoherentMetadata;
import epc.metadataformat.MetadataHolder;
import epc.metadataformat.TextHolder;
import epc.metadataformat.presentation.PresentationHolder;
import epc.metadataformat.storage.MetadataStorageInMemory;

public class TestDataMetadataInMemoryStorage {
	public static MetadataStorageInMemory createMetadataStorageInMemoryNoTestData() {

		TextHolder textHolder = new TextHolder();
		MetadataHolder metadataHolder = new MetadataHolder();
		PresentationHolder presentationHolder = new PresentationHolder();
		CoherentMetadata coherentMetadata = CoherentMetadata.usingTextMetadataPresentationHolders(
				textHolder, metadataHolder, presentationHolder);

		return MetadataStorageInMemory.usingCoherentMetadata(coherentMetadata);

	}

	public static MetadataStorageInMemory createMetadataStorageInMemoryContainingTestData() {

		CoherentMetadata coherentMetadata = CoherentMetadata.usingTextMetadataPresentationHolders(
				TestDataTextElement.createTestTextElements(),
				TestDataMetadataElement.createTestMetadataElements(),
				TestDataPresentationElement.createTestPresentationElements());

		return MetadataStorageInMemory.usingCoherentMetadata(coherentMetadata);

	}
}
