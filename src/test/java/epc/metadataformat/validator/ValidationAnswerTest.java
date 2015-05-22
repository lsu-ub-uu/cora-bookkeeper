package epc.metadataformat.validator;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.testng.annotations.Test;

/**
 * ValidationAnswerTest is a test class for ValidationAnswer
 * 
 * @author olov
 * 
 */
public class ValidationAnswerTest {

	@Test
	public void testInit() {
		ValidationAnswer answer = new ValidationAnswer();
		assertEquals(answer.dataIsValid(), true, "A new ValidationAnswer is valid");
		assertEquals(answer.dataIsInvalid(), false, "A new ValidationAnswer is valid");
	}

	@Test
	public void testAddErrorMessage() {
		ValidationAnswer answer = new ValidationAnswer();
		answer.addErrorMessage("Test error");
		assertEquals(answer.dataIsValid(), false,
				"ValidationAnswer with at least one error message is not valid");
		assertEquals(answer.dataIsInvalid(), true,
				"ValidationAnswer with at least one error message is invalid");
	}

	@Test
	public void testAddErrorMessageAndError() {
		ValidationAnswer answer = new ValidationAnswer();
		Exception exception = new Exception("message");
		answer.addErrorMessageAndAppendErrorMessageFromExceptionToMessage("Test error", exception);
		assertEquals(answer.dataIsValid(), false,
				"ValidationAnswer with at least one error message is not valid");
		assertEquals(answer.dataIsInvalid(), true,
				"ValidationAnswer with at least one error message is invalid");
		assertEquals(answer.getErrorMessages().iterator().next(), "Test error message");
	}

	@Test
	public void testAddErrorMessages() {
		Collection<String> errorMessages = new ArrayList<>();
		errorMessages.add("Test error");
		errorMessages.add("Test error2");
		ValidationAnswer answer = new ValidationAnswer();
		answer.addErrorMessages(errorMessages);
		Collection<String> errorMessagesOut = answer.getErrorMessages();
		Iterator<String> errorMessagesOutIterator = errorMessagesOut.iterator();
		for (String errorMessage : errorMessages) {
			assertEquals(errorMessagesOutIterator.next(), errorMessage);
		}
	}

	@Test
	public void testGetErrorMessages() {
		ValidationAnswer answer = new ValidationAnswer();
		answer.addErrorMessage("Test error");
		Collection<String> errorMessages = answer.getErrorMessages();

		assertEquals(errorMessages.iterator().next(), "Test error");
	}
}
