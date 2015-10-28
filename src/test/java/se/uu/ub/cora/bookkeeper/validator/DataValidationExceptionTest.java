package se.uu.ub.cora.bookkeeper.validator;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class DataValidationExceptionTest {
	@Test
	public void testInit() {
		String message = "message";
		DataValidationException exception = DataValidationException.withMessage(message);
		assertEquals(exception.getMessage(), "message");
	}
}
