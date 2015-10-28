package se.uu.ub.cora.bookkeeper;

import org.testng.Assert;
import org.testng.annotations.Test;

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
