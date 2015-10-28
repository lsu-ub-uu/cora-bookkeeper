package se.uu.ub.cora.bookkeeper.presentation;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

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
