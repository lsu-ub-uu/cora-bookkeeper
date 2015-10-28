package se.uu.ub.cora.bookkeeper.testdata;

import se.uu.ub.cora.bookkeeper.TextElement;
import se.uu.ub.cora.bookkeeper.TextHolder;
import se.uu.ub.cora.bookkeeper.TranslationHolder;

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
