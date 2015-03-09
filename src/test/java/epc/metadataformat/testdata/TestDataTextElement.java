package epc.metadataformat.testdata;

import epc.metadataformat.TextElement;
import epc.metadataformat.TextHolder;
import epc.metadataformat.TranslationHolder;

public class TestDataTextElement {
	public static TextHolder createTestTextElements(){
		TextHolder textHolder = new TextHolder();
		
		
		TranslationHolder translationHolder = new TranslationHolder();
		translationHolder.addTranslation("sv", "Testar en text");
		translationHolder.addTranslation("en", "Testing with a text");
		TextElement textElement = TextElement.withIdAndTranslationHolder("textId", translationHolder);
		
		textHolder.addTextElement(textElement);
		
		
		return textHolder;
	}
}
