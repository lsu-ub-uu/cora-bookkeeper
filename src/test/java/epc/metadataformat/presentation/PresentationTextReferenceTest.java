package epc.metadataformat.presentation;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PresentationTextReferenceTest {
	private PresentationTextReference presentationTextReference;

	@BeforeMethod
	public void beforeMethod() {
		String textRef = "textRef";
		presentationTextReference = new PresentationTextReference(textRef);
	}

	@Test
	public void testInit() {
		assertEquals(presentationTextReference.getTextRef(), "textRef");
	}

	@Test
	public void testPresentationChildReference() {
		PresentationChildReference presentationChildReference = presentationTextReference;
		String refId = presentationChildReference.getReferenceId();
		assertEquals(refId, "textRef");
	}
}
