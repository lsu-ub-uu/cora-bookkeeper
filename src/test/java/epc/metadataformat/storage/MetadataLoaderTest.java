package epc.metadataformat.storage;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.MetadataHolder;
import epc.metadataformat.TextElement;
import epc.metadataformat.storage.MetadataLoader;
import epc.metadataformat.storage.MetadataStorageGateway;
import epc.metadataformat.storage.MetadataStorageInMemory;

public class MetadataLoaderTest {
	@Test
	public void testLoadMetadataFromStorage() {
		MetadataStorageGateway metadataStorageGateway = new MetadataStorageInMemory();

		Map<String, String> translations = new HashMap<>();
		translations.put("sv", "Testar en text");
		translations.put("en", "Testing with a text");
		TextElement textElement = new TextElement("textId", translations);

		metadataStorageGateway.storeText("textId", textElement);

		MetadataLoader metadataLoader = new MetadataLoader(
				metadataStorageGateway);

		MetadataHolder metadataHolder = metadataLoader.loadAllMetadata();

		Map<String, TextElement> textElements = metadataHolder
				.getTextElements();
		TextElement textElementOut = textElements.get("textId");

		Assert.assertEquals(textElementOut.getTranslationByLanguage("sv"),
				"Testar en text",
				"The translated text should be the same as the one stored");

		Assert.assertEquals(textElementOut.getTranslationByLanguage("en"),
				"Testing with a text",
				"The translated text should be the same as the one stored");

	}
}
