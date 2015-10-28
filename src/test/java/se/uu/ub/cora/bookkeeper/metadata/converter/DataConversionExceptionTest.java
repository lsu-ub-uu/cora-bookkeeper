package se.uu.ub.cora.bookkeeper.metadata.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class DataConversionExceptionTest {
	@Test
	public void testInitWithMessage() {
		String message = "message";
		DataConversionException dataConversionException = DataConversionException
				.withMessage(message);
		assertEquals(dataConversionException.getMessage(), "message");
	}

	@Test
	public void testInitWithMessageAndExcepiton() {
		String message = "message";
		Exception e = new Exception();
		DataConversionException dataConversionException = DataConversionException
				.withMessageAndException(message, e);
		assertEquals(dataConversionException.getMessage(), "message");
	}
}
