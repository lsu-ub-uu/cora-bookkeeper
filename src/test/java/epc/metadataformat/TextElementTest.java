package epc.metadataformat;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

public class TextElementTest {
	@Test
	public void testInit() {
		Map<String, String> translations = new HashMap<>();
		translations.put("sv", "Testar en text");
		translations.put("en", "Testing with a text");
		TextElement textElement = new TextElement("textId", translations);
		
		assertEquals(textElement.getId(), "textId",
				"TextId should be the same as the one set in the constructor");

		assertEquals(textElement.getTranslations(), translations,
				"Translations should be the same as the one set in the constructor");
		
		assertEquals(textElement.getTranslationByLanguage("sv"),
				"Testar en text", "The fetched translated text is not correct");
	}
}
