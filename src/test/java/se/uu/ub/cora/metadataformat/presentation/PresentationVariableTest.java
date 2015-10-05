package se.uu.ub.cora.metadataformat.presentation;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.presentation.PresentationElement;
import se.uu.ub.cora.metadataformat.presentation.PresentationVariable;

public class PresentationVariableTest {
	private PresentationVariable presentationVariable;

	@BeforeMethod
	public void beforeMethod() {
		String id = "presentationVariableId";
		String refVarId = "presentationRefVarId";
		presentationVariable = new PresentationVariable(id, refVarId,
				PresentationVariable.Mode.INPUT);
	}

	@Test
	public void testInitInput() {
		assertEquals(presentationVariable.getId(), "presentationVariableId");
		assertEquals(presentationVariable.getRefVarId(), "presentationRefVarId");
		assertEquals(presentationVariable.getMode(), PresentationVariable.Mode.INPUT);
		// small hack to get 100% coverage on enum
		PresentationVariable.Mode.valueOf(PresentationVariable.Mode.INPUT.toString());
	}

	@Test
	public void testModeInput() {
		PresentationVariable.Mode mode = PresentationVariable.Mode.INPUT;
		assertEquals(mode.getValue(), "input");
	}

	@Test
	public void testModeOutput() {
		PresentationVariable.Mode mode = PresentationVariable.Mode.OUTPUT;
		assertEquals(mode.getValue(), "output");
	}

	@Test
	public void testPresentationElement() {
		PresentationElement presentationElement = presentationVariable;
		assertEquals(presentationElement.getId(), "presentationVariableId");
	}
}
