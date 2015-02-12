package epc.metadataformat.testdata;

import java.util.HashMap;
import java.util.Map;

import epc.metadataformat.TextElement;

public class TestDataTextElement {
	public static Map<String, TextElement> createTestTextElements(){
		Map<String, TextElement> textElements = new HashMap<>();
		
		
		Map<String, String> translations = new HashMap<>();
		translations.put("sv", "Testar en text");
		translations.put("en", "Testing with a text");
		TextElement textElement = new TextElement("textId", translations);
		
		textElements.put("textId", textElement);
		
		
		return textElements;
	}
}
