package epc.metadataformat.storage;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.CoherentMetadata;
import epc.metadataformat.testdata.TestDataMetadataElement;
import epc.metadataformat.testdata.TestDataTextElement;

public class MetadataCacheTest {
	@Test
	public void testInit() {
		MetadataCache metadataCache = new MetadataCache();
		Assert.assertNotNull(metadataCache.getAllMetadata(),
				"getAllMetadata should return a CoherentMetadata object when initialized");
	}
	@Test
	public void testInitWithNull(){
		MetadataCache metadataCache = MetadataCache.usingMetadataStorage(null);
		Assert.assertNotNull(metadataCache.getAllMetadata(),
				"getAllMetadata should return a CoherentMetadata object when "
				+ "initialized with null");
		
	}

	@Test
	public void testInitLoadMetadataFromStorage() {

		CoherentMetadata coherentMetadata = CoherentMetadata.usingTextHolderAndMetadataHolder(TestDataTextElement.createTestTextElements(), TestDataMetadataElement.createTestMetadataElements());

		MetadataStorage metadataInMemoryStorage = MetadataStorageInMemory.usingCoherentMetadata(coherentMetadata);

		MetadataCache metadataCache = MetadataCache.usingMetadataStorage(metadataInMemoryStorage);

		// Map<String, String> translations = new HashMap<>();
		// translations.put("sv", "Testar en text");
		// translations.put("en", "Testing with a text");
		// TextElement textElement = new TextElement("textId", translations);
		//
		// metadataInMemoryStorage.storeText("textId", textElement);

		// Map<String, TextElement> textElements = coherentMetadata
		// .getTextElements();
		// TextElement textElementOut = textElements.get("textId");

		assertEquals(metadataCache.getTextTranslation("sv", "textId"),
				"Testar en text",
				"The translated text should be the same as the one stored");

		Assert.assertEquals(metadataCache.getTextTranslation("en", "textId"),
				"Testing with a text",
				"The translated text should be the same as the one stored");
	}
}
