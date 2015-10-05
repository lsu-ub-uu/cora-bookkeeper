package se.uu.ub.cora.metadataformat.validator;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.metadataformat.validator.DataValidationException;

public class DataValidationExceptionTest {
	@Test
	public void testInit() {
		String message = "message";
		DataValidationException exception = DataValidationException.withMessage(message);
		assertEquals(exception.getMessage(), "message");
	}
}
