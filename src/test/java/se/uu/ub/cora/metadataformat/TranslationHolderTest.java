package se.uu.ub.cora.metadataformat;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.TranslationHolder;

public class TranslationHolderTest {
	@Test
	public void testInti(){
		TranslationHolder translationHolder = new TranslationHolder();
		translationHolder.addTranslation("sv","En text på svenska");
		
		String translationOut = translationHolder.getTranslation("sv");
		
		Assert.assertEquals(translationOut, "En text på svenska",
				"The returned text should be the same as the one entered");
	}
}
