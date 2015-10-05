package se.uu.ub.cora.metadataformat;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.TextElement;
import se.uu.ub.cora.metadataformat.TextHolder;
import se.uu.ub.cora.metadataformat.TranslationHolder;

public class TextHolderTest {
	@Test
	public void testInit() {
		TextHolder textHolder = new TextHolder();

		TranslationHolder translationHolder = new TranslationHolder();
		translationHolder.addTranslation("sv", "En text på svenska");
		translationHolder.addTranslation("en", "A text in english");

		TextElement textElement = TextElement.withIdAndTranslationHolder("textId", translationHolder);

		textHolder.addTextElement(textElement);

		TextElement textElementOut = textHolder.getTextElement("textId");

		Assert.assertEquals(textElementOut, textElement,
				"The element should be the same as the one entered");

	}
}
