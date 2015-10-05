package se.uu.ub.cora.metadataformat.presentation;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.presentation.PresentationElement;
import se.uu.ub.cora.metadataformat.presentation.PresentationHolder;
import se.uu.ub.cora.metadataformat.presentation.PresentationVariable;

public class PresentationHolderTest {
	@Test
	public void testAddGetElement() {
		PresentationHolder presentationHolder = new PresentationHolder();
		PresentationElement presentationElement = new PresentationVariable("varId", "refVarId",
				PresentationVariable.Mode.INPUT);
		presentationHolder.add(presentationElement);
		assertEquals(presentationHolder.getPresentationElement("varId"), presentationElement);
	}
}
