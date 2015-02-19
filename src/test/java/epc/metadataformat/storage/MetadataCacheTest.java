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
		MetadataCache metadataCache = new MetadataCache(null);
		Assert.assertNotNull(metadataCache.getAllMetadata(),
				"getAllMetadata should return a CoherentMetadata object when "
				+ "initialized with null");
		
	}

	@Test
	public void testInitLoadMetadataFromStorage() {

		CoherentMetadata coherentMetadata = new CoherentMetadata(
				TestDataTextElement.createTestTextElements(),
				TestDataMetadataElement.createTestMetadataElements());

		MetadataStorageGateway metadataInMemoryStorage = new MetadataInMemoryStorage(
				coherentMetadata);

		MetadataCache metadataCache = new MetadataCache(metadataInMemoryStorage);

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
