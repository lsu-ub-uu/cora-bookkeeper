package se.uu.ub.cora.metadataformat.testdata;

import se.uu.ub.cora.metadataformat.TextElement;
import se.uu.ub.cora.metadataformat.TextHolder;
import se.uu.ub.cora.metadataformat.TranslationHolder;

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
