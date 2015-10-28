package se.uu.ub.cora.bookkeeper;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TextElementTest {
	@Test
	public void testInit() {
		TranslationHolder translationHolder = new TranslationHolder();
		translationHolder.addTranslation("sv", "Testar en text");
		translationHolder.addTranslation("en", "Testing with a text");
		TextElement textElement = TextElement.withIdAndTranslationHolder("textId", translationHolder);
		
		assertEquals(textElement.getId(), "textId",
				"TextId should be the same as the one set in the constructor");

		assertEquals(textElement.getTranslations(), translationHolder,
				"Translations should be the same as the one set in the constructor");
		
		assertEquals(textElement.getTranslationByLanguage("sv"),
				"Testar en text", "The fetched translated text is not correct");
	}
}
