package epc.metadataformat;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TextHolderTest {
	@Test
	public void testInit() {
		TextHolder textHolder = new TextHolder();

		TranslationHolder translationHolder = new TranslationHolder();
		translationHolder.addTranslation("sv", "En text på svenska");
		translationHolder.addTranslation("en", "A text in english");

		TextElement textElement = new TextElement("textId", translationHolder);

		textHolder.addTextElement(textElement);

		TextElement textElementOut = textHolder.getTextElement("textId");

		Assert.assertEquals(textElementOut, textElement,
				"The element should be the same as the one entered");

	}
}
