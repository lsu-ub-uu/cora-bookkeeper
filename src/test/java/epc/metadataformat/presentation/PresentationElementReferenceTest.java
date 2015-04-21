package epc.metadataformat.presentation;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PresentationElementReferenceTest {
	private PresentationElementReference presentationElementReference;

	@BeforeMethod
	public void beforeMethod() {
		String elementRef = "elementRef";
		String presentationOf = "presentationOf";
		presentationElementReference = new PresentationElementReference(elementRef, presentationOf);
	}

	@Test
	public void testInit() {
		assertEquals(presentationElementReference.getElementRef(), "elementRef");
		assertEquals(presentationElementReference.getPresentationOf(), "presentationOf");
	}

	@Test
	public void testPresentationChildReference() {
		PresentationChildReference presentationChildReference = presentationElementReference;
		String refId = presentationChildReference.getReferenceId();
		assertEquals(refId, "elementRef");

	}

	@Test
	public void testElementRefMinimized() {
		String elementRefMinimized = "elementRefMinimized";
		presentationElementReference.setElementRefMinimized(elementRefMinimized);
		assertEquals(presentationElementReference.getElementRefMinimized(), "elementRefMinimized");
	}
}
